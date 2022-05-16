@file:OptIn(DelicateCoroutinesApi::class)

package io.github.ackuq.scrapers

import io.ktor.server.plugins.BadRequestException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

val scraperContext = newFixedThreadPoolContext(4, "scraper")

@Serializable
data class ScrapedInformation(val title: String, val url: String, val content: List<String>)

interface Scraper {
    val baseURL: String
    suspend fun getPages(): List<ScrapedInformation>
}

object ScraperS : Scraper {
    override val baseURL = "https://www.socialdemokraterna.se/var-politik/a-till-o"

    private fun getContents(url: String): List<String> {
        val doc = Jsoup.connect(url).userAgent("Mozilla").get()
        val content = doc.select("div.sv-text-portlet.sv-use-margins > div.sv-text-portlet-content > ul > li")
        return content.map { it.text() }
    }

    override suspend fun getPages(): List<ScrapedInformation> =
        withContext(scraperContext) {
            val doc = Jsoup.connect(baseURL).userAgent("Mozilla").get()
            val listElements = doc.select("li.active.currentpage > ul > li a[href]")
            val jobs = mutableListOf<Deferred<ScrapedInformation>>()
            for (element in listElements) {
                val title = element.text()
                val url = element.attr("abs:href")
                jobs.add(
                    async {
                        val content = getContents(url)
                        ScrapedInformation(title, url, content)
                    }
                )
            }
            jobs.awaitAll()
        }
}

fun getScraper(abbreviation: String): Scraper {
    when (abbreviation.uppercase()) {
        "S" -> return ScraperS
        else -> throw BadRequestException("No scraper for party with abbreviation $abbreviation")
    }
}
