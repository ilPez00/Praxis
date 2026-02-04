package com.praxis.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.praxis.app.data.model.FeedbackGrade

/**
 * Grading Dialog
 * Implements the mutual grading system from whitepaper section 3.5
 * Private, bidirectional feedback to recalibrate goal weights
 */
@Composable
fun GradingDialog(
    matchName: String,
    goalName: String,
    onGradeSelected: (FeedbackGrade) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grade Collaboration",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "How did $matchName perform on \"$goalName\"?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Your feedback helps improve future matches. This is private and helps calibrate goal difficulty.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Grade options
                LazyColumn {
                    items(FeedbackGrade.values().toList()) { grade ->
                        GradeOption(
                            grade = grade,
                            description = getGradeDescription(grade),
                            onClick = { onGradeSelected(grade) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GradeOption(
    grade: FeedbackGrade,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = grade.displayName(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun getGradeDescription(grade: FeedbackGrade): String = when (grade) {
    FeedbackGrade.SUCCEEDED -> "Completed the goal successfully and stayed focused"
    FeedbackGrade.TRIED_BUT_FAILED -> "Put in genuine effort but didn't complete it"
    FeedbackGrade.MEDIOCRE -> "Showed up but performance was average"
    FeedbackGrade.TOTAL_NOOB -> "Clearly unprepared or inexperienced"
    FeedbackGrade.DISTRACTED -> "Got sidetracked or didn't stay focused on the goal"
}
