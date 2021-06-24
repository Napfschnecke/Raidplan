package com.raidplan.data

import com.raidplan.R
import io.realm.RealmList
import io.realm.RealmObject

open class Dungeon : RealmObject() {

    var name: String? = null
    var timed = false
    var duration: String? = null
    var level: String? = null


    fun getAbbr(title: String): String {

        return when (title) {
            "Freehold" -> "FH"
            "Atal'dazar" -> "AD"
            "Kings' Rest" -> "KR"
            "The MOTHERLODE!!" -> "ML"
            "Siege of Boralus" -> "SOB"
            "Shrine of the Storm" -> "SOTS"
            "Tol Dagor" -> "TD"
            "Temple of Sethraliss" -> "TOS"
            "The Underrot" -> "UR"
            "Waycrest Manor" -> "WM"
            "Operation: Mechagon - Junkyard" -> "YARD"
            "Operation: Mechagon - Workshop" -> "WORK"
            else -> ""
        }
    }

    fun getImgId(title: String?): Int {

        return when (title) {
            "" -> 1
            else -> 0
        }
    }
}