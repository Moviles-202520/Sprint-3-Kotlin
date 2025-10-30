package com.example.sprint_2_kotlin.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val category_id: Int = 0,
    val name: String = ""
)
