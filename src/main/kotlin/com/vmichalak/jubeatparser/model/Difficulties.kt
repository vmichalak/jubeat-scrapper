package com.vmichalak.jubeatparser.model

import kotlinx.serialization.Serializable

@Serializable
data class Difficulties (
    val basic: Float,
    val advanced: Float,
    val extreme: Float
)
