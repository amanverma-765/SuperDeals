package com.ark.scraper

import com.ark.api.OakCatalogManager
import com.ark.core.domain.ApiResponse
import com.ark.domain.model.MarketPlace
import com.ark.domain.model.ProductCatalog
import com.ark.domain.model.SearchFilter

suspend fun getAmazonData(
    manager: OakCatalogManager,
    url: String
): ProductCatalog? {

    return ShortUrlResolver().use { resolver ->
        try {

            val originalUrl = resolver.resolveUrl(url)
            val asin = resolver.getAmazonProductId(originalUrl)

            asin?.let { id ->
                val productResponse = manager.fetchCatalog(
                    query = id,
                    page = 1,
                    filter = SearchFilter.FEATURED,
                    marketPlaces = listOf(MarketPlace.AMAZON)
                )

                when (productResponse) {
                    is ApiResponse.Error -> return null
                    is ApiResponse.Success -> return productResponse.data.firstOrNull()
                }

            }

        } catch (e: Exception) {
            println("Error: ${e.message}")
            return null
        }
    }

}