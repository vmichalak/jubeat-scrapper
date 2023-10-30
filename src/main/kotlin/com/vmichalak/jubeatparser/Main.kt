package com.vmichalak.jubeatparser

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val tracks = JubeatParser.parseAve(true)
    //val tracks = JubeatParser.parseBeyondTheAve(true)
    val json = Json.encodeToString(tracks)
    File("output/database.json").writeText(json)
    println(json)
    println("${tracks.count()} tracks found")
}
