package com.raidplan.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.RectF
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.divyanshu.draw.widget.DrawView
import com.github.chrisbanes.photoview.PhotoView
import com.raidplan.R
import com.raidplan.data.Bosses
import com.raidplan.util.CustomDrawView
import com.raidplan.util.MvRxEpoxyController
import com.raidplan.util.PhotoViewToucher

abstract class ZoomFragment(var boss: String) :
    BaseMvRxFragment() {

    lateinit var recyclerView: EpoxyRecyclerView
    lateinit var recyclerViewTwo: EpoxyRecyclerView

    lateinit var drawview: CustomDrawView
    lateinit var attacher: PhotoViewToucher
    var selectedIcon: Int = 0
    var selectedMarker: Int = 0

    val epoxyController by lazy { epoxyController() }
    val secondEpoxyController by lazy { secondEpoxyController() }

    var image: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_zoom_mvrx, container, false).apply {
            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.setController(epoxyController)

            recyclerViewTwo = findViewById(R.id.recycler_view_2)
            recyclerViewTwo.setController(secondEpoxyController)

            drawview = findViewById(R.id.drawview)
            drawview.setColor(Color.RED)
            drawview.setStrokeWidth(5f)
            drawview.setAlpha(100)

            val rel = findViewById<RelativeLayout>(R.id.zoomcont)
            image = findViewById(R.id.largeImage)
            image?.setImageResource(Bosses.getAreaByName(boss))

            attacher = PhotoViewToucher(image, context, rel)
            attacher.setZoomable(false)

            /*
            recyclerView.layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
             */
        }
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
        recyclerViewTwo.requestModelBuild()
    }

    abstract fun setSelection(sel: Int)

    abstract fun setMarker(sel: Int)

    abstract fun updateImage(res: Int)

    abstract fun updateBossArea(boss: String)

    abstract fun epoxyController(): MvRxEpoxyController

    abstract fun secondEpoxyController(): MvRxEpoxyController
}