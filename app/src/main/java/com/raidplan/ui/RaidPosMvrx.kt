package com.raidplan.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.raidplan.*
import com.raidplan.data.Bosses
import com.raidplan.data.Raidmarker
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RaidPosArgs(val boss: String? = "") : Parcelable


data class RaidPosState(
    val boss: String? = "",
    val phase: Int = 1,
    val selection: Int = 0,
    val toolbar: Int = 0,
    val marker: Int = -1
) :
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

    fun switchMarker(sel: Int) {
        setState {
            copy(marker = sel)
        }
    }

    fun switchToolbar(newPos: Int) {
        setState {
            copy(
                toolbar = newPos
            )
        }
    }
}

class RaidPosMvrx(boss: String) : ZoomFragment(boss) {

    private val viewModel: RaidPosViewModel by fragmentViewModel()

    override fun setSelection(sel: Int) {
        selectedIcon = sel
        viewModel.switchSelection(sel)
        attacher.selectedIcon = sel
    }

    override fun setMarker(sel: Int) {
        selectedMarker = sel
        viewModel.switchMarker(sel - 1)
        attacher.selectedMarker = sel
    }

    override fun updateImage(res: Int) {
        image?.setImageResource(res)
        attacher.clearIcons()
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
    }

    override fun secondEpoxyController() = simpleController(viewModel) { state ->

        tool {
            id("arrowLeft")
            icon(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_arrow_back_ios_24,
                    context?.theme
                )
            )
            onClick { v ->
                when (state.toolbar) {
                    0 -> viewModel.switchToolbar(2)
                    else -> viewModel.switchToolbar(state.toolbar - 1)
                }
            }
        }

        when (state.toolbar) {
            0 -> {
                drawview.setTouchBlock(true)
            }
            1 -> {
                for (i in 0..7) {
                    tool {
                        id("marker$i")
                        icon(
                            ResourcesCompat.getDrawable(
                                resources,
                                Raidmarker.raidmarkerById(i),
                                context?.theme
                            )
                        )
                        if (state.marker == i) {
                            bg(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.rectangle_frame,
                                    context?.theme
                                )
                            )
                        }
                        onClick { v ->
                            drawview.setTouchBlock(true)
                            setMarker(i + 1)
                            setSelection(0)
                        }
                    }
                }
                tool {
                    id("clearMarker")
                    icon(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_delete_24,
                            context?.theme
                        )
                    )
                    onClick { v ->
                        attacher.clearIcons()
                    }
                }

            }
            2 -> {
                drawview.setTouchBlock(false)
                tool {
                    id("clear")
                    icon(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_delete_24,
                            context?.theme
                        )
                    )
                    onClick { v ->
                        drawview.clearCanvas()
                    }
                }

                tool {
                    id("undo")
                    icon(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_undo_24,
                            context?.theme
                        )
                    )
                    onClick { v ->
                        drawview.undo()
                    }
                }

                tool {
                    id("redo")
                    icon(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_redo_24,
                            context?.theme
                        )
                    )
                    onClick { v ->
                        drawview.redo()
                    }
                }

                tool {
                    id("minusStroke")
                    icon(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_remove_24,
                            context?.theme
                        )
                    )
                    onClick { v ->
                        drawview.setStrokeWidth(
                            (drawview.getStrokeWidth() - 1.0f).coerceAtLeast(
                                1.0f
                            )
                        )
                        requestModelBuild()
                    }
                }

                simpleText {
                    id("strokeCurrent")
                    stroke("${drawview.getStrokeWidth().toInt()}")
                }

                tool {
                    id("plusStroke")
                    icon(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_add_24,
                            context?.theme
                        )
                    )
                    onClick { v ->
                        drawview.setStrokeWidth((drawview.getStrokeWidth() + 1.0f))
                        requestModelBuild()
                    }
                }
            }

        }

        tool {
            id("arrowRight")
            icon(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_arrow_forward_ios_24,
                    context?.theme
                )
            )
            onClick { v ->
                when (state.toolbar) {
                    2 -> viewModel.switchToolbar(0)
                    else -> viewModel.switchToolbar(state.toolbar + 1)
                }
            }
        }
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

