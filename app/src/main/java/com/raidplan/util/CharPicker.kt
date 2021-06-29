package com.raidplan.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.raidplan.MainActivity
import com.raidplan.R
import com.raidplan.api.ApiData
import com.raidplan.api.DataCrawler
import com.raidplan.api.RequestGenerator
import com.raidplan.api.RequestService
import com.raidplan.data.Character
import com.raidplan.data.Guild
import com.raidplan.data.User
import io.realm.Realm
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharPicker {

    fun persistMainChar(char: Character, mainActivity: MainActivity) {
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                val user = realm.where(User::class.java).findFirst()
                val ch = realm.where(Character::class.java).equalTo("name", char.name)
                    .equalTo("server", char.server).findFirst()
                user?.let { u ->
                    u.mainChar = ch
                }
            }
        }
        val requestService = RequestGenerator.createService(
            RequestService::class.java
        )

        val call = requestService.getCharImage(
            "${char.server?.lowercase()}",
            "${char.name?.lowercase()}",
            "eu",
            "profile-eu",
            "${ApiData.AUTH_TOKEN}"
        )

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val assets = response.body()?.get("assets")?.asJsonArray
                val rawObj = assets?.get(3)?.asJsonObject
                val rawUrl = rawObj?.get("value").toString()
                Realm.getDefaultInstance().use {
                    it.executeTransactionAsync { realm ->
                        val ch = realm.where(Character::class.java).equalTo("name", char.name)
                            .equalTo("server", char.server).findFirst()
                        ch?.rawUrl = rawUrl.replace("\"", "")
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
        })
        retrieveGuildInfo(char, mainActivity)

    }

    private fun retrieveGuildInfo(char: Character, activity: MainActivity) {
        val requestService = RequestGenerator.createService(
            RequestService::class.java
        )

        val call = requestService.getCharInfo(
            "${char.server?.lowercase()?.replace(" ", "-")}",
            "${char.name?.lowercase()}",
            "profile-eu",
            "en_gb",
            "${ApiData.AUTH_TOKEN}"
        )

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val guild =
                    response.body()?.getAsJsonObject("guild")?.asJsonObject?.get("name").toString()
                        .replace("\"", "")

                Realm.getDefaultInstance().use {
                    it.executeTransaction { bgRealm ->
                        val user = bgRealm.where(User::class.java).findFirst()
                        val g = bgRealm.where(Guild::class.java).equalTo("name", guild).findFirst()
                        if (g == null) bgRealm.createObject(Guild::class.java, guild)
                        user?.let { u ->
                            u.mainChar?.guild = guild
                        }
                    }
                }
                GlobalScope.launch {
                    DataCrawler.getGuildMembers(guild, "${char.server}", activity)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
        })
    }
}