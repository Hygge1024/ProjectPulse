package com.hygge.projectpulse.ui.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hygge.projectpulse.R
import com.hygge.projectpulse.data.local.entity.ExerciseEntity
import com.hygge.projectpulse.ui.components.ExerciseImage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailSheet(
    exercise: ExerciseEntity,
    onDismiss: () -> Unit,
    onNoteChange: (String) -> Unit
) {
    var note by remember(exercise.id) { mutableStateOf(exercise.userNote) }
    val locale = Locale.getDefault()
    val isZh = locale.language == Locale.CHINESE.language
    val instructions = if (isZh && exercise.instructionsZh.isNotBlank()) exercise.instructionsZh else exercise.instructionsEn
    val steps = instructions.split("\n").filter { it.isNotBlank() }
    val title = exercise.nameZh?.takeIf { it.isNotBlank() } ?: exercise.nameEn

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.fillMaxHeight(0.9f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ExerciseImage(
                        path = exercise.gifPath,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }
            }

            item {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChipTag(text = categoryNameZh(exercise.category))
                    ChipTag(text = targetNameZh(exercise.target))
                    ChipTag(text = equipmentNameZh(exercise.equipment))
                }
            }

            item {
                Text(
                    text = stringResource(R.string.exercise_instructions),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(steps.size) { index ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${index + 1}.",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = steps[index],
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.exercise_note),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("Write your training notes here...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.outline,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            item {
                Button(
                    onClick = {
                        onNoteChange(note)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
private fun ChipTag(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 10.dp)
    )
}
