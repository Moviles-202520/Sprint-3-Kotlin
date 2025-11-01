package com.example.sprint_2_kotlin.model.data

/**
 * Data class para representar la distribución de ratings por categoría
 * Usado para Business Question #4
 */
data class CategoryRatingDistribution(
    val category: String,
    val avgVeracityRating: Double,
    val avgPoliticalBiasRating: Double,
    val ratingCount: Int,
    val veracity1Star: Int = 0,
    val veracity2Star: Int = 0,
    val veracity3Star: Int = 0,
    val veracity4Star: Int = 0,
    val veracity5Star: Int = 0,
    val biasLeftCount: Int = 0,
    val biasCenterCount: Int = 0,
    val biasRightCount: Int = 0
)

/**
 * Clase para estadísticas generales de ratings
 */
data class RatingStatistics(
    val totalRatings: Int,
    val avgVeracity: Double,
    val avgBias: Double,
    val mostRatedCategory: String,
    val mostCredibleCategory: String,
    val mostBiasedCategory: String
)

/**
 * Wrapper para todos los datos de distribución
 */
data class RatingDistributionData(
    val distributions: List<CategoryRatingDistribution>,
    val statistics: RatingStatistics
)






