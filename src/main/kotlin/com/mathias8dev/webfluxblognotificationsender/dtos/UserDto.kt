package com.mathias8dev.webfluxblognotificationsender.dtos


data class UserDto(
    var firstname: String? = null,
    var lastname: String? = null,
    var telephone: String? = null,
    var email: String? = null,
    var username: String? = null,
    var password: String? = null,
)