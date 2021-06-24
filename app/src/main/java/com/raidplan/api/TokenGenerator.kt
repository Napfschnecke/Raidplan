package com.raidplan.api

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object TokenGenerator {

    val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(ApiData.TOKEN_URI)
        .addConverterFactory(GsonConverterFactory.create())

    private var retrofit = builder.build()

    fun <S> createService(serviceClass: Class<S>?): S {
        return createService(serviceClass, null)
    }

    fun <S> createService(
        serviceClass: Class<S>?, clientId: String?, clientSecret: String?
    ): S {
        if (!clientId.isNullOrEmpty() && !clientSecret.isNullOrEmpty()) {
            val authToken: String = Credentials.basic(ApiData.CLIENT_ID, ApiData.CLIENT_SECRET)
            return createService(serviceClass, authToken)
        }
        return createService(serviceClass, null, null)
    }

    fun <S> createService(
        serviceClass: Class<S>?, authToken: String?
    ): S {
        authToken?.let { token ->
            val httpClient: OkHttpClient.Builder = OkHttpClient.Builder().apply {
                this.addInterceptor(RequestGenerator.logger)
                this.addInterceptor(AuthenticationInterceptor(token))
            }
            builder.client(httpClient.build())
            retrofit = builder.build()
        }

        return retrofit.create(serviceClass!!)
    }
}