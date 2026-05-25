package com.example.readmate_frontend.data.model.response.category

import com.example.readmate_frontend.data.model.response.groups.CategoryItem

data class CreateCategoryResponse(
    val success: Boolean,
    val message: String?,
    val data: CategoryItem
)
