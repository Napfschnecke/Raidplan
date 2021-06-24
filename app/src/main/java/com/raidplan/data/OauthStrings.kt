package com.raidplan.data

import io.realm.RealmObject

open class OauthStrings : RealmObject() {

    var authCode: String? = null
    var authToken: String? = null
    var authExpiration: Long = 0
}