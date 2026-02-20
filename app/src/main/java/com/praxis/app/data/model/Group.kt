package com.praxis.app.data.model

import java.util.Date
import java.util.UUID

/**
 * A community group room, domain-focused.
 * Ported from praxis_webapp GroupsPage.tsx / GroupChatRoom.tsx
 */
data class Group(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val domain: Domain,
    val creatorId: String = "",
    val memberCount: Int = 0,
    val isJoined: Boolean = false
)

/**
 * A message sent inside a group chat room.
 */
data class GroupMessage(
    val id: String = UUID.randomUUID().toString(),
    val groupId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val createdAt: Date = Date()
)
