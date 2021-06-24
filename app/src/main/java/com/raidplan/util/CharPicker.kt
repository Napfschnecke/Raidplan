package com.raidplan.util

import android.content.Context
import android.provider.ContactsContract
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharPicker {

    open fun buildCharPicker(context: Context) {

        val list = mutableListOf<Character>()

        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                val characters = realm.where(User::class.java).findFirst()?.characters
                characters?.forEach { char ->
                    val unmanaged = realm.copyFromRealm(char)
                    list.add(unmanaged)
                }
            }
        }
        list.apply {
            sortByDescending { it.level }
        }
        list.filter { it.level > 50 }

        val adapter = CustomArrayAdapter(context, R.layout.simple_list_styled, list)

        val builder = MaterialAlertDialogBuilder(context, R.style.MyDialogTheme)

        builder
            .setTitle(R.string.set_character)
            .setAdapter(adapter) { dialog, id ->
                persistMainChar(list[id], context as MainActivity)
            }
            .setCancelable(false)

        builder.create().show()
    }

    private fun persistMainChar(char: Character, mainActivity: MainActivity) {
        Realm.getDefaultInstance().use {
            it.executeTransactionAsync { realm ->
                val user = realm.where(User::class.java).findFirst()
                val ch = realm.where(Character::class.java).equalTo("name", char.name)
                    .equalTo("server", char.server).findFirst()
                user?.let { u ->
                    u.mainChar = ch
                }
            }
        }
        retrieveGuildInfo(char, mainActivity)

    }

    private fun retrieveGuildInfo(char: Character, activity: MainActivity) {
        val requestService = RequestGenerator.createService(
            RequestService::class.java
        )

        val call = requestService.getGuildName(
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
                    it.executeTransactionAsync { bgRealm ->
                        val user = bgRealm.where(User::class.java).findFirst()
                        val ch = bgRealm.where(Character::class.java).equalTo("name", char.name)
                            .equalTo("server", char.server).findFirst()
                        user?.let { u ->
                            u.mainChar?.guild = guild
                        }
                    }
                }
                DataCrawler.getGuildMembers(guild, "${char.server}", activity)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
        })
    }
}