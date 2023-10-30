package com.vmichalak.jubeatparser.model

import kotlinx.serialization.Serializable

@Serializable
data class Track (
    val id: Int,
    val title: String,
    val artist: String,
    val difficulties: Difficulties
)
