package com.example.readmate_frontend.data.model.response.groups

data class GroupHomeResponse(
    val success: Boolean,
    val message: String?,
    val data: GroupHomeData
)

data class GroupHomeData(
    val groupId: Int,
    val name: String,
    val photoUrl: String,
    val memberCount: Int,
    val categories: List<CategoryItem>
)

enum class ReadingMode {
    NORMAL, EXCHANGE
}

enum class CategoryStatus {
    ONGOING, COMPLETED
}
data class CategoryItem(
    val categoryId: Int,
    val name: String,
    val bookTitle: String?,
    val bookAuthor: String?,
    val readingMode: ReadingMode,
    val status: CategoryStatus,
    val pagesPerRound: Int,
    val daysPerRound: Int
)