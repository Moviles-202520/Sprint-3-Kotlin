package com.example.sprint_2_kotlin.model.data

import kotlinx.serialization.Serializable

@Serializable
data class RatingItem(
    val rating_item_id: Int = 0,
    val news_item_id: Int = 0,
    val user_profile_id: Int = 0,
    val assigned_reliability_score: Double = 0.00,
    val comment_text: String = "",
    val rating_date: String = "",
    val is_completed: Boolean = false
)