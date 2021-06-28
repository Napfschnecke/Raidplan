package com.raidplan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.raidplan.R
import com.raidplan.util.MvRxEpoxyController

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

            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.setController(epoxyController)
            recyclerView.layoutManager = GridLayoutManager(context, 3)
            secondRecyclerView = findViewById(R.id.recycler_view_two)
            secondRecyclerView.setController(secondEpoxyController)
            secondRecyclerView.layoutManager =
                GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)
        }
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
        secondRecyclerView.requestModelBuild()
    }

    abstract fun epoxyController(): MvRxEpoxyController
    abstract fun secondEpoxyController(): MvRxEpoxyController
}