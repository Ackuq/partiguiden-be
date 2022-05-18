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
import org.jsoup.nodes.Element
import java.io.InvalidClassException

val scraperContext = newFixedThreadPoolContext(4, "scraper")

@Serializable
data class ScrapedInformation(val title: String, val url: String, val content: List<String>, val paragraph: String?)

data class PageInformation(val title: String, val url: String)

interface Scraper {
    val listURL: String
    val listElementSelector: String
    val opinionsTag: String?
        get() = null
    val secondaryOpinionsTag: String?
        get() = null

    // Some specific entries may be corrupted, specify these here
    val skipTitles: List<String>
        get() = emptyList()

    suspend fun getPages(max: Int? = null): List<ScrapedInformation> =
        withContext(scraperContext) {
            val doc = Jsoup.connect(listURL).userAgent("Mozilla").get()
            var listElements = doc.select(listElementSelector).toList()
            if (max != null) {
                listElements = listElements.take(max)
            }
            val jobs = mutableListOf<Deferred<ScrapedInformation>>()
            for (element in listElements) {
                val information = getInformation(element)
                if (
                    skipTitles.contains(information.title) || information.title.isEmpty() || information.url.isEmpty()
                ) {
                    continue
                }
                jobs.add(
                    async {
                        getContents(information, element)
                    }
                )
            }
            jobs.awaitAll()
        }

    /**
     * Get the content for a sub-page.
     */
    suspend fun getContents(information: PageInformation, element: Element): ScrapedInformation {
        val doc = Jsoup.connect(information.url).userAgent("Mozilla").get()
        var content = opinionsTag?.let { doc.select(it) }
            ?: throw InvalidClassException("If using the default getContents method, opinionsTag may not be null")
        if (content.isEmpty() && secondaryOpinionsTag != null) {
            content = doc.select(secondaryOpinionsTag!!)
        }
        return ScrapedInformation(
            title = information.title,
            url = information.url,
            content = content.map { it.text().trim() },
            paragraph = null
        )
    }

    fun getInformation(element: Element): PageInformation = PageInformation(
        title = element.text(),
        url = element.attr("abs:href")
    )
}

object ScraperS : Scraper {
    override val listURL = "https://www.socialdemokraterna.se/var-politik/a-till-o"
    override val listElementSelector = "li.active.currentpage > ul > li a[href]"
    override val opinionsTag = "div.sv-text-portlet.sv-use-margins > div.sv-text-portlet-content > ul > li"
}

object ScraperC : Scraper {
    override val listURL = "https://www.centerpartiet.se/var-politik/politik-a-o"
    override val listElementSelector = ".sol-collapse-decoration.sol-political-area a"
    override val opinionsTag = "p:contains(vill) + ul > li"
    override val secondaryOpinionsTag = "p:contains(anser) + ul > li"
}

object ScraperKD : Scraper {
    override val listURL = "https://kristdemokraterna.se/politik-a-o/"
    override val listElementSelector = ".u-txt-brand"

    override suspend fun getContents(information: PageInformation, element: Element): ScrapedInformation {
        // Unsafe, but we know that there is always a parent
        val text = element.parent()!!.text().replaceFirst(information.title, "").trim()
        // If multiple lines, split
        return ScrapedInformation(
            title = information.title,
            url = information.url,
            content = emptyList(),
            paragraph = text
        )
    }

    override fun getInformation(element: Element): PageInformation =
        PageInformation(title = element.text(), url = listURL)
}

object ScraperL : Scraper {
    override val listURL = "https://www.liberalerna.se/politik-a-o/"
    override val listElementSelector = ".politicsIdx-list-group a"

    override suspend fun getContents(information: PageInformation, element: Element): ScrapedInformation {
        val doc = Jsoup.connect(information.url).userAgent("Mozilla").get()
        // The liberals only have paragraphs, take the first paragraph
        val text = doc.select(".spolitik-content.container > .wysiwyg-content p").first()
        return ScrapedInformation(
            title = information.title,
            url = information.url,
            content = emptyList(),
            paragraph = text?.text()
        )
    }
}

object ScraperMP : Scraper {
    override val listURL = "https://www.mp.se/politik"
    override val listElementSelector = ".questions > div div.question .question-content a"
    override val opinionsTag = "h2:contains(Miljöpartiet vill) + ul li"
}

object ScraperM : Scraper {
    override val listURL = "https://moderaterna.se/var-politik"
    override val listElementSelector = ".search-subjects__content--search__form--list__subjects ul li a"
    override val opinionsTag = ".site-main__article.site-main__entry-content ul li"
    override val secondaryOpinionsTag = ".site-main__article.site-main__entry-content h2:contains(Moderaterna vill)"
    override val skipTitles = listOf("A-kassa")
}

object ScraperSD : Scraper {
    override val listURL = "https://sd.se/a-o/"
    override val listElementSelector = ".post-row.post-type-our-politics"

    override suspend fun getContents(information: PageInformation, element: Element): ScrapedInformation {
        val paragraphElement = element.selectFirst(":nth-child(2)")
        return ScrapedInformation(
            title = information.title,
            url = information.url,
            content = emptyList(),
            paragraph = paragraphElement?.text()
        )
    }

    override fun getInformation(element: Element): PageInformation {
        // SD uses a table for content
        val titleElement = element.selectFirst(":nth-child(1) > a")!!
        return PageInformation(
            title = titleElement.text(),
            url = titleElement.attr("abs:href")
        )
    }
}

object ScraperV : Scraper {
    override val listURL = "https://www.vansterpartiet.se/politik-a-o/"
    override val listElementSelector = ".mo-card__cover-link"
    override val opinionsTag = "p:contains(Vänsterpartiet vill bland annat:) + ul li"
    override val secondaryOpinionsTag = ".or-wysiwyg.or-wysiwyg--article.or-wysiwyg--theme-red-white strong"

    override suspend fun getContents(information: PageInformation, element: Element): ScrapedInformation {
        val doc = Jsoup.connect(information.url).userAgent("Mozilla").get()
        val content = doc.select(opinionsTag)
        val paragraph = doc.selectFirst(secondaryOpinionsTag)

        return ScrapedInformation(
            title = information.title,
            url = information.url,
            content = content.map { it.text() },
            paragraph = paragraph?.text()?.trim()
        )
    }
}

fun getScraper(abbreviation: String): Scraper {
    return when (abbreviation.uppercase()) {
        "S" -> ScraperS
        "C" -> ScraperC
        "KD" -> ScraperKD
        "L" -> ScraperL
        "MP" -> ScraperMP
        "M" -> ScraperM
        "SD" -> ScraperSD
        "V" -> ScraperV
        else -> throw BadRequestException("No scraper for party with abbreviation $abbreviation")
    }
}
