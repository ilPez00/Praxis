package com.praxis.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Message(
    val text: String,
    val isMine: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Chat Screen
 * Goal-focused direct messaging (no feeds, no distractions)
 * Implements the DM channel from whitepaper section 3.5
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    matchName: String,
    sharedGoalName: String,
    onBack: () -> Unit,
    onCompleteCollaboration: () -> Unit
) {
    var messages by remember { 
        mutableStateOf(listOf(
            Message("Hey! Ready to work on $sharedGoalName?", false)
        )) 
    }
    var inputText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(matchName)
                        Text(
                            text = "Working on: $sharedGoalName",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onCompleteCollaboration) {
                        Text("Complete & Grade")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageBubble(message)
                }
            }
            
            // Input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message about $sharedGoalName...") },
                    maxLines = 3
                )
                
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            messages = messages + Message(inputText, true)
                            inputText = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, "Send")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (message.isMine) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                fontSize = 14.sp
            )
        }
    }
}
