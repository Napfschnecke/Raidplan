package com.raidplan.data

import android.graphics.drawable.Drawable
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

        fun getRoleBySpec(spec: String?): String {

            return when (spec) {
                "Guardian" -> "Tank"
                "Protection" -> "Tank"
                "Vengeance" -> "Tank"
                "Brewmaster" -> "Tank"
                "Blood" -> "Tank"

                "Windwalker" -> "Melee"
                "Subtlety" -> "Melee"
                "Assassination" -> "Melee"
                "Outlaw" -> "Melee"
                "Unholy" -> "Melee"
                "Frost" -> "Melee"
                "Fury" -> "Melee"
                "Arms" -> "Melee"
                "Enhancer" -> "Melee"
                "Survival" -> "Melee"
                "Feral" -> "Melee"
                "Havoc" -> "Melee"
                "Retribution" -> "Melee"

                "Elemental" -> "Range"
                "Beast Mastery" -> "Range"
                "Marksmanship" -> "Range"
                "Balance" -> "Range"
                "Shadow" -> "Range"

                "Holy" -> "Healer"
                "Discipline" -> "Healer"
                "Restoration" -> "Healer"
                "Mistweaver" -> "Melee"

                else -> ""
            }
        }

        fun getRoleByClass(c: String?): String {
            return when (c) {
                "8" -> "Range"
                "9" -> "Range"
                "4" -> "Melee"
                else -> ""
            }
        }

        fun getIconByRole(role: String): Int {
            return when (role) {
                "Range" -> R.drawable.ic_range
                "Melee" -> R.drawable.ic_melee
                "Tank" -> R.drawable.ic_tank
                "Healer" -> R.drawable.ic_healer
                else -> R.drawable.ic_tank
            }
        }

        fun getCovenantIcon(cov: String?): Int {
            return when (cov) {
                "Night Fae" -> R.drawable.nightfae_2
                "Venthyr" -> R.drawable.venthyr_2
                "Necrolord" -> R.drawable.necro_2
                "Kyrian" -> R.drawable.kyrian_2
                else -> R.drawable.nightfae
            }
        }
    }
}