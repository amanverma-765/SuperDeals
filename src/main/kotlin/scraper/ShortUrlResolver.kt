package com.ark.scraper

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.io.Closeable


class ShortUrlResolver : Closeable {

    private val client = HttpClient(OkHttp) {
        followRedirects = false
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    suspend fun resolveUrl(shortenedUrl: String): String {
        return try {
            val response = client.head(shortenedUrl)
            when (response.status.value) {
                in 300..399 -> {
                    response.headers[HttpHeaders.Location] ?: shortenedUrl
                }

                else -> shortenedUrl
            }
        } catch (e: Exception) {
            throw Exception("Failed to resolve URL: ${e.message}")
        }
    }

    fun getAmazonProductId(longUrl: String): String? {
        // Regex pattern to match the ASIN in the URL
        val regex = """(?:dp|o|gp)/([A-Z0-9]{10})""".toRegex()

        // Find the match and return the first group (ASIN)
        val matchResult = regex.find(longUrl)
        return matchResult?.groups?.get(1)?.value
    }

    override fun close() {
        client.close()
    }
}
