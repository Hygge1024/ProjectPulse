package com.hygge.projectpulse.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseJson(
    val id: String,
    val name: String,
    val category: String,
    @SerialName("body_part")
    val bodyPart: String,
    val equipment: String,
    val instructions: Map<String, String>,
    @SerialName("instruction_steps")
    val instructionSteps: Map<String, List<String>> = emptyMap(),
    @SerialName("muscle_group")
    val muscleGroup: String,
    @SerialName("secondary_muscles")
    val secondaryMuscles: List<String> = emptyList(),
    val target: String,
    val image: String = "",
    @SerialName("gif_url")
    val gifUrl: String = "",
    @SerialName("media_id")
    val mediaId: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    val attribution: String = ""
)
