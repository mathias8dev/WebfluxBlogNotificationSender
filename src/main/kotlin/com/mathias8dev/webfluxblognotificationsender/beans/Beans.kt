package com.mathias8dev.webfluxblognotificationsender.beans


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*


@Configuration
class Beans {

    /*
    * Currently, this bean is not needed but since this microservice can evolve and since one can
    * need to change the config based on a user, it can be helpful for further updates
    * */
    @Bean
    fun javaMailSender(
        environment: Environment
    ): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = environment.getProperty("spring.mail.host")!!
        mailSender.port = environment.getProperty("spring.mail.port")!!.toInt()

        mailSender.username = environment.getProperty("spring.mail.username")!!
        mailSender.password = environment.getProperty("spring.mail.password")!!

        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = environment.getProperty("spring.mail.protocol")
        props["mail.smtp.auth"] = environment.getProperty("spring.mail.properties.mail.smtp.auth")!!
        props["mail.smtp.starttls.enable"] = environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable")
        props["mail.smtp.ssl.enable"] = environment.getProperty("spring.mail.properties..mail.smtp.ssl.enable")
        props["mail.debug"] = "true"

        return mailSender
    }


}