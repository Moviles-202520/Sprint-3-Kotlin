package com.example.sprint_2_kotlin.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprint_2_kotlin.model.data.AppDatabase
import com.example.sprint_2_kotlin.model.data.NewsItem
import com.example.sprint_2_kotlin.model.network.NetworkStatusTracker
import com.example.sprint_2_kotlin.model.repository.Repository
import io.ktor.client.utils.EmptyContent
import io.ktor.http.content.MultiPartData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * NewsFeedViewModel with Cache Support
 *
 * CHANGES:
 * - Now extends AndroidViewModel (to pass context to Repository)
 * - Added cache-related states (isLoading, isRefreshing, cacheStatus)
 * - Added refreshNewsFeed() for pull-to-refresh
 * - Uses loadNewsFeedCached() for cache-first strategy
 */
class NewsFeedViewModel(
    application: Application //  CAMBIO: ahora recibe Application
) : AndroidViewModel(application) { //  CAMBIO: extiende AndroidViewModel

    private val dao = AppDatabase.getDatabase(application).CommentDao()
    // CAMBIO: Repository ahora recibe context
    private val repository = Repository(application.applicationContext, dao)

    // EXISTING: News items state
    private val _newsItems = MutableStateFlow<List<NewsItem>>(emptyList())
    val newsItems: StateFlow<List<NewsItem>> = _newsItems

    // ============================================
    // NEW: Cache-related states
    // ============================================

    // Loading state (for first load)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Refreshing state (for pull-to-refresh)
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Cache status (for UI display)
    private val _cacheStatus = MutableStateFlow<String>("")
    val cacheStatus: StateFlow<String> = _cacheStatus

    companion object {
        private const val TAG = "NewsFeedViewModel"
    }

    init {
        loadNewsItems()
    }

    /**
     * UPDATED: Load news items with cache support
     *
     * Strategy:
     * 1. Observe cached data (reactive with Flow)
     * 2. Load fresh data if cache is empty or expired
     */
    private fun loadNewsItems(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (forceRefresh) {
                    _isRefreshing.value = true
                } else {
                    _isLoading.value = true
                }

                Log.d(TAG, "Loading news items - forceRefresh: $forceRefresh")

                // NEW: Observe cached data (reactive)
                val cachedItems = repository.getNewsFeedCached().first().let { cachedItems ->
                    _newsItems.value = cachedItems
                    Log.d(TAG, "News items updated from cache: ${cachedItems.size} items")

                    // Update cache status
                    updateCacheStatus()
                }

                if (_newsItems.value.isEmpty()) {
                    Log.d(TAG, "Cache is empty — fetching from database...")

                    // 2️⃣ Si no hay datos en caché, cargar desde BD o red
                    val dbItems = repository.getNewsItems() // tu función personalizada
                    _newsItems.value = dbItems


                }

            } catch (e: Exception) {
                Log.e(TAG, "Error loading news items", e)
                e.printStackTrace()
                _newsItems.value = emptyList()
            } finally {
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }

        // NEW: Load data with cache strategy (in parallel)
        viewModelScope.launch {
            try {
                repository.loadNewsFeedCached(forceRefresh = forceRefresh)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cached news feed", e)
            }
        }
    }

    /**
     * NEW: Refresh news feed (for pull-to-refresh)
     * Forces fetching fresh data from Supabase
     */
    fun refreshNewsFeed() {
        Log.d(TAG, "Refreshing news feed...")
        loadNewsItems(forceRefresh = true)
    }

    /**
     * NEW: Update cache status for UI display
     */
    private suspend fun updateCacheStatus() {
        try {
            val cachedCount = repository.getCachedItemsCount()
            _cacheStatus.value = if (cachedCount > 0) {
                " Cached: $cachedCount items"
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating cache status", e)
            _cacheStatus.value = ""
        }
    }

    /**
     * NEW: Clear cache manually (optional utility)
     */
    fun clearCache() {
        viewModelScope.launch {
            try {
                repository.clearCache()
                _cacheStatus.value = "Cache cleared"
                // Reload data after clearing
                loadNewsItems(forceRefresh = true)
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing cache", e)
            }
        }
    }

    /**
     * EXISTING: Get category label (unchanged)
     */
    fun getCategoryLabel(categoryId: Int): String = when (categoryId) {
        1 -> "Politics"
        2 -> "Sports"
        3 -> "Science"
        4 -> "Economics"
        5 -> "Business"
        6 -> "Climate"
        7 -> "Conflict"
        8 -> "Local"
        else -> "General"
    }
}