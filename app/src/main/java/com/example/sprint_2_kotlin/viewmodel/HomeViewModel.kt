package com.example.sprint_2_kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprint_2_kotlin.model.network.NetworkStatusTracker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tracker = NetworkStatusTracker(application.applicationContext)

    val isConnected: StateFlow<Boolean> = tracker.isConnected.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )
}