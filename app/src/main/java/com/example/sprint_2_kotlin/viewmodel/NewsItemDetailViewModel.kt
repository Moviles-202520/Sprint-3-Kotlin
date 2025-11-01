package com.example.sprint_2_kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprint_2_kotlin.model.data.AppDatabase
import com.example.sprint_2_kotlin.model.data.NewsItem
import com.example.sprint_2_kotlin.model.data.RatingItem
import com.example.sprint_2_kotlin.model.data.UserProfile
import com.example.sprint_2_kotlin.model.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.NetworkMonitor

/**
 * NewsItemDetailViewModel
 *
 * CHANGE: Now extends AndroidViewModel to pass context to Repository
 */
class NewsItemDetailViewModel(
    application: Application // CAMBIO: ahora recibe Application
) : AndroidViewModel(application) { //  CAMBIO: extiende AndroidViewModel

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> get() = _isConnected
    // CAMBIO: Repository ahora recibe context
    private val dao = AppDatabase.getDatabase(application).CommentDao()
    // CAMBIO: Repository ahora recibe context
    private val repository = Repository(application.applicationContext, dao)
    private val _newsItem = MutableStateFlow<NewsItem?>(null)
    val newsItem: StateFlow<NewsItem?> = _newsItem.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)

    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _ratings = MutableStateFlow<List<RatingItem>>(emptyList())
    val ratings: StateFlow<List<RatingItem>> = _ratings.asStateFlow()

    // Keep the old function for backward compatibility if needed
    fun loadNewsItem(newsItem: NewsItem) {
        _newsItem.value = newsItem
        loadRatings(newsItem.news_item_id)
    }

    // Load news item by ID
    fun loadNewsItemById(newsItemId: Int) {
        viewModelScope.launch {
            try {
                // Fetch the news item from repository
                val item = repository.getNewsItemById(newsItemId)
                _newsItem.value = item

                // Load ratings for this news item
                loadRatings(newsItemId)
            } catch (e: Exception) {
                // Handle error - you might want to add error state
                _newsItem.value = null
            }
        }
    }

    private fun loadRatings(newsItemId: Int) {
        viewModelScope.launch {
            try {
                val ratingsList = repository.getRatingsForNewsItem(newsItemId)
                _ratings.value = ratingsList
            } catch (e: Exception) {
                _ratings.value = emptyList()
            }
        }
    }

    fun addComment(userProfileId: Int, comment:String, newsItemId: Int, rating: Double, onSuccess: () -> Unit, onError: (Throwable) -> Unit)
    {
        viewModelScope.launch {
            try {
             repository.addNewComments(userProfileId,newsItemId, comment = comment, rating = rating, completed = false)
            } catch (e: Exception) {

            }
        }
    }

    fun startSync(networkMonitor: NetworkMonitor) {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { connected ->
                _isConnected.value = connected
                if (connected) repository.syncPendingComments()
            }
        }
    }

    fun startNetworkObserver(networkMonitor: NetworkMonitor) {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { connected ->
                if (connected) {
                    startSync(networkMonitor)
                }
            }
        }
    }
}











