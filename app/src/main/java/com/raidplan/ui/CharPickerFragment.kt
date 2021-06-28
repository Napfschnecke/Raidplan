package com.raidplan.ui

import android.view.View
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.google.android.material.snackbar.Snackbar
import com.raidplan.MainActivity
import com.raidplan.R
import com.raidplan.chars
import com.raidplan.data.Character
import com.raidplan.data.Classes
import com.raidplan.data.User
import com.raidplan.header
import com.raidplan.util.CharPicker
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController
import io.realm.Realm
import io.realm.RealmList

data class CharPickerState(
    var user: User? = null,
    var member: List<Character>? = listOf()
) :
    MvRxState

class CharPickerModel(initialState: CharPickerState) :
    MvRxViewModel<CharPickerState>(initialState) {

    val realm: Realm = Realm.getDefaultInstance()
    var charList: RealmList<Character>? = null

    init {

        val user = realm.where(User::class.java).findFirst()
        user?.let {
            charList = realm.where(User::class.java).findFirst()?.characters

            charList?.addChangeListener { gm, changeset ->
                val unmanaged = realm.copyFromRealm(gm)
                setState {
                    copy(member = unmanaged)
                }
            }

            charList?.let { gm ->
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

class CharPickerFragment : BaseFragment() {

    private val viewModel: CharPickerModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->

        header {
            id("header")
            title(resources.getString(R.string.set_character))
            color(R.color.colorAccent)
        }

        state.member?.forEach { char ->

            chars {
                id(char.name)
                name(char.name)
                lvl("${char.level}")
                server(char.server)
                color(Classes.getClassColor(char.playerClass))
                onClick(View.OnClickListener {
                    val act = activity as MainActivity
                    CharPicker().persistMainChar(char, act)
                    Snackbar.make(
                        it,
                        resources.getString(R.string.setting_character),
                        Snackbar.LENGTH_LONG
                    ).apply {
                        val lay = view as Snackbar.SnackbarLayout
                        val cLay = act.layoutInflater.inflate(R.layout.snackbar_progress, null)
                        lay.addView(cLay)

                        setBackgroundTint(act.resources.getColor(R.color.colorAccent))
                        setTextColor(act.resources.getColor(R.color.black))
                        show()
                    }
                })
            }
        }

    }

    override fun onDestroy() {
        viewModel.charList?.removeAllChangeListeners()
        viewModel.realm.close()
        super.onDestroy()
    }
}