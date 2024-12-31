package com.ark.tgbot

import com.ark.api.OakCatalogManager
import com.ark.domain.model.ProductCatalog
import com.ark.scraper.getAmazonData
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile

fun startBot(manager: OakCatalogManager) {
    val bot = bot {
        token = "8075996051:AAH8O9D9gibkRozbZsPDOR-dIVS7rvZ5p_g"

        dispatch {
            text {
                val firstLine = text.lineSequence().firstOrNull()
                val urlRegex = Regex("https?://[\\w./]+")
                val urls = urlRegex.findAll(text).map { it.value }.toList()

                if (urls.isEmpty()) {
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = "Please enter a correct message")
                } else {
                    urls.forEach { url ->
                        fetchAndBuildTgMessage(manager, url) { msg, imgUrl ->
                            val newMessage = "$firstLine \n $msg"

                            bot.sendPhoto(
                                chatId = ChatId.fromId(message.chat.id),
                                photo = TelegramFile.ByFileId(imgUrl ?: ""),
                                caption = newMessage,
                                parseMode = ParseMode.HTML,
                                disableNotification = false
                            )
                        }
                    }
                }
            }
        }
    }
    bot.startPolling()
}

suspend fun fetchAndBuildTgMessage(
    manager: OakCatalogManager,
    url: String,
    sendData: (String?, String?) -> Unit
) {
    val platform = detectMarketplace(url)
    val data = when (platform) {
        Marketplace.FLIPKART -> null
        Marketplace.AMAZON -> getAmazonData(manager, url)
        Marketplace.OTHER -> null
    }
    if (data == null) {
        sendData("This Marketplace is not implemented yet", null)
        return
    }

    val message = createMessage(data)
    println(data.discountPercent)
    println(data.discount)
    sendData(message, data.imgUrl)
}

private fun detectMarketplace(url: String): Marketplace {
    return when {
        url.contains("amzn") || url.contains("amazon") -> Marketplace.AMAZON
        url.contains("fkrt") || url.contains("flipkart") -> Marketplace.FLIPKART
        else -> Marketplace.OTHER
    }
}

enum class Marketplace {
    FLIPKART,
    AMAZON,
    OTHER
}

private fun createMessage(data: ProductCatalog): String {
    return """
<b>🌟 Product Details 🌟</b>

<b>🛒 Marketplace:</b> ${data.marketPlace.name}
<b>🔗 Product URL:</b> <a href="${data.productUrl}">Click Here</a>

<b>📦 Title:</b> <i>${data.title}</i>
<b>💲 MRP:</b> ₹${data.mrp?.let { "%.2f".format(it) } ?: "N/A"}
<b>💰 Display Price:</b> ₹${data.displayPrice?.let { "%.2f".format(it) } ?: "N/A"}

<b>🔖 Discount:</b> ${data.discountPercent?.let { "%.2f".format(it) } ?: "N/A"}% off
<b>💸 Discount Amount:</b> ₹${data.discount?.let { "%.2f".format(it) } ?: "N/A"}

<b>⭐ Rating:</b> ${data.rating?.let { "%.1f".format(it) } ?: "N/A"} (${data.ratingCount ?: "0"} reviews)
"""
}

