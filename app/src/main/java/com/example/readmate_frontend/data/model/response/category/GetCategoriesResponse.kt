package com.example.readmate_frontend.data.model.response.category

import com.example.readmate_frontend.data.model.response.groups.CategoryItem

data class GetCategoriesResponse(
    val success: Boolean,
    val message: String,
    val data: GetCategoriesData
)

data class GetCategoriesData(
    val categories: List<CategoryItem>
)