package com.raidplan.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {

    var battleTag: String? = null

    @PrimaryKey
    var accountId: String? = null

    var characters: RealmList<Character> = RealmList()
    var mainChar: Character? = null
    var currentPeriod: Int = 0
    var showTutorial: Boolean = true
    var shouldReset: Boolean = true
}