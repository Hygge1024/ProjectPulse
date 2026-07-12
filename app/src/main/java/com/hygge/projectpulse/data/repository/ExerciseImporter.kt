package com.hygge.projectpulse.data.repository

import android.content.Context
import com.hygge.projectpulse.data.local.entity.ExerciseEntity
import com.hygge.projectpulse.data.remote.model.ExerciseJson
import java.nio.charset.Charset
import javax.inject.Inject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class ExerciseImporter @Inject constructor(
    private val context: Context
) {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun import(): List<ExerciseEntity> {
        val nameZhMap = loadNameZhMap()
        context.assets.open("exercises.json").use { input ->
            val list: List<ExerciseJson> = json.decodeFromStream(input)
            return list.map { item ->
                ExerciseEntity(
                    externalId = item.id,
                    nameEn = item.name,
                    nameZh = nameZhMap[item.id] ?: "",
                    category = item.category,
                    bodyPart = item.bodyPart,
                    target = item.target,
                    muscleGroup = item.muscleGroup,
                    equipment = item.equipment,
                    instructionsEn = (item.instructionSteps["en"] ?: item.instructions["en"]?.split(". "))?.joinToString("\n") ?: "",
                    instructionsZh = (item.instructionSteps["zh"] ?: item.instructions["zh"]?.split(". "))?.joinToString("\n") ?: "",
                    level = "",
                    mediaId = item.mediaId,
                    imagePath = item.image.ifBlank { "images/${item.id}-${item.mediaId}.jpg" },
                    gifPath = item.gifUrl.ifBlank { "videos/${item.id}-${item.mediaId}.gif" }
                )
            }
        }
    }

    private fun loadNameZhMap(): Map<String, String> {
        return try {
            context.assets.open("exercise_names_zh.json").use { input ->
                val text = input.bufferedReader(Charset.forName("UTF-8")).readText()
                json.decodeFromString<Map<String, String>>(text)
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
