package com.raidplan.api

import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.raidplan.MainActivity
import com.raidplan.R
import com.raidplan.data.OauthStrings
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiData {
    companion object {
        const val CLIENT_ID = "b423495a6362420592f311e9ea96ff59"
        const val CLIENT_SECRET = "inVm0NO5hW7XMmrBrqe6GSShvs3kxk7C"
        const val REDIRECT_URI = "https://localhost/"
        const val BASE_URI = "https://eu.api.blizzard.com/"
        const val AUTHORIZE_URI = "https://eu.battle.net/"
        const val TOKEN_URI = "https://eu.battle.net/"

        var AUTH_CODE: String? = null
        var AUTH_TOKEN: String? = null
        var AUTH_EXPIRATON: Long = 0

        fun getAuthToken(act: MainActivity, reauth: Boolean) {
            val tokenService = TokenGenerator.createService(
                TokenService::class.java,
                CLIENT_ID,
                CLIENT_SECRET
            )
            val call = tokenService.getAccessToken(
                REDIRECT_URI,
                "wow.profile",
                "authorization_code",
                "$AUTH_CODE"
            )
            Snackbar.make(
                act.findViewById(R.id.drawer_layout),
                act.resources.getString(R.string.retrieving),
                Snackbar.LENGTH_LONG
            ).apply {
                val lay = view as Snackbar.SnackbarLayout
                val cLay = act.layoutInflater.inflate(R.layout.snackbar_progress, null)
                lay.addView(cLay)

                setBackgroundTint(act.resources.getColor(R.color.colorAccent))
                setTextColor(act.resources.getColor(R.color.black))
                show()
            }


            call.enqueue(object : Callback<AccessToken> {
                override fun onResponse(
                    call: Call<AccessToken>,
                    response: Response<AccessToken>
                ) {
                    Realm.getDefaultInstance().use { realm ->
                        realm.executeTransactionAsync { bgRealm ->
                            bgRealm.where(OauthStrings::class.java).findFirst()?.let { strings ->
                                strings.authToken = response.body()?.access_token
                                strings.authExpiration = System.currentTimeMillis() + 5184000000
                            }
                        }
                    }
                    AUTH_TOKEN = "${response.body()?.access_token}"
                    AUTH_EXPIRATON = System.currentTimeMillis() + 5184000000

                    if (!reauth) DataCrawler.getAccountInfo(act)
                }

                override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                    Log.d("Response", "${t.message}")
                }
            })
        }
    }
}