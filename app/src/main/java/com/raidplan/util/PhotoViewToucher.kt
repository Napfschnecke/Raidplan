package com.raidplan.util

import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import com.github.chrisbanes.photoview.PhotoViewAttacher

internal class PhotoViewToucher(view: ImageView?) : PhotoViewAttacher(view), OnLongClickListener {
    var event: MotionEvent? = null
    var scaleBackup = 1.0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        this.event = event
        return super.onTouch(view, event)
    }

    override fun onLongClick(view: View): Boolean {
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
}