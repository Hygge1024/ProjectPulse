package com.hygge.projectpulse.ui.exercises

import java.util.Locale

fun categoryNameZh(category: String): String = when (category.lowercase(Locale.getDefault())) {
    "upper arms" -> "上臂"
    "lower arms" -> "前臂"
    "upper legs" -> "大腿"
    "lower legs" -> "小腿"
    "waist" -> "腰腹"
    "back" -> "背部"
    "chest" -> "胸部"
    "shoulders" -> "肩部"
    "neck" -> "颈部"
    "cardio" -> "有氧"
    else -> category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun categoryName(category: String): String =
    category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
