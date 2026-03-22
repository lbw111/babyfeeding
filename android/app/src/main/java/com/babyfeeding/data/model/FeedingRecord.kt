package com.babyfeeding.data.model

enum class FeedingType {
    BOTTLE,
    SOLID_FOOD
}

enum class MilkType {
    BREAST_MILK,
    FORMULA
}

enum class FoodType {
    RICE_CEREAL,
    FRUIT_PUREE,
    VEGETABLE_PUREE,
    MEAT_PUREE,
    OTHER
}

enum class FoodAmount {
    SMALL,
    MEDIUM,
    LARGE
}

enum class Acceptance {
    LIKED,
    OKAY,
    REFUSED
}

data class FeedingRecord(
    val id: String? = null,
    val babyId: String,
    val type: FeedingType,
    val timestamp: Long,
    val amountMl: Int? = null,
    val milkType: MilkType? = null,
    val foodType: FoodType? = null,
    val foodAmount: FoodAmount? = null,
    val acceptance: Acceptance? = null,
    val note: String? = null,
    val createdBy: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val deleted: Boolean = false
)
