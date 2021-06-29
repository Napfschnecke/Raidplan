package com.raidplan.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.raidplan.*
import com.raidplan.data.Bosses
import com.raidplan.data.Classes
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

    fun switchToolbar(direction: Int) {
        setState {
            var newPos = this.toolbar
            newPos += direction
            if (newPos < 0) {
                newPos = 2
            } else if (newPos > 2) {
                newPos = 0
            }
            copy(
                toolbar = newPos
            )
        }
    }
}

class RaidPosMvrx(boss: String, grid: Int = 0) : ZoomFragment(boss, grid) {

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

    override fun updateGridSpan(span: Int) {
        activity?.runOnUiThread {
            layoutManager?.spanCount = span
        }
    }

    override fun epoxyController() = simpleController(viewModel) { state ->

        if ("${state.boss}".contains("Sylvanas Windrunner")) {
            for (i in 1..4) {
                check {
                    id("check$i")
                    phase(
                        "${resources.getString(R.string.phase)} $i"
                    )
                    checked(
                        state.phase == i
                    )
                    onClick { v ->
                        viewModel.switchPhase(i)
                        updateBossArea("${state.boss?.subSequence(0, state.boss.length - 1)}$i")
                    }
                }
            }
        } else if ("${state.boss}".contains("Kel'Thuzad")) {
            for (i in 1..2) {
                check {
                    id("check$i")
                    phase(
                        "${resources.getString(R.string.phase)} $i"
                    )
                    checked(
                        state.phase == i
                    )
                    onClick { v ->
                        viewModel.switchPhase(i)
                        updateBossArea("${state.boss?.subSequence(0, state.boss.length - 1)}$i")
                    }
                }
            }
        }
    }

    override fun secondEpoxyController() = simpleController(viewModel) { state ->

        when (state.toolbar) {
            0 -> {
                updateGridSpan(6)
                drawview.setTouchBlock(true)
                for (i in 0..4) {
                    tool {
                        id("role$i")
                        icon(
                            ResourcesCompat.getDrawable(
                                resources,
                                Classes.getIconByRole(Classes.getRoleById(i)),
                                context?.theme
                            )
                        )
                        if (state.selection == i + 1) {
                            bg(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.rectangle_fill,
                                    context?.theme
                                )
                            )
                        }
                        onClick { v ->
                            setMarker(0)
                            setSelection(i + 1)
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
            1 -> {
                updateGridSpan(9)
                drawview.setTouchBlock(true)
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
                                    R.drawable.rectangle_fill,
                                    context?.theme
                                )
                            )
                        }
                        onClick { v ->
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
                updateGridSpan(5)
                drawview.setTouchBlock(false)

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

                colorbox {
                    id("colorPicker")

                    color(
                        drawview.getColor()
                    )

                    onClick { v ->
                        ColorPickerDialogBuilder
                            .with(context, R.style.CustomAlertDialog)
                            .setTitle("Choose color")
                            .initialColor(drawview.getColor())
                            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                            .density(12)
                            .setPositiveButton(
                                "Select"
                            ) { dialog, selectedColor, allColors ->
                                drawview.setColor(selectedColor)
                                requestModelBuild()
                            }
                            .setNegativeButton(
                                "Cancel"
                            ) { dialog, which ->
                                dialog.dismiss()
                            }
                            .build()
                            .show()
                    }
                }

                plusminus {
                    id("stroke")
                    stroke("${drawview.getStrokeWidth().toInt()}")
                    onClick { v ->
                        drawview.setStrokeWidth(
                            (drawview.getStrokeWidth() - 1.0f).coerceAtLeast(
                                1.0f
                            )
                        )
                        requestModelBuild()
                    }
                    onClick2 { v ->
                        drawview.setStrokeWidth(
                            (drawview.getStrokeWidth() + 1.0f)
                        )
                        requestModelBuild()
                    }
                }

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
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arrowLeft?.setOnClickListener { v ->
            setMarker(0)
            setSelection(0)
            viewModel.switchToolbar(-1)
        }
        arrowRight?.setOnClickListener { v ->
            setMarker(0)
            setSelection(0)
            viewModel.switchToolbar(1)
        }
        super.onViewCreated(view, savedInstanceState)
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

