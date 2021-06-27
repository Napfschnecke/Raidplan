package com.raidplan.ui

import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.raidplan.MainActivity
import com.raidplan.boss
import com.raidplan.data.Bosses
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController

data class BossPickerState(val dummy: String = "") :
    MvRxState

class BossPickerModel(initialState: BossPickerState) :
    MvRxViewModel<BossPickerState>(initialState) {
}

class BossPickerFragment : BaseFragment(3) {

    private val viewModel: BossPickerModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->

        for (i in 0..9) {
            val bossName = Bosses.getBossById(i)
            val bossSplit = bossName.split(" ")
            var bossNameFormatted = ""
            var lengthSinceBreak = 0
            bossSplit.forEach { split ->
                if (split.isNotEmpty() && lengthSinceBreak + split.length <= 10) {
                    bossNameFormatted = "$bossNameFormatted $split"
                    lengthSinceBreak += split.length
                } else if (split.isNotEmpty()) {
                    bossNameFormatted = "$bossNameFormatted\n$split"
                    lengthSinceBreak = 0
                }
            }
            boss {
                id(i)
                title(bossNameFormatted)
                onClick { v ->
                    (activity as MainActivity).openRaidPositioner(bossName)
                }
            }
        }
    }
}