package com.raidplan.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Guild : RealmObject() {

    @PrimaryKey
    var name: String? = null
    var server: String? = null

    var roster: RealmList<Character> = RealmList()
}