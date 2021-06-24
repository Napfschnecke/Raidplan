package com.raidplan.data

import com.raidplan.R

open class Classes {
    companion object {
        fun getClassById(id: String?): String {

            return when (id) {
                "1" -> "Warrior"
                "2" -> "Paladin"
                "3" -> "Hunter"
                "4" -> "Rogue"
                "5" -> "Priest"
                "6" -> "Death Knight"
                "7" -> "Shaman"
                "8" -> "Mage"
                "9" -> "Warlock"
                "10" -> "Monk"
                "11" -> "Druid"
                "12" -> "Demon Hunter"
                else -> ""
            }
        }

        fun getClassColor(id: String?): Int {

            return when (id) {
                "1" -> R.color.warrior
                "2" -> R.color.paladin
                "3" -> R.color.hunter
                "4" -> R.color.rogue
                "5" -> R.color.priest
                "6" -> R.color.dk
                "7" -> R.color.shaman
                "8" -> R.color.mage
                "9" -> R.color.warlock
                "10" -> R.color.monk
                "11" -> R.color.druid
                "12" -> R.color.dh

                else -> android.R.color.white
            }
        }
    }
}