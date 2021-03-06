package com.raidplan.api

import android.util.Log
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.raidplan.MainActivity
import com.raidplan.data.*
import io.realm.Realm
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class DataCrawler {

    companion object {

        var step = 0.0
        private val requestService = RequestGenerator.createService(
            RequestService::class.java
        )


        /**
         * retrieve accountinfo (id and battletag)
         */
        fun getAccountInfo(act: MainActivity) {
            val accountService = TokenGenerator.createService(
                TokenService::class.java
            )

            val profileCall = accountService.getProfile("${ApiData.AUTH_TOKEN}")

            profileCall.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Realm.getDefaultInstance().use {
                        it.executeTransactionAsync { bgRealm ->
                            if (bgRealm.where(User::class.java).equalTo(
                                    "accountId",
                                    "${response.body()?.get("id")}"
                                ).findFirst() == null
                            ) {
                                val activeUser = bgRealm.createObject(
                                    User::class.java,
                                    "${response.body()?.get("id")}"
                                ).apply {
                                    battleTag =
                                        "${response.body()?.get("battletag")}".replace("\"", "")
                                }
                            }
                        }
                        getAllCharacters(act)
                        //getGuildMembers(act)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
            })
        }

        /**
         * retrieve a list of all characters on the account
         */
        fun getAllCharacters(act: MainActivity) {

            val call = requestService.getCharacters(
                "profile-eu",
                "eu",
                "en_gb",
                "${ApiData.AUTH_TOKEN}"
            )

            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val json = response.body()
                    val l = json?.getAsJsonArray("wow_accounts")
                    var setChar: Character? = null

                    Realm.getDefaultInstance().use { realm ->
                        realm.executeTransactionAsync { bgRealm ->
                            bgRealm.where(User::class.java).findFirst()?.let { user ->
                                user.mainChar?.let { setChar = bgRealm.copyFromRealm(it) }
                                l?.forEach { acc ->
                                    val charArray = acc.asJsonObject?.get("characters")?.asJsonArray
                                    charArray?.forEach { char ->
                                        var ch = bgRealm.where(Character::class.java)
                                            .equalTo(
                                                "id",
                                                "${
                                                    char.asJsonObject["name"].toString().replace(
                                                        "\"",
                                                        ""
                                                    )
                                                }${
                                                    char.asJsonObject["realm"].asJsonObject["name"].toString()
                                                        .replace(
                                                            "\"",
                                                            ""
                                                        )
                                                }"
                                            ).findFirst()
                                        if (ch == null && char.asJsonObject["level"].asInt == 60) {
                                            ch = bgRealm.createObject(
                                                Character::class.java,
                                                "${
                                                    char.asJsonObject["name"].toString().replace(
                                                        "\"",
                                                        ""
                                                    )
                                                }${
                                                    char.asJsonObject["realm"].asJsonObject["name"].toString()
                                                        .replace(
                                                            "\"",
                                                            ""
                                                        )
                                                }"
                                            )
                                            ch.apply {
                                                this.name =
                                                    char.asJsonObject["name"].toString().replace(
                                                        "\"",
                                                        ""
                                                    )
                                                this.server =
                                                    char.asJsonObject["realm"].asJsonObject["name"].toString()
                                                        .replace("\"", "")
                                                level =
                                                    char.asJsonObject["level"].asInt
                                                playerClass =
                                                    char.asJsonObject["playable_class"].asJsonObject["id"].toString()
                                                char.asJsonObject["guild"]?.let {
                                                    this.guild =
                                                        char.asJsonObject["guild"].toString()
                                                            .replace("\"", "")
                                                }
                                            }
                                            user.characters.add(ch)
                                        }
                                    }

                                }
                            }
                        }
                    }
                    act.showCharPicker()

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("CharacterRetrievalError", "${t.message}")
                }
            })
        }

        fun getGuildMembers(guildName: String, server: String, act: MainActivity) {

            var char: Character? = null
            var g: Guild? = null

            Realm.getDefaultInstance().use { realm ->
                realm.executeTransactionAsync { bgRealm ->
                    bgRealm.where(User::class.java).findFirst()?.let { u ->
                        val unmanaged = bgRealm.copyFromRealm(u)
                        char = unmanaged.mainChar
                    }
                }
            }

            val call = requestService.getGuildRoster(
                server.lowercase().replace(" ", "-"),
                guildName.lowercase().replace(" ", "-"),
                "eu",
                "profile-eu",
                "en_gb",
                "${ApiData.AUTH_TOKEN}"

            )

            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    val member = response.body()?.get("members")?.asJsonArray

                    Realm.getDefaultInstance().use { realm ->
                        realm.executeTransactionAsync { r ->
                            g = r.where(Guild::class.java).equalTo("name", guildName)
                                .findFirst()
                            if (g == null) {
                                g = r.createObject(Guild::class.java, guildName)
                            }
                            member?.forEach { m ->
                                val ch = m.asJsonObject["character"] as JsonObject
                                val charClass = ch["playable_class"] as JsonObject
                                var c = r.where(Character::class.java)
                                    .equalTo("name", ch["name"].asString)
                                    .equalTo("server", char?.server)
                                    .findFirst()

                                if (c == null && ch.asJsonObject["level"].asInt == 60) {
                                    c = r.createObject(
                                        Character::class.java,
                                        "${ch.asJsonObject["name"]}${ch.asJsonObject["realm"]}"
                                    )
                                    c.name = ch.asJsonObject["name"].asString
                                    c.guild = g?.name
                                    c.playerClass = charClass["id"].asString
                                    c.server = char?.server
                                    c.guildRank = m.asJsonObject["rank"].asInt
                                    c.level = ch.asJsonObject["level"].asInt
                                } else if (c != null) {
                                    c.guildRank = m.asJsonObject["rank"].asInt
                                }

                                if (c != null && g?.roster?.contains(c) == false) {
                                    g?.roster?.add(c)
                                }
                            }
                            g?.let { gg ->
                                val gu = r.copyFromRealm(gg)
                                getGuildRosterData(gu.roster)
                                act.runOnUiThread {
                                    act.showMainFragment()
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
            })
        }

        fun getCharInfo(server: String, name: String) {
            val call = requestService.getCharInfo(
                server.lowercase()?.replace(" ", "-"),
                name.lowercase(),
                "profile-eu",
                "en_gb",
                "${ApiData.AUTH_TOKEN}"
            )

            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Realm.getDefaultInstance().use {
                        it.executeTransaction { bgRealm ->
                            var ch = bgRealm.where(Character::class.java).equalTo("name", name)
                                .equalTo("server", server).findFirst()
                            response.body()?.asJsonObject?.let { js ->
                                ch?.itemLevel =
                                    js.getAsJsonPrimitive("equipped_item_level").asInt
                                ch?.itemLevelMax =
                                    js.getAsJsonPrimitive("average_item_level").asInt
                                ch?.activeSpec =
                                    js.getAsJsonObject("active_spec")
                                        ?.getAsJsonPrimitive("name")?.asString
                                if (Classes.getRoleByClass(ch?.playerClass) != "") {
                                    ch?.role = Classes.getRoleByClass(ch?.playerClass)
                                } else {
                                    ch?.role = Classes.getRoleBySpec(ch?.activeSpec)
                                }
                                val covInfo = js.getAsJsonObject("covenant_progress")
                                ch?.covenant =
                                    covInfo.getAsJsonObject("chosen_covenant")
                                        ?.getAsJsonPrimitive("name")?.asString
                                ch?.renown =
                                    covInfo.getAsJsonPrimitive("renown_level").asInt
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
            })
        }

        fun getGuildRosterData(
            roster: List<Character>
        ) {
            step = 1000.0 / roster.size.toDouble()
            GlobalScope.launch {
                var i = 0
                roster.forEach {
                    getCharInfo("${it.server}", "${it.name}")
                    i++
                    if (i % 10 == 0) {
                        Thread.sleep(100)
                    }
                }
            }
        }

        fun getPvpRatings(act: MainActivity, char: Character, progressBar: ProgressBar?) {

            val call = requestService.getPvpRatings(
                "${char.server?.toLowerCase()}",
                "${char.name?.toLowerCase()}",
                "2v2",
                "eu",
                "profile-eu",
                "en_gb",
                "${ApiData.AUTH_TOKEN}"
            )

            val call2 = requestService.getPvpRatings(
                "${char.server?.toLowerCase()}",
                "${char.name?.toLowerCase()}",
                "3v3",
                "eu",
                "profile-eu",
                "en_gb",
                "${ApiData.AUTH_TOKEN}"
            )

            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    if (response.code() == 401 || response.code() == 403 || response.code() == 404) {
                        progressBar?.let {
                            it.progress = (it.progress + step).toInt()
                            if (it.progress >= 990) {
                                it.isVisible = false
                            }
                        }
                        get3s(call2, progressBar, act, char)
                        getReputations(act, char, progressBar)
                        act.reAuthorize(response.code())
                        return
                    } else {
                        val rating = response.body()?.asJsonObject?.get("rating")?.asInt
                        Realm.getDefaultInstance().use {
                            var c: Character?
                            it.executeTransaction {
                                c = it.where(Character::class.java)
                                    .equalTo("name", char.name)
                                    .equalTo("server", char.server).findFirst()
                                rating?.let { r -> c?.arena2s = r }
                            }
                        }
                        get3s(call2, progressBar, act, char)
                        getReputations(act, char, progressBar)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                }
            })

        }

        fun get3s(
            call2: Call<JsonObject>,
            progressBar: ProgressBar?,
            act: MainActivity,
            char: Character
        ) {
            call2.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.code() == 401 || response.code() == 403 || response.code() == 404) {
                        progressBar?.let {
                            it.progress = (it.progress + step).toInt()
                            if (it.progress >= 990) {
                                it.isVisible = false
                            }
                        }
                        act.reAuthorize(response.code())
                        return
                    } else {
                        val rating3s =
                            response.body()?.asJsonObject?.get("rating")?.asInt
                        var c: Character?
                        Realm.getDefaultInstance().use {
                            it.executeTransaction {
                                c = it.where(Character::class.java)
                                    .equalTo("name", char.name)
                                    .equalTo("server", char.server).findFirst()
                                rating3s?.let { r -> c?.arena3s = r }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
            })
        }

        fun getReputations(act: MainActivity, char: Character, progressBar: ProgressBar?) {

            val call = requestService.getReputations(
                "${char.server?.toLowerCase()}",
                "${char.name?.toLowerCase()}",
                "eu",
                "profile-eu",
                "en_gb",
                "${ApiData.AUTH_TOKEN}"
            )

            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.code() == 401 || response.code() == 403 || response.code() == 404) {
                        progressBar?.let {
                            it.progress = (it.progress + step).toInt()
                            if (it.progress >= 990) {
                                it.isVisible = false
                            }
                        }
                        act.reAuthorize(response.code())
                        return
                    } else {
                        val reps = response.body()?.asJsonObject?.get("reputations")?.asJsonArray
                        reps?.forEach {
                            val rep = it.asJsonObject
                            val faction = rep["faction"].asJsonObject
                            val facId = faction.get("id").asInt

                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
            })

        }
    }
}