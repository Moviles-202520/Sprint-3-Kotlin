package com.example.sprint_2_kotlin.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprint_2_kotlin.model.data.AppDatabase
import com.example.sprint_2_kotlin.model.data.RatingDistributionData
import com.example.sprint_2_kotlin.model.network.NetworkStatusTracker
import com.example.sprint_2_kotlin.model.repository.Repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para Business Question #4
 * Maneja el estado y lógica de distribución de ratings por categoría
 */
class RatingDistributionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).CommentDao()
    // CAMBIO: Repository ahora recibe context
    private val repository = Repository(application.applicationContext, dao)

    // Estados
    var distributionData by mutableStateOf<RatingDistributionData?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadDistributionData()
    }

    /**
     * Cargar datos de distribución desde el repository
     */
    fun loadDistributionData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val result = repository.getRatingDistributionByCategory()

                result.onSuccess { data ->
                    distributionData = data
                    isLoading = false
                }.onFailure { error ->
                    errorMessage = "Error loading data: ${error.message}"
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = "Unexpected error: ${e.message}"
                isLoading = false
            }
        }
    }

    /**
     * Refrescar datos
     */
    fun refresh() {
        loadDistributionData()
    }

    /**
     * Obtener color para categoría (para charts)
     */
    fun getCategoryColor(category: String): androidx.compose.ui.graphics.Color {
        return when (category) {
            "Technology" -> androidx.compose.ui.graphics.Color(0xFF2196F3)
            "Politics" -> androidx.compose.ui.graphics.Color(0xFFE91E63)
            "Health" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
            "Security" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
            "Sports" -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
            else -> androidx.compose.ui.graphics.Color(0xFF607D8B)
        }
    }

    /**
     * Formatear rating para display
     */
    fun formatRating(rating: Double): String {
        return String.format("%.1f", rating)
    }

    /**
     * Obtener emoji para nivel de credibilidad
     */
    fun getCredibilityEmoji(avgVeracity: Double): String {
        return when {
            avgVeracity >= 4.5 -> "🌟" // Excellent
            avgVeracity >= 3.5 -> "✅" // Good
            avgVeracity >= 2.5 -> "⚠️" // Moderate
            else -> "❌" // Poor
        }
    }

    /**
     * Obtener emoji para sesgo político
     */
    fun getBiasEmoji(avgBias: Double): String {
        return when {
            avgBias < -30 -> "⬅️" // Left
            avgBias > 30 -> "➡️" // Right
            else -> "⚖️" // Center
        }
    }
}