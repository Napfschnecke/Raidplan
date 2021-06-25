package com.raidplan.ui

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.snackbar.Snackbar
import com.raidplan.R
import com.raidplan.data.Bosses
import com.raidplan.util.MvRxEpoxyController
import com.raidplan.util.PhotoViewToucher

abstract class ZoomFragment(var boss: String) :
    BaseMvRxFragment() {

    lateinit var recyclerView: EpoxyRecyclerView
    var selectedIcon: Int = 0
    var imageMemory = Array<PhotoView?>(5) { null }

    val epoxyController by lazy { epoxyController() }
    var image: PhotoView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_zoom_mvrx, container, false).apply {
            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.setController(epoxyController)
            val rel = findViewById<RelativeLayout>(R.id.zoomcont)
            image = findViewById<PhotoView>(R.id.largeImage)
            image?.setImageResource(Bosses.getAreaByName(boss))
            val attacher = PhotoViewToucher(image)

            attacher.setOnMatrixChangeListener { rect ->
                if (attacher.scale > 1.0f) {
                    attacher.scaleBackup = attacher.scale
                }
            }
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            val disX = size.x
            val disY = size.y
            attacher.setOnLongClickListener { v ->
                if (selectedIcon != 0) {
                    attacher.event?.let { e ->
                        var x = 0
                        var y = 0
                        if (e.actionMasked == MotionEvent.ACTION_DOWN) {
                            x = e.x.toInt()
                            y = e.y.toInt()
                        }
                        imageMemory[selectedIcon - 1]?.let {
                            rel.removeView(it)
                            imageMemory[selectedIcon - 1] = null
                        }
                        val img = PhotoView(context)
                        img.scaleX = 1.5f
                        img.scaleY = 1.5f
                        when (selectedIcon) {
                            1 -> img.setImageResource(R.drawable.tank_blob_selected)
                            2 -> img.setImageResource(R.drawable.healer_blob_selected)
                            3 -> img.setImageResource(R.drawable.melee_blob_selected)
                            4 -> img.setImageResource(R.drawable.range_blob_selected)
                            5 -> img.setImageResource(R.drawable.boss_blob_selected)
                        }
                        rel.addView(img)
                        val lp = img.layoutParams as RelativeLayout.LayoutParams
                        imageMemory[selectedIcon - 1] = img
                        lp.leftMargin = x - (disX - rel.width + 16)
                        lp.topMargin = y - (disY - rel.height + 16)
                        img.layoutParams = lp
                        true
                    }
                }
                false
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