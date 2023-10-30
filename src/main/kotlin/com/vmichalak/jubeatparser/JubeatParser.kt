package com.vmichalak.jubeatparser

import com.vmichalak.jubeatparser.model.Difficulties
import com.vmichalak.jubeatparser.model.Track
import org.jsoup.Connection.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File
import java.io.FileOutputStream

object JubeatParser {
    private const val BASE_URL = "https://p.eagate.573.jp"
    private val AVE_URL = listOf(
        "/game/jubeat/ave/music/index.html",
        "/game/jubeat/ave/music/original.html"
    )
    private val BEYOND_URL = listOf(
        "/game/jubeat/beyond/music/index.html",
        "/game/jubeat/beyond/music/original.html"
    )

    fun parseAve(downloadJacket: Boolean): List<Track> =
        AVE_URL.flatMap { parsePages(BASE_URL + it, downloadJacket) }

    fun parseBeyondTheAve(downloadJacket: Boolean): List<Track> =
        BEYOND_URL.flatMap { parsePages(BASE_URL + it, downloadJacket) }

    private fun parsePages(baseUrl: String, downloadJacket: Boolean): List<Track> {
        var firstTrack: Track? = null
        var page: Int = 1
        val tracks = mutableListOf<Track>()
        while (true) {
            val url = forgeUrl(baseUrl, page)
            val pageTracks = parsePage(url, downloadJacket)
            // The website returns the first page if you exceed the maximum page.
            // We add an exit condition on the detection of the first track.
            if (firstTrack == null) {
                firstTrack = pageTracks.first()
            } else if (pageTracks.first() == firstTrack) {
                break
            }
            tracks.addAll(pageTracks)
            page++
        }
        return tracks
    }

    private fun parsePage(url: String, downloadJacket: Boolean): List<Track> {
        val document: Document = get(url)
        val data: Elements = document.select(".list_data")
        return data.map {
            val jacketUrl = it.select("p > img").attr("src")
            val id = fromJacketUrlToId(jacketUrl).toInt()
            val title = it.select("ul > li:nth-child(1)").text()
            val artist = it.select("ul > li:nth-child(2)").text()
            val basic = it.select("ul > li:nth-child(3) > ul > li:nth-child(2)").text().toFloat()
            val advanced = it.select("ul > li:nth-child(3) > ul > li:nth-child(4)").text().toFloat()
            val extreme = it.select("ul > li:nth-child(3) > ul > li:nth-child(6)").text().toFloat()
            if(downloadJacket) {
                downloadJacket(BASE_URL + jacketUrl, id)
            }
            Track(
                id = id,
                title = title,
                artist = artist,
                difficulties = Difficulties(
                    basic = basic,
                    advanced = advanced,
                    extreme = extreme
                )
            )
        }
    }

    private fun forgeUrl(baseUrl: String, page: Int): String =
        when (page > 1) {
            true -> "$baseUrl?page=$page"
            false -> baseUrl
        }

    private fun fromJacketUrlToId(url: String): String =
        url.substring(url.indexOf("/id")+3).removeSuffix(".gif")

    private fun get(url: String): Document =
        Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0")
            .timeout(60_000)
            .get()

    private fun downloadJacket(url: String, id: Int) {
        val file = File("output/jacket/$id.gif")
        if(file.exists()) {
           return
        }
        val resultImageResponse: Response = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0")
            .ignoreContentType(true)
            .execute()
        val out = FileOutputStream(file)
        out.write(resultImageResponse.bodyAsBytes())
        out.close()
    }
}
