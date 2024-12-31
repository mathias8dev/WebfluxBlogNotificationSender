package com.mathias8dev.webfluxblognotificationsender.services

import com.mathias8dev.webfluxblognotificationsender.dtos.UserDto


interface NotificationService {

    suspend fun sendAccountCreated(userDto: UserDto)

    suspend fun sendPasswordResetRequested(userDto: UserDto, passwordResetLink: String)

    suspend fun sendPasswordReset(userDto: UserDto)

    suspend fun sendConfirmEmail(email: String, emailConfirmUrl: String)
}