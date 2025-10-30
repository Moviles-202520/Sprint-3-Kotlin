package com.example.sprint_2_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprint_2_kotlin.model.data.NewsItem
import com.example.sprint_2_kotlin.model.data.RatingItem
import com.example.sprint_2_kotlin.model.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsItemDetailViewModel(
    private val repository: Repository = Repository()
) : ViewModel() {

    private val _newsItem = MutableStateFlow<NewsItem?>(null)
    val newsItem: StateFlow<NewsItem?> = _newsItem.asStateFlow()

    private val _ratings = MutableStateFlow<List<RatingItem>>(emptyList())
    val ratings: StateFlow<List<RatingItem>> = _ratings.asStateFlow()

    // Keep the old function for backward compatibility if needed
    fun loadNewsItem(newsItem: NewsItem) {
        _newsItem.value = newsItem
        loadRatings(newsItem.news_item_id)
    }

    // 👈 NEW: Load news item by ID
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
}