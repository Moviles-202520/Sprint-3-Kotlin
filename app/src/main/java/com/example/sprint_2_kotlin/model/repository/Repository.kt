package com.example.sprint_2_kotlin.model.repository

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import com.example.sprint_2_kotlin.model.data.NewsItem
import com.example.sprint_2_kotlin.model.data.RatingItem
import com.example.sprint_2_kotlin.model.data.UserProfile
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


class Repository {

    //cliente de supabase
    private val client = createSupabaseClient(
        supabaseUrl = "https://oikdnxujjmkbewdhpyor.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9pa2RueHVqam1rYmV3ZGhweW9yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk0MDU0MjksImV4cCI6MjA3NDk4MTQyOX0.htw3cdc-wFcBjKKPP4aEC9K9xBEnvPULMToP_PIuaLI"
    ) {
        install(Postgrest)
        install(Auth)
    }

    private val auth = client.auth

    //Crear funciones a medida que se van necesitando


    //Funciones de Auth
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


    //Funciones de fetch

    //retornar las primeras 20 filas
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


// Add this method to your Repository class
// Place it after getRatingsForNewsItem() method

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