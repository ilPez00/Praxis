package com.praxis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.praxis.app.data.model.Domain
import com.praxis.app.data.model.GroupMessage
import com.praxis.app.ui.theme.getColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Group chat room screen.
 * Ported from praxis_webapp GroupChatRoom.tsx.
 *
 * Uses local state for messages (real-time Supabase integration is handled
 * by the webapp backend; the Android client will connect via WebSocket in a
 * future sprint).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatRoomScreen(
    groupId: String,
    groupName: String,
    domain: Domain = Domain.FITNESS,
    currentUserName: String = "You",
    onBack: () -> Unit
) {
    val domainColor = domain.getColor()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }

    // Seed with a few mock messages so the chat feels alive
    var messages by remember {
        mutableStateOf(
            listOf(
                GroupMessage(
                    id = "1", groupId = groupId,
                    senderId = "alex", senderName = "Alex",
                    text = "Hey everyone! Ready to push forward this week? 💪",
                    createdAt = Date(System.currentTimeMillis() - 3_600_000)
                ),
                GroupMessage(
                    id = "2", groupId = groupId,
                    senderId = "sam", senderName = "Sam",
                    text = "Always! Hit a new PR yesterday — couldn't be more motivated.",
                    createdAt = Date(System.currentTimeMillis() - 1_800_000)
                ),
                GroupMessage(
                    id = "3", groupId = groupId,
                    senderId = "jordan", senderName = "Jordan",
                    text = "Let's go! Accountability is everything. Checking in daily 🔥",
                    createdAt = Date(System.currentTimeMillis() - 900_000)
                )
            )
        )
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = groupName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Surface(
                            color = domainColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${domain.emoji()} ${domain.displayName()}",
                                fontSize = 11.sp,
                                color = domainColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Message the group…") },
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val canSend = messageText.isNotBlank()
                    IconButton(
                        onClick = {
                            if (canSend) {
                                messages = messages + GroupMessage(
                                    id = UUID.randomUUID().toString(),
                                    groupId = groupId,
                                    senderId = "me",
                                    senderName = currentUserName,
                                    text = messageText.trim(),
                                    createdAt = Date()
                                )
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (canSend) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(24.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (canSend) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                GroupMessageBubble(message = message, isMe = message.senderId == "me")
            }
        }
    }
}

@Composable
private fun GroupMessageBubble(message: GroupMessage, isMe: Boolean) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            if (!isMe) {
                Text(
                    text = message.senderName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (isMe) 16.dp else 4.dp,
                    topEnd = if (isMe) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = if (isMe) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    color = if (isMe) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
            Text(
                text = timeFormat.format(message.createdAt),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}
