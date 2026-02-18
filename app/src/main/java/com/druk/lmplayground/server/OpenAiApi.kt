package com.druk.lmplayground.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String? = null,
    val messages: List<ChatMessage>,
    val stream: Boolean = false,
    val temperature: Float? = null,
    val max_tokens: Int? = null,
    val top_p: Float? = null,
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
)

@Serializable
data class ChatCompletionResponse(
    val id: String,
    @SerialName("object") val objectType: String = "chat.completion",
    val created: Long,
    val model: String,
    val choices: List<ChatChoice>,
    val usage: ChatUsage? = null,
)

@Serializable
data class ChatChoice(
    val index: Int,
    val message: ChatMessage? = null,
    val delta: ChatDelta? = null,
    val finish_reason: String? = null,
)

@Serializable
data class ChatDelta(
    val role: String? = null,
    val content: String? = null,
)

@Serializable
data class ChatUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
)

@Serializable
data class ChatCompletionChunk(
    val id: String,
    @SerialName("object") val objectType: String = "chat.completion.chunk",
    val created: Long,
    val model: String,
    val choices: List<ChatChoice>,
)
