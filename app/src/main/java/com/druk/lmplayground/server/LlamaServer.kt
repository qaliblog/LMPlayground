package com.druk.lmplayground.server

import android.content.Context
import com.druk.llamacpp.LlamaGenerationCallback
import com.druk.llamacpp.LlamaModel
import com.druk.lmplayground.storage.StoragePreferences
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class LlamaServer(private val context: Context, private val getModel: () -> LlamaModel?) {

    private val storagePreferences = StoragePreferences(context)

    companion object {
        const val DEFAULT_MODEL_NAME = "local-model"
    }

    private val json = Json { ignoreUnknownKeys = true }

    private val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(CORS) {
            anyHost()
            allowMethod(io.ktor.http.HttpMethod.Options)
            allowMethod(io.ktor.http.HttpMethod.Post)
            allowHeader(io.ktor.http.HttpHeaders.ContentType)
            allowHeader(io.ktor.http.HttpHeaders.Authorization)
        }
        routing {
            get("/v1/models") {
                call.respond(mapOf("object" to "list", "data" to listOf(
                    mapOf("id" to DEFAULT_MODEL_NAME, "object" to "model", "created" to 1677610602, "owned_by" to "library")
                )))
            }
            post("/v1/chat/completions") {
                val request = call.receive<ChatCompletionRequest>()
                val model = getModel()
                if (model == null) {
                    call.respond(io.ktor.http.HttpStatusCode.ServiceUnavailable, "Model not loaded")
                    return@post
                }

                val session = model.createSession(
                    n_ctx = storagePreferences.contextLength,
                    n_batch = storagePreferences.batchSize,
                    n_threads = storagePreferences.threads,
                    n_threads_batch = storagePreferences.threads,
                    temp = request.temperature ?: storagePreferences.temperature,
                    top_p = request.top_p ?: storagePreferences.topP,
                    min_p = storagePreferences.minP,
                    top_k = storagePreferences.topK,
                    repeat_penalty = storagePreferences.repeatPenalty
                )
                try {
                    for (message in request.messages) {
                        session.addMessage(message.role, message.content)
                    }

                    if (request.stream) {
                        call.response.header(io.ktor.http.HttpHeaders.ContentType, "text/event-stream")
                        call.response.header(io.ktor.http.HttpHeaders.CacheControl, "no-cache")
                        call.response.header(io.ktor.http.HttpHeaders.Connection, "keep-alive")

                        call.respondTextWriter(contentType = io.ktor.http.ContentType.Text.EventStream) {
                            val channel = Channel<String>(Channel.UNLIMITED)
                            val callback = object : LlamaGenerationCallback {
                                override fun newTokens(newTokens: ByteArray) {
                                    val text = String(newTokens, Charsets.UTF_8)
                                    channel.trySend(text)
                                }
                            }

                            withContext(Dispatchers.Default) {
                                val id = UUID.randomUUID().toString()
                                val created = System.currentTimeMillis() / 1000
                                val modelName = request.model ?: DEFAULT_MODEL_NAME

                                // Generate in a separate thread/coroutine
                                val job = launch {
                                    while (session.generate(callback) == 0) {
                                        // generation continues
                                    }
                                    channel.close()
                                }

                                for (text in channel) {
                                    val chunk = ChatCompletionChunk(
                                        id = id,
                                        created = created,
                                        model = modelName,
                                        choices = listOf(
                                            ChatChoice(
                                                index = 0,
                                                delta = ChatDelta(content = text)
                                            )
                                        )
                                    )
                                    write("data: ${Json.encodeToString(chunk)}\n\n")
                                    flush()
                                }

                                val finalChunk = ChatCompletionChunk(
                                    id = id,
                                    created = created,
                                    model = modelName,
                                    choices = listOf(
                                        ChatChoice(
                                            index = 0,
                                            delta = ChatDelta(),
                                            finish_reason = "stop"
                                        )
                                    )
                                )
                                write("data: ${Json.encodeToString(finalChunk)}\n\n")
                                write("data: [DONE]\n\n")
                                flush()
                                job.join()
                            }
                        }
                    } else {
                        var fullResponse = ""
                        val callback = object : LlamaGenerationCallback {
                            override fun newTokens(newTokens: ByteArray) {
                                fullResponse += String(newTokens, Charsets.UTF_8)
                            }
                        }
                        withContext(Dispatchers.Default) {
                            while (session.generate(callback) == 0) {
                                // generation continues
                            }
                        }

                        val response = ChatCompletionResponse(
                            id = UUID.randomUUID().toString(),
                            created = System.currentTimeMillis() / 1000,
                            model = request.model ?: DEFAULT_MODEL_NAME,
                            choices = listOf(
                                ChatChoice(
                                    index = 0,
                                    message = ChatMessage(role = "assistant", content = fullResponse),
                                    finish_reason = "stop"
                                )
                            )
                        )
                        call.respond(response)
                    }
                } finally {
                    session.destroy()
                }
            }
        }
    }

    fun start() {
        server.start(wait = false)
    }

    fun stop() {
        server.stop(1000, 2000)
    }
}
