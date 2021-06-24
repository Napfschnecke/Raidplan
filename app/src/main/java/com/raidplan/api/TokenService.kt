package com.raidplan.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface TokenService {

    @FormUrlEncoded
    @POST("oauth/token")
    fun getAccessToken(
        @Field("redirect_uri") redirectUri: String,
        @Field("scope") scope: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String
    ): Call<AccessToken>

    @GET("oauth/userinfo")
    fun getProfile(
        @Query("access_token") token: String
    ): Call<JsonObject>
}
