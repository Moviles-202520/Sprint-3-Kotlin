package com.example.sprint_2_kotlin.model.repository

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import com.example.sprint_2_kotlin.model.data.NewsItem
import com.example.sprint_2_kotlin.model.data.RatingItem
import com.example.sprint_2_kotlin.model.data.UserProfile
import android.content.Context
import android.util.Log
import com.example.sprint_2_kotlin.model.data.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.result.PostgrestResult
import java.math.RoundingMode

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository with Cache-First Strategy for News Feed
 * Maintains all existing Supabase functionality
 */
class Repository(private val context: Context) {

    // ============================================
    // SUPABASE CLIENT (EXISTING CODE - NO CHANGES)
    // ============================================

    private val client = createSupabaseClient(
        supabaseUrl = "https://oikdnxujjmkbewdhpyor.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9pa2RueHVqam1rYmV3ZGhweW9yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk0MDU0MjksImV4cCI6MjA3NDk4MTQyOX0.htw3cdc-wFcBjKKPP4aEC9K9xBEnvPULMToP_PIuaLI"
    ) {
        install(Postgrest)
        install(Auth)
    }

    private val auth = client.auth

    // ============================================
    // ROOM DATABASE (NEW - FOR CACHING)
    // ============================================

    private val database = AppDatabase.getDatabase(context)
    private val newsItemDao = database.newsItemDao()

    // Cache expiration time: 30 minutes in milliseconds
    private val CACHE_EXPIRATION_TIME = 30 * 60 * 1000L

    companion object {
        private const val TAG = "Repository"
    }

    // ============================================
    // AUTH FUNCTIONS (EXISTING CODE - NO CHANGES)
    // ============================================

    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun signUp(email: String, password: String): Boolean {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }

    suspend fun signOut() {
        auth.signOut()
    }

    // ============================================
    // FETCH FUNCTIONS (EXISTING CODE - NO CHANGES)
    // ============================================

    /**
     * EXISTING: Get news items with pagination (direct from Supabase)
     * This method remains unchanged for compatibility
     */
    suspend fun getNewsItems(pageSize: Int = 20, startRow: Int = 0): List<NewsItem> {
        val response = client.postgrest["news_items"].select {
            range(startRow.toLong(), (startRow + pageSize - 1).toLong())
        }
        return response.decodeList()
    }

    suspend fun getRatingsForNewsItem(newsItemId: Int): List<RatingItem> {
        return try {
            val response = client.postgrest["rating_items"].select {
                filter {
                    eq("news_item_id", newsItemId)
                }
            }
            response.decodeList<RatingItem>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getNewsItemById(newsItemId: Int): NewsItem {
        return try {
            val response = client.postgrest["news_items"].select {
                filter {
                    eq("news_item_id", newsItemId)
                }
            }
            response.decodeSingle<NewsItem>()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to load news item with id: $newsItemId")
        }
    }

    suspend fun addNewComments(
        userProfileId: Int,
        newsItemId: Int,
        comment: String,
        rating: Double,
        completed: Boolean
    ): Any {


        return try {
            val user = client.auth.currentUserOrNull()!!.id
            val response = client
                .from("user_profiles").select(){ filter { eq("user_auth_id",user) } }
            val profiles = response.decodeList<UserProfile>()


            val profile = profiles.first()
            val userProfileId = profile.user_profile_id //  este es el que usarás en tus inserts

            val scaledValue = rating * 100
            val truncatedValue = kotlin.math.floor(scaledValue) // Usa floor para truncar, como en la versión de Python (data-camp.com/es/tutorial/python-round-to-two-decimal-places)
            val ratingf = truncatedValue / 100



            val datos = RatingItem(
                  newsItemId,
                  userProfileId,
                 ratingf,
                 comment,
                 true
            )

            client.from("rating_items").insert(listOf(datos)){}


        } catch (e: Exception) {

        }
    }

    suspend fun updateComment():Any{
        return try {

        } catch (e: Exception){

        }
    }

}
    // ============================================
    // NEW: CACHE-FIRST FUNCTIONS FOR NEWS FEED
    // ============================================

    /**
     * NEW: Get news feed with Cache-First strategy (reactive)
     *
     * This returns a Flow that automatically updates when cache changes
     * Use this in your ViewModel for reactive UI updates
     *
     * @return Flow of cached news items
     */
    fun getNewsFeedCached(): Flow<List<NewsItem>> {
        return newsItemDao.getAllNewsItems().map { cachedEntities ->
            cachedEntities.map { it.toNewsItem() }
        }
    }

    /**
     * NEW: Load news feed with caching logic
     *
     * Strategy:
     * 1. Check if cache has fresh data
     * 2. If yes, use cached data (instant load)
     * 3. If no, fetch from Supabase and cache it
     *
     * @param forceRefresh If true, skip cache and fetch from Supabase (pull-to-refresh)
     * @param pageSize Number of items to fetch
     * @param startRow Starting row for pagination
     */
    suspend fun loadNewsFeedCached(
        forceRefresh: Boolean = false,
        pageSize: Int = 20,
        startRow: Int = 0
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "loadNewsFeedCached - forceRefresh: $forceRefresh")

            // Check if we should use cached data
            if (!forceRefresh && shouldUseCachedData()) {
                Log.d(TAG, "Using cached data (cache is fresh)")
                return@withContext // Data is already in Flow
            }

            // Cache expired or forceRefresh - fetch from Supabase
            Log.d(TAG, "Fetching fresh data from Supabase...")

            val freshNewsItems = getNewsItems(pageSize, startRow)

            if (freshNewsItems.isEmpty()) {
                Log.w(TAG, "No data received from Supabase")
                return@withContext
            }

            // Clear old cache if force refresh
            if (forceRefresh) {
                newsItemDao.deleteAllNewsItems()
                Log.d(TAG, "Cache cleared due to force refresh")
            }

            // Convert to entities and cache them
            val entities = freshNewsItems.map { it.toEntity() }
            newsItemDao.insertAllNewsItems(entities)

            Log.d(TAG, "Successfully cached ${entities.size} news items from Supabase")

        } catch (e: Exception) {
            Log.e(TAG, "Error loading news feed", e)
            // If Supabase fails, cached data will still be available via Flow
        }
    }

    /**
     * NEW: Get a news item by ID with cache fallback
     *
     * 1. Try to get from cache first (fast)
     * 2. If not in cache, fetch from Supabase
     * 3. Cache the fetched item for future use
     */
    suspend fun getNewsItemByIdCached(newsItemId: Int): NewsItem? = withContext(Dispatchers.IO) {
        try {
            // First try cache
            val cachedItem = newsItemDao.getNewsItemById(newsItemId)
            if (cachedItem != null) {
                Log.d(TAG, "News item $newsItemId found in cache")
                return@withContext cachedItem.toNewsItem()
            }

            // Not in cache - fetch from Supabase
            Log.d(TAG, "News item $newsItemId not in cache, fetching from Supabase...")
            val item = getNewsItemById(newsItemId)

            // Cache it for future use
            newsItemDao.insertNewsItem(item.toEntity())

            item
        } catch (e: Exception) {
            Log.e(TAG, "Error getting news item $newsItemId", e)
            null
        }
    }

    /**
     * NEW: Check if cached data is still fresh (not expired)
     */
    private suspend fun shouldUseCachedData(): Boolean {
        val hasCachedData = newsItemDao.hasCachedData()
        if (!hasCachedData) {
            Log.d(TAG, "No cached data available")
            return false
        }

        val cachedItems = newsItemDao.getAllNewsItemsList()
        if (cachedItems.isEmpty()) {
            return false
        }

        // Check if cache is still fresh
        val newestItem = cachedItems.minByOrNull { it.cachedTimestamp }
        val currentTime = System.currentTimeMillis()
        val cacheAge = currentTime - (newestItem?.cachedTimestamp ?: 0)

        val isFresh = cacheAge < CACHE_EXPIRATION_TIME

        Log.d(TAG, "Cache age: ${cacheAge / 1000}s, Fresh: $isFresh")

        return isFresh
    }

    /**
     * NEW: Get cached items count (for analytics/debugging)
     */
    suspend fun getCachedItemsCount(): Int = withContext(Dispatchers.IO) {
        newsItemDao.getCachedItemsCount()
    }

    /**
     * NEW: Manually clear all cached data
     */
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        newsItemDao.deleteAllNewsItems()
        Log.d(TAG, "Cache cleared manually")
    }

    /**
     * NEW: Delete expired cache items (maintenance)
     */
    suspend fun deleteExpiredCache() = withContext(Dispatchers.IO) {
        val expirationTimestamp = System.currentTimeMillis() - CACHE_EXPIRATION_TIME
        newsItemDao.deleteOldCachedItems(expirationTimestamp)
        Log.d(TAG, "Expired cache items deleted")
    }
}
