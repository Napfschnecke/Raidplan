package com.raidplan.ui

import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.raidplan.R
import com.raidplan.data.Character
import com.raidplan.data.Classes
import com.raidplan.data.Guild
import com.raidplan.data.User
import com.raidplan.guild
import com.raidplan.headerGuild
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController
import io.realm.Realm
import io.realm.RealmList

data class GuildState(
    var user: User? = null,
    var member: List<Character>? = listOf(),
    var rank: String = "3"
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
                    copy(member = unmanaged.filter { m -> m.guildRank <= rank.toInt() })
                }
            }

            guildMembers?.let { gm ->
                val unmanagedUser = realm.copyFromRealm(user)
                val unmanagedMembers = realm.copyFromRealm(gm.toMutableList())
                setState {
                    copy(
                        user = unmanagedUser,
                        member = unmanagedMembers
                    )
                }
            }
        }
    }
}

class GuildFragmentMvrx : BaseFragment() {

    private val viewModel: GuildViewModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->

        headerGuild {
            id("guildheader")
        }
        state.member?.forEach { char ->

            var hk: Int? = 0
            if (char.dungeonList.size > 0) {
                hk =
                    char.dungeonList.sortedByDescending { key -> key.level?.toInt() }[0].level?.toInt()
            }

            guild {
                id(char.name)
                name(char.name)
                color(Classes.getClassColor(char.playerClass))
                hk?.let {
                    when {
                        it == 0 -> {
                            keycolor(R.color.dk)
                        }
                        it < 15 -> {
                            keycolor(android.R.color.white)
                        }
                        else -> {
                            keycolor(R.color.monk)
                        }
                    }
                }
                keylvl("$hk")
            }
        }

    }

    override fun onDestroy() {
        viewModel.guildMembers?.removeAllChangeListeners()
        viewModel.realm.close()
        super.onDestroy()
    }
}