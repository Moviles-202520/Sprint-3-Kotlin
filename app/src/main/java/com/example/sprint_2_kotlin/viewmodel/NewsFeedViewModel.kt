package com.example.sprint_2_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprint_2_kotlin.model.data.NewsItem
import com.example.sprint_2_kotlin.model.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsFeedViewModel : ViewModel() {

    private val repository = Repository()
    private val _newsItems = MutableStateFlow<List<NewsItem>>(emptyList())
    val newsItems: StateFlow<List<NewsItem>> = _newsItems

    init {
        loadNewsItems()
    }

    private fun loadNewsItems() {
        viewModelScope.launch {
            try {
                _newsItems.value = repository.getNewsItems()
            } catch (e: Exception) {
                e.printStackTrace()
                _newsItems.value = emptyList()
            }
        }
    }

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
