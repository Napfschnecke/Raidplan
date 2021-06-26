package com.raidplan.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.raidplan.R
import com.raidplan.data.Character
import com.raidplan.data.Classes
import com.raidplan.data.Guild
import com.raidplan.data.User
import com.raidplan.guild
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController
import io.realm.Realm
import io.realm.RealmList

data class GuildState(
    var user: User? = null,
    var member: List<Character>? = listOf(),
    var rank: Float = 2.0f
) :
    MvRxState

class GuildViewModel(initialState: GuildState) : MvRxViewModel<GuildState>(initialState) {

    val realm: Realm = Realm.getDefaultInstance()
    var guildMembers: RealmList<Character>? = null

    init {

        val user = realm.where(User::class.java).findFirst()
        user?.let {
            val guild =
                realm.where(Guild::class.java).equalTo("name", it.mainChar?.guild)
                    .findFirst()

            guildMembers = guild?.roster
            guildMembers?.addChangeListener { gm, changeset ->
                val unmanaged = realm.copyFromRealm(gm)
                setState {
                    copy(member = unmanaged.sortedByDescending { c -> c.itemLevel })
                }
            }

            guildMembers?.let { gm ->
                val unmanagedUser = realm.copyFromRealm(user)
                val unmanagedMembers = realm.copyFromRealm(gm.toMutableList())
                setState {
                    copy(
                        user = unmanagedUser,
                        member = unmanagedMembers.sortedByDescending { c -> c.itemLevel }
                    )
                }
            }
            val um = realm.copyFromRealm(user)

            setState { copy(rank = um.rankPref) }
        }
    }

    fun updateRank(value: Float) {
        withState { state ->
            Realm.getDefaultInstance().use {
                it.executeTransactionAsync { bgRealm ->
                    val u =
                        bgRealm.where(User::class.java).equalTo("accountId", state.user?.accountId)
                            .findFirst()
                    u?.rankPref = value
                }
            }
        }
        setState { copy(rank = value) }
    }
}

class GuildFragmentMvrx : SliderFragment() {

    private val viewModel: GuildViewModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->

        val filteredMembers = state.member?.filter { it.guildRank <= state.rank }

        filteredMembers?.forEach { char ->

            guild {
                id(char.name)
                val imgRes = Classes.getIconByRole("${char.role}")
                img(ResourcesCompat.getDrawable(resources, imgRes, context?.theme))
                cov(
                    ResourcesCompat.getDrawable(
                        resources,
                        Classes.getCovenantIcon(char.covenant),
                        context?.theme
                    )
                )
                renown("${char.renown}")
                name(char.name)
                spec("${char.activeSpec} ${Classes.getClassById(char.playerClass)}")
                ilvl("${char.itemLevel}ilvl")
                color(Classes.getClassColor(char.playerClass))
                onClick { v ->
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.guildMembers?.removeAllChangeListeners()
        viewModel.realm.close()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        slider.addOnChangeListener { s, value, fromUser ->
            viewModel.updateRank(value)
            view.findViewById<TextView>(R.id.select_rank_value)?.text = value.toInt().toString()
        }
        super.onViewCreated(view, savedInstanceState)
    }
}