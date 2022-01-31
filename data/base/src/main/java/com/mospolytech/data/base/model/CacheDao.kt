package com.mospolytech.data.base.model

import kotlinx.serialization.Serializable
import org.kodein.db.model.orm.Metadata

@Serializable
data class CacheDao(
    override val id: String,
    val value: String
) : Metadata