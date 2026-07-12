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

fun targetNameZh(target: String): String = when (target.lowercase(Locale.getDefault())) {
    "abs" -> "腹肌"
    "pectorals" -> "胸肌"
    "biceps" -> "肱二头肌"
    "triceps" -> "肱三头肌"
    "glutes" -> "臀肌"
    "delts" -> "三角肌"
    "upper back" -> "上背"
    "lats" -> "背阔肌"
    "calves" -> "小腿"
    "quads" -> "股四头肌"
    "forearms" -> "前臂"
    "cardiovascular system" -> "心肺"
    "hamstrings" -> "腘绳肌"
    "spine" -> "脊柱"
    "traps" -> "斜方肌"
    "adductors" -> "内收肌"
    "serratus anterior" -> "前锯肌"
    "abductors" -> "外展肌"
    "levator scapulae" -> "肩胛提肌"
    else -> target.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun equipmentNameZh(equipment: String): String = when (equipment.lowercase(Locale.getDefault())) {
    "body weight" -> "自重"
    "dumbbell" -> "哑铃"
    "cable" -> "绳索"
    "barbell" -> "杠铃"
    "leverage machine" -> "器械"
    "band" -> "弹力带"
    "smith machine" -> "史密斯机"
    "kettlebell" -> "壶铃"
    "weighted" -> "负重"
    "stability ball" -> "瑜伽球"
    "ez barbell" -> "EZ杠"
    "sled machine" -> "雪橇"
    "assisted" -> "辅助"
    "medicine ball" -> "药球"
    "rope" -> "绳索"
    "roller" -> "滚轮"
    "resistance band" -> "阻力带"
    "bosu ball" -> "波速球"
    "wheel roller" -> "健腹轮"
    "olympic barbell" -> "奥运杠铃"
    "upper body ergometer" -> "上肢测功计"
    "trap bar" -> "六角杠"
    "tire" -> "轮胎"
    "stepmill machine" -> "楼梯机"
    "stationary bike" -> "动感单车"
    "skierg machine" -> "滑雪机"
    "hammer" -> "锤子"
    "elliptical machine" -> "椭圆机"
    else -> equipment.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}
