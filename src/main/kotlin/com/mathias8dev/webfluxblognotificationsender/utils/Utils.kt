package com.mathias8dev.webfluxblognotificationsender.utils

import com.mathias8dev.webfluxblognotificationsender.beans.request.ReactiveRequestContextHolder
import korlibs.template.Template
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.jwt.Jwt
import java.nio.file.Files


object Utils {

    private val logger = LoggerFactory.getLogger(Utils::class.java)

    fun baseServerUrl(request: ServerHttpRequest): String {
        return request.uri.scheme + "://" + request.uri.host + ":" + request.uri.port
    }

    fun getTemplate(filepath: String): Template {
        return runBlocking(Dispatchers.IO) {
            val resource = ClassPathResource(filepath)
            logger.debug("Did the file exists ? {}", resource.exists())
            val file = resource.file
            val content = String(Files.readAllBytes(file.toPath()))
            Template(content)
        }
    }

    suspend fun tryGetBaseServerUrl(): String? {
        return ReactiveRequestContextHolder.get()?.let { request ->
            baseServerUrl(request)
        }.otherwise {
            kotlin.runCatching {
                SpringApplicationContext.getBean(Environment::class.java).getProperty("webfluxblog.gateway.baseurl")
            }.getOrNull()
        }
    }


    suspend fun getJwtTokenString(): String? {
        return tryOrNull {
            ReactiveSecurityContextHolder.getContext()
                .map<Any>(SecurityContext::getAuthentication)
                .map {
                    val authentication = it as Authentication
                    getJwt(authentication).tokenValue
                }
                .awaitSingle()
        }
    }


    suspend fun getJwtSubject(): String? {
        return tryOrNull {
            ReactiveSecurityContextHolder.getContext()
                .map<Any>(SecurityContext::getAuthentication)
                .map {
                    val authentication = it as Authentication
                    getJwt(authentication).subject
                }
                .awaitSingle()
        }
    }

    @Throws(IllegalStateException::class)
    private fun getJwt(authentication: Authentication): Jwt {
        logger.debug("The authentication is {}", authentication)
        logger.debug("The principal is Jwt ? {}", authentication.principal is Jwt)
        logger.debug("The credentials is Jwt ? {}", authentication.credentials is Jwt)

        // If the authentication object is JwtAuthenticationToken, then the principal is Jwt and the credentials also
        // Reference org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken
        return (authentication.principal as? Jwt ?: authentication.credentials as? Jwt).otherwise {
            throw IllegalStateException("JWT not found in principal or credentials")
        }
    }

}