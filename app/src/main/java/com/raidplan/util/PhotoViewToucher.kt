package com.raidplan.util

import android.content.Context
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.android.material.snackbar.Snackbar
import com.raidplan.R

class PhotoViewToucher(view: ImageView?, val context: Context, val rel: RelativeLayout) :
    PhotoViewAttacher(view),
    OnLongClickListener {
    var event: MotionEvent? = null
    var scaleBackup = 1.0f
    var selectedIcon: Int = 0
    var imageMemory = Array<ImageView?>(5) { null }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        this.event = event
        handleTouch()
        view.performClick()
        return super.onTouch(view, event)
    }

    override fun onLongClick(view: View): Boolean {
        Snackbar.make(view, "asdasd", Snackbar.LENGTH_SHORT).show()
        return false
    }

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        this.scale = scaleBackup
    }

    fun clearIcons() {
        imageMemory.forEach {
            rel.removeView(it)
        }
    }

    private fun handleTouch() {
        if (selectedIcon != 0) {
            event?.let { e ->
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
                val img = ImageView(context)
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

                lp.leftMargin = x - 18
                lp.topMargin = y - 18
                img.layoutParams = lp
                true
            }
        }
    }
}