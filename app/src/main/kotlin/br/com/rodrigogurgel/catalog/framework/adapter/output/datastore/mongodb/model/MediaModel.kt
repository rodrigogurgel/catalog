package br.com.rodrigogurgel.catalog.framework.adapter.output.datastore.mongodb.model

import org.springframework.data.mongodb.core.mapping.Field

data class MediaModel(
    @Field("url")
    val url: String,
    @Field("type")
    val type: String
)
