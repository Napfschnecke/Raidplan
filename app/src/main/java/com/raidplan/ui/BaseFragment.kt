package com.raidplan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.raidplan.MainActivity
import com.raidplan.R
import com.raidplan.api.DataCrawler
import com.raidplan.util.MvRxEpoxyController

abstract class BaseFragment(var grid: Int = 0, var refreshable: Boolean = true) :
    BaseMvRxFragment() {

    lateinit var recyclerView: EpoxyRecyclerView
    lateinit var swipelayout: SwipeRefreshLayout
    lateinit var progressBar: ProgressBar

    val epoxyController by lazy { epoxyController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_mvrx, container, false).apply {
            swipelayout = findViewById(R.id.swipe_layout)
            swipelayout.setColorSchemeColors(
                resources.getColor(R.color.hunter),
                resources.getColor(R.color.mage),
                resources.getColor(R.color.dh),
                resources.getColor(R.color.legendary)
            )

            if (refreshable) {
                swipelayout.setOnRefreshListener {
                    swipelayout.isRefreshing = false
                    refreshData(this)
                }
            } else {
                swipelayout.setOnRefreshListener {
                    swipelayout.isRefreshing = false
                }
            }

            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.setController(epoxyController)
            if (grid != 0) {
                recyclerView.layoutManager = GridLayoutManager(context, grid)
            }
        }
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
    }


    fun refreshData(view: View) {
        /*
        progressBar = view.findViewById<ProgressBar>(R.id.update_progress).apply {
            isVisible = true
            progress = 0
        }
         */

        val act = activity as MainActivity

        act.user?.mainChar?.let {
            DataCrawler.getGuildMembers("${it.guild}", "${it.server}", act)
        }

        swipelayout.isRefreshing = false
    }

    abstract fun epoxyController(): MvRxEpoxyController
}