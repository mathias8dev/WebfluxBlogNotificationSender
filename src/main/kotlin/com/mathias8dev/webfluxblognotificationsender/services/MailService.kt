package com.mathias8dev.webfluxblognotificationsender.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.mathias8dev.webfluxblognotificationsender.data.NotificationResource
import com.mathias8dev.webfluxblognotificationsender.data.Strings
import com.mathias8dev.webfluxblognotificationsender.dtos.UserDto
import com.mathias8dev.webfluxblognotificationsender.utils.Utils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service


@Service
class MailService(
    private val javaMailSender: JavaMailSender,
) : NotificationService {


    private fun sendMail(userDto: UserDto, sender: String, subject: String, message: String) =
        MailSenderWrapper.with(javaMailSender)
            .setReceiver(userDto.email!!)
            .setSender(sender, Strings.APP_NAME)
            .setSubject(subject)
            .setText(message, true)
            .wrap()
            .send()

    private fun sendSMS(userDto: UserDto, sender: String, subject: String, message: String) {
    }

    private suspend fun sendNotifications(
        resource: NotificationResource,
        userDto: UserDto,
        templateData: Map<String, *>
    ) {
        coroutineScope {
            val renderedMail = Utils.getTemplate(resource.mailTemplatePath).invoke(templateData)
            val renderedSms = Utils.getTemplate(resource.smsTemplatePath).invoke(templateData)

            launch {
                kotlin.runCatching {
                    sendMail(
                        userDto,
                        resource.sender,
                        resource.subject,
                        renderedMail
                    )
                }.onFailure {
                    //  Make a job to rerun it later
                }
            }

            launch {
                kotlin.runCatching {
                    sendSMS(
                        userDto,
                        resource.sender,
                        resource.subject,
                        renderedSms
                    )
                }.onFailure {
                    // make a job to rerun it later
                }
            }
        }
    }

    override suspend fun sendAccountCreated(userDto: UserDto) {
        sendNotifications(
            resource = NotificationResource.ACCOUNT_CREATED,
            userDto = userDto,
            templateData = ObjectMapper().convertValue(userDto, object : TypeReference<Map<String, Any>>() {})
        )
    }


    override suspend fun sendPasswordResetRequested(userDto: UserDto, passwordResetLink: String) {
        sendNotifications(
            resource = NotificationResource.PASSWORD_RESET_REQUESTED,
            userDto = userDto,
            templateData = mapOf(
                "firstname" to userDto.firstname,
                "lastname" to userDto.lastname,
                "passwordResetUrl" to passwordResetLink,
                "customerSupportEmail" to Strings.CUSTOMER_SUPPORT_EMAIL,
                "customerSupportTelephone" to Strings.CUSTOMER_SUPPORT_TELEPHONE,
            )
        )
    }

    override suspend fun sendPasswordReset(userDto: UserDto) {
        sendNotifications(
            resource = NotificationResource.PASSWORD_RESET,
            userDto = userDto,
            templateData = mapOf(
                "firstname" to userDto.firstname,
                "lastname" to userDto.lastname,
                "customerSupportEmail" to Strings.CUSTOMER_SUPPORT_EMAIL,
                "customerSupportTelephone" to Strings.CUSTOMER_SUPPORT_TELEPHONE,
            )
        )
    }

    override suspend fun sendConfirmEmail(email: String, emailConfirmUrl: String) {
        coroutineScope {
            sendNotifications(
                resource = NotificationResource.ACCOUNT_CONFIRM_EMAIL,
                userDto = UserDto(email = email),
                templateData = mapOf(
                    "emailConfirmUrl" to emailConfirmUrl
                )
            )
        }
    }


}