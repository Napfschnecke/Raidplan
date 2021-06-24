package com.raidplan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.raidplan.MainActivity
import com.raidplan.R
import com.raidplan.api.DataCrawler
import com.raidplan.util.MvRxEpoxyController
import com.raidplan.data.Character
import com.raidplan.data.User
import io.realm.Realm

abstract class AdvancedFragment : BaseMvRxFragment() {

    lateinit var recyclerView: EpoxyRecyclerView
    lateinit var secondRecyclerView: EpoxyRecyclerView

    lateinit var swipelayout: SwipeRefreshLayout
    val epoxyController by lazy { epoxyController() }
    val secondEpoxyController by lazy { secondEpoxyController() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advanced_mvrx, container, false).apply {
            swipelayout = findViewById(R.id.swipe_layout)
            swipelayout.setColorSchemeColors(
                resources.getColor(R.color.black),
                resources.getColor(R.color.mage)
            )
            swipelayout.setOnRefreshListener {
                val act = activity as MainActivity
                var char: Character? = null
                Realm.getDefaultInstance().use { realm ->
                    realm.where(User::class.java).findFirst()?.let { u ->
                        val unmanaged = realm.copyFromRealm(u)
                        char = unmanaged.mainChar
                    }
                }

                char?.let {
                    DataCrawler.getKeystoneData(act, it)
                }
                swipelayout.isRefreshing = false
            }
            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.setController(epoxyController)
            secondRecyclerView = findViewById(R.id.recycler_view_two)
            secondRecyclerView.setController(secondEpoxyController)
            secondRecyclerView.layoutManager = GridLayoutManager(context, 5)
        }
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
        secondRecyclerView.requestModelBuild()
    }

    abstract fun epoxyController(): MvRxEpoxyController
    abstract fun secondEpoxyController(): MvRxEpoxyController
}