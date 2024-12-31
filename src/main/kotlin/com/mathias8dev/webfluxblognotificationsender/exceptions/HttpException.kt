package com.mathias8dev.webfluxblognotificationsender.exceptions

import org.springframework.http.HttpStatus

data class HttpException(
    val httpStatus: HttpStatus,
    override val message: String?,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)