package com.raidplan.ui

import android.view.View
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.raidplan.MainActivity
import com.raidplan.R
import com.raidplan.api.DataCrawler
import com.raidplan.auth
import com.raidplan.util.MvRxViewModel
import com.raidplan.util.simpleController

data class AuthState(val dummy: String? = "") : MvRxState

class AuthViewModel(initialState: AuthState) : MvRxViewModel<AuthState>(initialState)

class AuthFragmentMvrx : BaseFragment() {

    private val viewModel: AuthViewModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->

        auth {
            id("setchar")
            title(resources.getString(R.string.setup))
            image(resources.getDrawable(R.drawable.ic_menu_camera))
            onClick(View.OnClickListener {
                (activity as MainActivity).authorize()
            })
        }
    }
}