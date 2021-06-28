package com.raidplan.data

import com.raidplan.R

open class Raidmarker {
    companion object {
        fun raidmarkerById(id: Int): Int {

            return when (id) {
                0 -> R.drawable.star
                1 -> R.drawable.moon
                2 -> R.drawable.condom
                3 -> R.drawable.square
                4 -> R.drawable.diamond
                5 -> R.drawable.cross
                6 -> R.drawable.triangle
                7 -> R.drawable.skull
                else -> R.drawable.star
            }
        }

        fun roleBlobById(id: Int): Int {

            return when (id) {
                1 -> R.drawable.tank_blob_selected
                2 -> R.drawable.healer_blob_selected
                3 -> R.drawable.melee_blob_selected
                4 -> R.drawable.range_blob_selected
                5 -> R.drawable.boss_blob_selected
                else -> R.drawable.tank_blob_selected
            }
        }
    }
}