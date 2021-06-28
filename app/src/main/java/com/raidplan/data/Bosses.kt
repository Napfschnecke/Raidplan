package com.raidplan.data

import com.raidplan.R

open class Bosses {
    companion object {
        fun getBossById(id: Int): String {

            return when (id) {
                0 -> "The Tarragrue"
                1 -> "The Eye of the Jailer"
                2 -> "The Nine"
                3 -> "Remnant of Ner'zhul"
                4 -> "Soulrender Dormazain"
                5 -> "Painsmith Raznal"
                6 -> "Guardian of the First Ones"
                7 -> "Fatescribe Roh-Kalo"
                8 -> "Kel'Thuzad"
                9 -> "Sylvanas Windrunner"
                else -> ""
            }
        }

        fun getBossImageById(id: Int): Int {

            return when (id) {
                0 -> R.drawable.tarragrue_image
                1 -> R.drawable.eye_image
                2 -> R.drawable.nine_image
                3 -> R.drawable.remnant_image
                4 -> R.drawable.soulrender_image
                5 -> R.drawable.painsmith_image
                6 -> R.drawable.guardian_image
                7 -> R.drawable.fatescribe_image
                8 -> R.drawable.kelthuzad_image
                9 -> R.drawable.sylvanas_image
                else -> R.drawable.tarragrue_image
            }
        }

        fun getAreaByName(name: String): Int {

            return when (name) {
                "The Tarragrue" -> R.drawable.tarragrue_area
                "The Eye of the Jailer" -> R.drawable.eye_area
                "The Nine" -> R.drawable.nine_area
                "Remnant of Ner'zhul" -> R.drawable.remnant_area
                "Soulrender Dormazain" -> R.drawable.soulrender_area
                "Painsmith Raznal" -> R.drawable.painsmith_area
                "Guardian of the First Ones" -> R.drawable.guardian_area
                "Fatescribe Roh-Kalo" -> R.drawable.fatescribe_area
                "Kel'Thuzad1" -> R.drawable.kelthuzad_area
                "Kel'Thuzad2" -> R.drawable.kelthuzad_phylactery_area
                "Sylvanas Windrunner1" -> R.drawable.sylvie_p1_area
                "Sylvanas Windrunner2" -> R.drawable.sylvie_chain_single_area
                "Sylvanas Windrunner3" -> R.drawable.sylvie_chain_multiple_area
                "Sylvanas Windrunner4" -> R.drawable.sylvie_p2_area
                else -> R.drawable.tarragrue_area
            }
        }
    }
}