package com.raidplan.ui

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.raidplan.R
import com.raidplan.data.Character
import com.raidplan.data.Classes
import com.raidplan.data.Guild
import com.raidplan.data.User
import com.raidplan.guild
import com.raidplan.headerGuild
import com.raidplan.headerSmall
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController
import io.realm.Realm
import io.realm.RealmList

data class GuildState(
    var user: User? = null,
    var member: List<Character>? = listOf(),
    var rank: String = "3",
    var filterTank: Boolean = false,
    var filterHeal: Boolean = false,
    var filterMelee: Boolean = false,
    var filterRange: Boolean = false,
    var hint: Boolean = true
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
        }

        Handler(Looper.getMainLooper()).postDelayed({
            setState { copy(hint = false) }
        }, 5000)
    }

    fun filter(role: String, shouldFilter: Boolean) {

        when (role) {
            "Tank" -> setState {
                copy(
                    filterTank = shouldFilter
                )
            }
            "Healer" -> setState {
                copy(
                    filterHeal = shouldFilter
                )
            }
            "Melee" -> setState {
                copy(
                    filterMelee = shouldFilter
                )
            }
            "Range" -> setState {
                copy(
                    filterRange = shouldFilter
                )
            }
        }
    }
}

class GuildFragmentMvrx : BaseFragment() {

    private val viewModel: GuildViewModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->

        headerGuild {
            id("filterBoxes")
            filterTank(state.filterTank)
            onClickTank(View.OnClickListener {
                viewModel.filter("Tank", !state.filterTank)
            })
            filterHeal(state.filterHeal)
            onClickHeal(View.OnClickListener {
                viewModel.filter("Healer", !state.filterHeal)
            })
            filterMelee(state.filterMelee)
            onClickMelee(View.OnClickListener {
                viewModel.filter("Melee", !state.filterMelee)
            })
            filterRange(state.filterRange)
            onClickRange(View.OnClickListener {
                viewModel.filter("Range", !state.filterRange)
            })
        }

        if (state.hint) {
            headerSmall {
                id("hint")
                title(resources.getString(R.string.longclick_to_add))
                color(R.color.colorAccent)
            }
        }

        var filteredMembers = state.member
        if (state.filterTank) {
            filteredMembers = filteredMembers?.filterNot { m -> m.role == "Tank" }
        }
        if (state.filterHeal) {
            filteredMembers = filteredMembers?.filterNot { m -> m.role == "Healer" }
        }
        if (state.filterMelee) {
            filteredMembers = filteredMembers?.filterNot { m -> m.role == "Melee" }
        }
        if (state.filterRange) {
            filteredMembers = filteredMembers?.filterNot { m -> m.role == "Range" }
        }

        filteredMembers?.forEach { char ->

            guild {
                id(char.name)
                val imgRes = Classes.getIconByRole("${char.role}")
                img(ResourcesCompat.getDrawable(resources, imgRes, context?.theme))
                roster(char.roster)
                cov(
                    ResourcesCompat.getDrawable(
                        resources,
                        Classes.getCovenantIcon(char.covenant),
                        context?.theme
                    )
                )
                renown("${char.renown}")
                name(char.name)
                ilvl("${char.itemLevel}ilvl")
                color(Classes.getClassColor(char.playerClass))
                onLongClick { v ->
                    Realm.getDefaultInstance().use { r ->
                        r.executeTransactionAsync { bgRealm ->
                            var char =
                                bgRealm.where(Character::class.java).equalTo("name", char.name)
                                    .findFirst()
                            char?.roster?.let {
                                char.roster = !char.roster
                            }

                        }
                    }
                    true
                }
            }
        }

    }

    override fun onDestroy() {
        viewModel.guildMembers?.removeAllChangeListeners()
        viewModel.realm.close()
        super.onDestroy()
    }
}