package com.mathias8dev.webfluxblognotificationsender.communication.configuration

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class AuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder().build()
//            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}