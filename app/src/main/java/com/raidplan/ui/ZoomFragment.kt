package com.raidplan.ui

import android.content.Context
import android.graphics.Point
import android.graphics.RectF
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.github.chrisbanes.photoview.PhotoView
import com.raidplan.R
import com.raidplan.data.Bosses
import com.raidplan.util.MvRxEpoxyController
import com.raidplan.util.PhotoViewToucher

abstract class ZoomFragment(var boss: String) :
    BaseMvRxFragment() {

    lateinit var recyclerView: EpoxyRecyclerView
    lateinit var attacher: PhotoViewToucher
    var selectedIcon: Int = 0

    val epoxyController by lazy { epoxyController() }
    var image: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_zoom_mvrx, container, false).apply {
            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.setController(epoxyController)
            val rel = findViewById<RelativeLayout>(R.id.zoomcont)
            image = findViewById(R.id.largeImage)
            image?.setImageResource(Bosses.getAreaByName(boss))
            attacher = PhotoViewToucher(image, context, rel)
            attacher.setZoomable(false)

            attacher.setOnScaleChangeListener { scaleFactor, focusX, focusY ->
                if (scaleFactor > 1.0f) {
                    attacher.scaleBackup = scaleFactor
                }
            }


            recyclerView.layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        }
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
    }

    abstract fun setSelection(sel: Int)

    abstract fun updateImage(res: Int)

    abstract fun updateBossArea(boss: String)

    abstract fun epoxyController(): MvRxEpoxyController
}