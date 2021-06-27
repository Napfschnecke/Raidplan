package com.raidplan.util

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class SquareView : FrameLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val child = getChildAt(0)
        child.measure(widthMeasureSpec, widthMeasureSpec)
        val width = FrameLayout.resolveSize(child.measuredHeight, widthMeasureSpec)
        child.measure(width, width)
        setMeasuredDimension(width, width)
    }
}