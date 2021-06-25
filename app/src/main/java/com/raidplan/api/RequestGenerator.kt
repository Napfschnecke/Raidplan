package com.raidplan.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RequestGenerator {

    val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(ApiData.BASE_URI)
        .addConverterFactory(GsonConverterFactory.create())

    private var retrofit = builder.build()

    fun <S> createService(
        serviceClass: Class<S>?, clientId: String?, clientSecret: String?
    ): S {
        if (!clientId.isNullOrEmpty() && !clientSecret.isNullOrEmpty()) {
            val authToken: String = Credentials.basic(ApiData.CLIENT_ID, ApiData.CLIENT_SECRET)
            return createService(serviceClass, null, null)
        }
        return createService(serviceClass, null, null)
    }

    fun <S> createService(
        serviceClass: Class<S>?
    ): S {
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder().apply {
            //this.addInterceptor(logger)
        }
        builder.client(httpClient.build())
        retrofit = builder.build()

        return retrofit.create(serviceClass!!)
    }
}