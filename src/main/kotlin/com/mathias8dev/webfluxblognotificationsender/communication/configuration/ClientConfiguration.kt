package com.mathias8dev.webfluxblognotificationsender.communication.configuration

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mathias8dev.webfluxblognotificationsender.communication.adapters.gson.InstantTypeAdapter
import com.mathias8dev.webfluxblognotificationsender.communication.adapters.gson.LocalDateTimeTypeAdapter
import com.mathias8dev.webfluxblognotificationsender.communication.adapters.gson.LocalDateTypeAdapter
import com.mathias8dev.webfluxblognotificationsender.communication.adapters.gson.ZonedDateTimeTypeAdapter
import com.mathias8dev.webfluxblognotificationsender.communication.clients.TwilioRestClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime


@Configuration
class ClientConfiguration {

    private var twilioApiUrl: String = "http://localhost:8080"

    @Value("\${webfluxblog.gateway.api-url}")
    private lateinit var gatewayApiUrl: String


    @Bean
    fun okHttpClient(
        okHttpClientBuilder: OkHttpClient.Builder,
    ): OkHttpClient {

        return okHttpClientBuilder
            .build()
    }

    @Bean
    fun okHttpClientBuilder(): OkHttpClient.Builder {
        val cache = Cache(File("cache"), 10 * 1024 * 1024) // 10MB cache file
        val cacheInterceptor = CacheInterceptor()
        val loggingInterceptor = HttpLoggingInterceptor()
        val authInterceptor = AuthInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .addInterceptor(authInterceptor)


    }

    @Bean
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
            .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeTypeAdapter())
            .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create()
    }

    @Bean
    fun gsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }


    @Bean
    fun twilioRestClient(okHttpClient: OkHttpClient): TwilioRestClient {
        return Retrofit.Builder().client(okHttpClient)
            .baseUrl(twilioApiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TwilioRestClient::class.java)
    }

}