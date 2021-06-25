package com.raidplan.ui

import android.os.Bundle
import android.os.Parcelable
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.raidplan.areaSwitch
import com.raidplan.areaSwitchKel
import com.raidplan.data.Bosses
import com.raidplan.roleBlob
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RaidPosArgs(val boss: String? = "") : Parcelable


data class RaidPosState(val boss: String? = "", val phase: Int = 1, val selection: Int = 0) :
    MvRxState {
    constructor(args: RaidPosArgs) : this(args.boss)
}

class RaidPosViewModel(initialState: RaidPosState) : MvRxViewModel<RaidPosState>(initialState) {

    fun switchPhase(phase: Int) {
        setState {
            copy(phase = phase)
        }
    }

    fun switchSelection(sel: Int) {
        setState {
            copy(selection = sel)
        }
    }
}

class RaidPosMvrx(boss: String) : ZoomFragment(boss) {

    private val viewModel: RaidPosViewModel by fragmentViewModel()

    override fun setSelection(sel: Int) {
        selectedIcon = sel
        viewModel.switchSelection(sel)
    }

    override fun updateImage(res: Int) {
        image?.setImageResource(res)
    }

    override fun updateBossArea(boss: String) {
        this.boss = boss
        updateImage(Bosses.getAreaByName(boss))
    }

    override fun epoxyController() = simpleController(viewModel) { state ->

        if ("${state.boss}".contains("Sylvanas Windrunner")) {
            areaSwitch {
                id("switch")
                p1Check(false)
                p2Check(false)
                p3Check(false)
                p4Check(false)
                when (state.phase) {
                    1 -> p1Check(true)
                    2 -> p2Check(true)
                    3 -> p3Check(true)
                    4 -> p4Check(true)

                }
                onClick1 { v ->
                    viewModel.switchPhase(1)
                    updateBossArea("${state.boss?.subSequence(0, state.boss.length - 1)}1")
                }
                onClick2 { v ->
                    viewModel.switchPhase(2)
                    updateBossArea("${state.boss?.subSequence(0, state.boss.length - 1)}2")
                }
                onClick3 { v ->
                    viewModel.switchPhase(3)
                    updateBossArea("${state.boss?.subSequence(0, state.boss.length - 1)}3")
                }
                onClick4 { v ->
                    viewModel.switchPhase(4)
                    updateBossArea("${state.boss?.subSequence(0, state.boss.length - 1)}4")
                }
            }
        } else if ("${state.boss}".contains("Kel'Thuzad")) {
            areaSwitchKel {
                id("switchKel")
                p1Check(false)
                p2Check(false)
                when (state.phase) {
                    1 -> p1Check(true)
                    2 -> p2Check(true)

                }
                onClick1 { v ->
                    viewModel.switchPhase(1)
                    updateBossArea("${state.boss?.subSequence(0, state.boss.length - 1)}1")
                }
                onClick2 { v ->
                    viewModel.switchPhase(2)
                    updateBossArea("${state.boss?.subSequence(0, state.boss.length - 1)}2")
                }
            }
        }

        roleBlob {
            id("roles")
            selectTank(false)
            selectHeal(false)
            selectMelee(false)
            selectRange(false)
            selectBoss(false)
            when (state.selection) {
                1 -> selectTank(true)
                2 -> selectHeal(true)
                3 -> selectMelee(true)
                4 -> selectRange(true)
                5 -> selectBoss(true)
            }
            onClick1 { v ->
                setSelection(1)
            }
            onClick2 { v ->
                setSelection(2)
            }
            onClick3 { v ->
                setSelection(3)
            }
            onClick4 { v ->
                setSelection(4)
            }
            onClick5 { v ->
                setSelection(5)
            }
        }
        /*
        zoom {
            id("area")
            if (state.boss == "Sylvanas Windrunner" || state.boss == "Kel'Thuzad") {
                img(
                    ResourcesCompat.getDrawable(
                        resources,
                        Bosses.getAreaByName("${state.boss}${state.phase}"),
                        context?.theme
                    )
                )
            } else {
                img(
                    ResourcesCompat.getDrawable(
                        resources,
                        Bosses.getAreaByName("${state.boss}"),
                        context?.theme
                    )
                )
            }
        }
         */
    }

    companion object {

        fun newInstance(boss: String): RaidPosMvrx {
            var bossPhaseFix = boss
            if (bossPhaseFix == "Sylvanas Windrunner" || bossPhaseFix == "Kel'Thuzad") {
                bossPhaseFix = "${bossPhaseFix}1"
            }
            val frag = RaidPosMvrx(bossPhaseFix)
            val bundle = Bundle().apply { putParcelable(MvRx.KEY_ARG, RaidPosArgs(bossPhaseFix)) }
            frag.arguments = bundle
            return frag
        }
    }
}

