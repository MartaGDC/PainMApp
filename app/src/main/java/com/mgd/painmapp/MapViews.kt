package com.mgd.painmapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.ContextCompat.*

class MapViews(context: Context, attrs: AttributeSet) : MapResponsiveViews(context, attrs) {
    override fun onDraw(canvas: Canvas) {
        val bounds = RectF()
        val cDrawable = getDrawable(context, imgFuente)
        canvas.clipPath(cPath)
        cPath.computeBounds(bounds, true)
        cDrawable?.setBounds(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
        cDrawable?.draw(canvas)
        canvas.drawPath(cPath, cPaint)
    }
}