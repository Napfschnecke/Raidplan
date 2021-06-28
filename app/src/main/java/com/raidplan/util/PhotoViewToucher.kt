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
import com.raidplan.data.Raidmarker

class PhotoViewToucher(view: ImageView?, val context: Context, val rel: RelativeLayout) :
    PhotoViewAttacher(view),
    OnLongClickListener {
    var event: MotionEvent? = null
    var scaleBackup = 1.0f
    var selectedIcon: Int = 0
    var selectedMarker: Int = 0
    var imageMemory = Array<ImageView?>(5) { null }
    var imageMemoryMarker = Array<ImageView?>(8) { null }

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
        imageMemoryMarker.forEach {
            rel.removeView(it)
        }
    }

    private fun handleTouch() {
        if (selectedIcon != 0 || selectedMarker != 0) {
            event?.let { e ->
                var x = 0
                var y = 0
                if (e.actionMasked == MotionEvent.ACTION_DOWN) {
                    x = e.x.toInt()
                    y = e.y.toInt()
                }

                if (selectedIcon != 0) {
                    imageMemory[selectedIcon - 1]?.let {
                        rel.removeView(it)
                        imageMemory[selectedIcon - 1] = null
                    }
                    val img = ImageView(context)
                    img.scaleX = 1.5f
                    img.scaleY = 1.5f
                    img.setImageResource(Raidmarker.roleBlobById(selectedIcon))

                    rel.addView(img)
                    val lp = img.layoutParams as RelativeLayout.LayoutParams
                    imageMemory[selectedIcon - 1] = img

                    lp.leftMargin = x - 18
                    lp.topMargin = y - 18
                    img.layoutParams = lp
                    true
                } else {
                    val index = selectedMarker - 1
                    imageMemoryMarker[index]?.let {
                        rel.removeView(it)
                        imageMemoryMarker[index] = null
                    }
                    val img = ImageView(context)
                    img.scaleX = 1.5f
                    img.scaleY = 1.5f
                    img.setImageResource(Raidmarker.raidmarkerById(index))

                    rel.addView(img)
                    val lp = img.layoutParams as RelativeLayout.LayoutParams
                    imageMemoryMarker[index] = img

                    lp.leftMargin = x - 18
                    lp.topMargin = y - 18
                    img.layoutParams = lp
                    true
                }
            }
        }
    }
}