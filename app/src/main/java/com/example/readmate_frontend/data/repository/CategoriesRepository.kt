package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.BooksApi
import com.example.readmate_frontend.data.api.CategoriesApi
import com.example.readmate_frontend.data.model.request.CreateCategoryRequest
import com.example.readmate_frontend.data.model.response.groups.CategoryItem
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val categoriesApi: CategoriesApi
){
    suspend fun getCategories(groupId: Int): List<CategoryItem> =
        categoriesApi.getCategories(groupId).data.categories

    suspend fun createCategory(groupId: Int, request: CreateCategoryRequest): CategoryItem =
        categoriesApi.createCategory(groupId, request).data

    suspend fun deleteCategory(groupId: Int, categoryId: Int) =
        categoriesApi.deleteCategory(groupId, categoryId)

}
