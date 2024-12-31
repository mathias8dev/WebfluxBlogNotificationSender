package com.mathias8dev.webfluxblognotificationsender.controllers

import com.mathias8dev.webfluxblognotificationsender.beans.gateway.GatewayContextHolder
import com.mathias8dev.webfluxblognotificationsender.beans.request.ReactiveRequestContextHolder
import com.mathias8dev.webfluxblognotificationsender.dtos.UserDto
import com.mathias8dev.webfluxblognotificationsender.services.MailService
import com.mathias8dev.webfluxblognotificationsender.utils.Utils
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/notifications/notify")
//@PreAuthorize("hasAnyAuthority('SCOPE_NOTIFY', 'ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CONTRIBUTOR')")
class NotificationController(
    private val mailService: MailService,
    environment: Environment
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("The mail username is ${environment.get("spring.mail.username")}")
        logger.debug("The mail password is ${environment.get("spring.mail.password")}")
    }

    @GetMapping("/test")
    suspend fun testContextHolder(): Map<String, *> {
        val gatewayContext = GatewayContextHolder.get()
        val requestContext = ReactiveRequestContextHolder.get()
        val jwt = Utils.getJwtTokenString()
        val subject = Utils.getJwtSubject()

        return mapOf(
            "gatewayContext" to gatewayContext,
            "requestContextUri" to requestContext?.uri,
            "inferredBasedUrl" to Utils.tryGetBaseServerUrl(),
            "jwt" to jwt,
            "subject" to subject,
        )
    }


    @PostMapping("/account-created")
    suspend fun sendAccountCreated(@RequestPart userDto: UserDto) {
        logger.debug("Sending account created")
        logger.debug("The userDto is {}", userDto)
        return mailService.sendAccountCreated(userDto)
    }

    @PostMapping("/request-password-reset")
    suspend fun sendPasswordResetRequested(@RequestPart userDto: UserDto, @RequestPart passwordResetLink: String) {
        return mailService.sendPasswordResetRequested(userDto, passwordResetLink)
    }

    @PostMapping("/password-reset")
    suspend fun sendPasswordReset(@RequestPart userDto: UserDto) {
        return mailService.sendPasswordReset(userDto)
    }


    @PostMapping("/confirm-email")
    suspend fun sendConfirmEmail(@RequestPart email: String, @RequestPart emailConfirmUrl: String) {
        return mailService.sendConfirmEmail(email, emailConfirmUrl)
    }
}