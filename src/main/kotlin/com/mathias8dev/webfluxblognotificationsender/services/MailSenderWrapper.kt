package com.mathias8dev.webfluxblognotificationsender.services

import jakarta.mail.internet.MimeMessage
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import java.io.File


class MailSenderWrapper {


    companion object {
        fun with(javaMailSender: JavaMailSender) =
            ParamsBuilder(javaMailSender)
    }


    class ParamsBuilder internal constructor(
        private val javaMailSender: JavaMailSender,
    ) {
        private val message = javaMailSender.createMimeMessage()
        private val helper = MimeMessageHelper(message, true)

        fun setReceiver(receiver: String): ParamsBuilder {
            helper.setTo(receiver)
            return this
        }

        fun setSender(address: String, personal: String = address): ParamsBuilder {
            helper.setFrom(address, personal)
            return this
        }

        fun setSubject(subject: String): ParamsBuilder {
            helper.setSubject(subject)
            return this
        }

        fun setText(text: String, isHtml: Boolean = false): ParamsBuilder {
            helper.setText(text, isHtml)
            return this
        }

        fun attach(partName: String, partPath: String): ParamsBuilder {
            val file = FileSystemResource(File(partPath))
            helper.addAttachment(partName, file)
            return this
        }

        fun attach(part: File, partName: String = part.name): ParamsBuilder {
            val file = FileSystemResource(part)
            helper.addAttachment(partName, file)
            return this
        }

        fun wrap() = Sender(javaMailSender, message)
    }

    class Sender internal constructor(
        private val javaMailSender: JavaMailSender,
        private val message: MimeMessage
    ) {


        @Throws(MailException::class)
        fun send() {
            javaMailSender.send(message)
        }
    }

}