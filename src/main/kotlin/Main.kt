package com.ark

import com.ark.api.OakCatalogManager
import com.ark.koin.OakInitialiser
import com.ark.tgbot.startBot

fun main() {

    OakInitialiser.initOak()
    val manager = OakCatalogManager()

    startBot(manager)
}