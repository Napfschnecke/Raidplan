package com.raidplan.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RequestService {

    @GET("profile/user/wow")
    fun getCharacters(
        @Query("namespace") namespace: String,
        @Query(":region") region: String,
        @Query("locale") locale: String,
        @Query("access_token") token: String
    ): Call<JsonObject>

    @GET("profile/user/wow/protected-character/{realmId}-{characterId}")
    fun getCharacterProtected(
        @Path("realmId") realmId: Int,
        @Path("characterId") characterId: Int,
        @Query(":region") region: String,
        @Query("namespace") namespace: String,
        @Query("locale") locale: String,
        @Query("access_token") token: String
    ): Call<JsonObject>

    @GET("profile/wow/character/{realm}/{name}/mythic-keystone-profile")
    fun getKeystoneProfile(
        @Path("realm") realm: String,
        @Path("name") name: String,
        @Query("namespace") namespace: String,
        @Query("locale") locale: String,
        @Query("access_token") token: String
    ): Call<JsonObject>

    @GET("/data/wow/guild/{realm}/{name}/roster")
    fun getGuildRoster(
        @Path("realm") realm: String,
        @Path("name") name: String,
        @Query(":region") region: String,
        @Query("namespace") namespace: String,
        @Query("locale") locale: String,
        @Query("access_token") token: String
    ): Call<JsonObject>

    @GET("/data/wow/mythic-keystone/period/{id}")
    fun getKeyPeriod(
        @Path("id") id: String,
        @Query(":region") region: String,
        @Query("namespace") namespace: String,
        @Query("locale") locale: String,
        @Query("access_token") token: String
    ): Call<JsonObject>

    @GET("/profile/wow/character/{realm}/{name}/pvp-bracket/{bracket}")
    fun getPvpRatings(
        @Path("realm") realm: String,
        @Path("name") name: String,
        @Path("bracket") bracket: String,
        @Query(":region") region: String,
        @Query("namespace") namespace: String,
        @Query("locale") locale: String,
        @Query("access_token") token: String
    ): Call<JsonObject>

    @GET("/profile/wow/character/{realm}/{name}/reputations")
    fun getReputations(
        @Path("realm") realm: String,
        @Path("name") name: String,
        @Query(":region") region: String,
        @Query("namespace") namespace: String,
        @Query("locale") locale: String,
        @Query("access_token") token: String
    ): Call<JsonObject>

    @GET("profile/wow/character/{realm-slug}/{name}")
    fun getGuildName(
        @Path("realm-slug") realmSlug: String,
        @Path("name") name: String,
        @Query("namespace") namespace: String,
        @Query("locale") locale: String,
        @Query("access_token") token: String
    ): Call<JsonObject>
}
