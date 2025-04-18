package br.com.rodrigogurgel.catalog.domain.exception

data class MalformedURLException(private val path: String) :
    IllegalArgumentException(
        "The medias path is invalid: '$path'. Please verify that the path is correct and accessible."
    )
