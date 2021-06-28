package com.raidplan.ui

import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.raidplan.MainActivity
import com.raidplan.data.Character
import com.raidplan.data.Classes
import com.raidplan.roster
import com.raidplan.rosterBench
import com.raidplan.rosterSelected
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RosterPickerArgs(val member: ArrayList<Character>) : Parcelable

data class RosterPickerState(
    var member: ArrayList<Character> = arrayListOf(),
    var memberSelected: ArrayList<Character> = arrayListOf()
) :
    MvRxState {
    constructor(args: RosterPickerArgs) : this(args.member)
}

class RosterPickerModel(initialState: RosterPickerState) :
    MvRxViewModel<RosterPickerState>(initialState) {

    fun updateLists(
        member: ArrayList<Character>,
        memberSelected: ArrayList<Character>,
        act: MainActivity
    ) {
        act.updateRosterToolbar(memberSelected)
        setState {
            copy(
                member = member,
                memberSelected = memberSelected
            )
        }
    }
}

class RosterPickerFragment : AdvancedFragment() {

    private val viewModel: RosterPickerModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->
        val act = activity as MainActivity
        var block = false

        state.memberSelected.sortedBy { it.getRoleId() }.forEach {

            if (!it.bench) {
                rosterSelected {
                    id("${it.name}selected")
                    title(it.name)
                    color(
                        ResourcesCompat.getDrawable(
                            resources,
                            Classes.getClassColor(it.playerClass),
                            context?.theme
                        )
                    )
                    val imgRes = Classes.getIconByRole("${it.role}")
                    role(ResourcesCompat.getDrawable(resources, imgRes, context?.theme))
                    onClick { v ->
                        if (!block) {
                            block = true
                            state.memberSelected.remove(it)
                            state.member.add(it)
                            viewModel.updateLists(state.member, state.memberSelected, act)
                            recyclerView.requestModelBuild()
                            secondRecyclerView.requestModelBuild()
                        }
                    }
                }
            } else {
                rosterBench {
                    id("${it.name}selected")
                    title(it.name)
                    color(
                        ResourcesCompat.getDrawable(
                            resources,
                            Classes.getClassColor(it.playerClass),
                            context?.theme
                        )
                    )
                    val imgRes = Classes.getIconByRole("${it.role}")
                    role(ResourcesCompat.getDrawable(resources, imgRes, context?.theme))
                    onClick { v ->
                        if (!block) {
                            block = true
                            it.bench = false
                            state.memberSelected.remove(it)
                            state.member.add(it)
                            viewModel.updateLists(state.member, state.memberSelected, act)
                            recyclerView.requestModelBuild()
                            secondRecyclerView.requestModelBuild()
                        }
                    }
                }
            }
        }
    }

    override fun secondEpoxyController() = simpleController(viewModel) { state ->
        val act = activity as MainActivity
        var block = false
        state.member.forEach {
            roster {
                id("${it.name}roster")
                title(it.name)
                color(Classes.getClassColor(it.playerClass))
                onClick { v ->
                    if (!block) {
                        block = true
                        state.memberSelected.add(it)
                        state.member.remove(it)
                        viewModel.updateLists(state.member, state.memberSelected, act)
                        recyclerView.requestModelBuild()
                        secondRecyclerView.requestModelBuild()
                    }
                }
                onLongClick { v ->
                    it.bench = true
                    state.memberSelected.add(it)
                    state.member.remove(it)
                    viewModel.updateLists(state.member, state.memberSelected, act)
                    recyclerView.requestModelBuild()
                    secondRecyclerView.requestModelBuild()
                    true
                }
            }
        }
    }

    companion object {

        fun newInstance(gm: ArrayList<Character>): RosterPickerFragment {

            val frag = RosterPickerFragment()
            val bundle = Bundle().apply { putParcelable(MvRx.KEY_ARG, RosterPickerArgs(gm)) }
            frag.arguments = bundle
            return frag
        }

        @BindingAdapter("app:tint")
        @JvmStatic
        fun ImageView.setImageTint(@ColorInt color: Int) {
            setColorFilter(color)
        }
    }
}