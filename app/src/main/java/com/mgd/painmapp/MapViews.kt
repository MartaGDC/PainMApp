package com.mgd.painmapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat.*
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser

class MapViews(context: Context, attrs: AttributeSet) : MapResponsiveViews(context, attrs) {
    lateinit var paths: List<String>
    override fun onDraw(canvas: Canvas) {
        val bounds = RectF()
        val cDrawable = getDrawable(context, imgFuente)
        canvas.clipPath(cPath)
        cPath.computeBounds(bounds, true)
        cDrawable?.setBounds(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
        cDrawable?.draw(canvas)
        canvas.drawPath(cPath, cPaint)

        for (path in paths){
            var dibujos: Path
            var dibujoLimpio: Path
            dibujos = PathParser.createPathFromPathData(path)
            val dibujosSinRect = path.replace(Regex("M[\\d.,\\s]+L[\\d.,\\s]+L[\\d.,\\s]+L[\\d.,\\s]+Z"), "") //Expresion regex M con digito, punto, coma o espacio + ... (Doble \ para escape)
            dibujoLimpio = PathParser.createPathFromPathData(dibujosSinRect)
            //Necesario modificar la escala y el centrado del path obtenido del dibujo del usuario para asegurar que coincide con el path del cuerpo humano:
            val boundsDibujo = RectF()
            //Escala:
            dibujos.computeBounds(boundsDibujo, true)
            val scaleX = bounds.width() / boundsDibujo.width()
            val scaleY = bounds.height() / boundsDibujo.height()
            val scaleFactor = minOf(scaleX, scaleY)
            val matrix =Matrix()
            matrix.setScale(scaleFactor, scaleFactor, boundsDibujo.centerX(), boundsDibujo.centerY())
            dibujoLimpio.transform(matrix)
            //Centrado:
            dibujos.computeBounds(boundsDibujo, true)
            val dx = (bounds.centerX() - boundsDibujo.centerX())
            val dy = (bounds.centerY() - boundsDibujo.centerY())
            matrix.reset()
            matrix.setTranslate(dx, dy)
            dibujoLimpio.transform(matrix)
            canvas.drawPath(dibujoLimpio, bPaint)
        }

    }
}