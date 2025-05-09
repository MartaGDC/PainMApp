package com.mgd.painmapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.ContextCompat.*
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.PathParser
import com.mgd.painmapp.controller.InterpretationHelper
import com.mgd.painmapp.model.storage.ColorBrush

class MapViews(context: Context, attrs: AttributeSet) : MapResponsiveViews(context, attrs) {
    lateinit var paths: List<String>
    val listLimpio = mutableListOf<Path>()
    override fun onDraw(canvas: Canvas) {
        if (isInEditMode) return //Añadido porque onDraw no sabía gestionar que los paths no estuvieran inicializados,
                                 // antes de la ejecución de la app, dado que se inician desde SensorialActivity.
                                 // Esto es porque las views realizan un renderizado-simulación antes de la ejecución, y
                                 // y no puede dibujar (onDraw) trazos nulos o inexistentes.
        val bounds = RectF()
        val cDrawable = getDrawable(context, imgFuente)
        canvas.clipPath(cPath)
        cPath.computeBounds(bounds, true)
        cDrawable?.setBounds(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
        cDrawable?.draw(canvas)

        val matrix =Matrix()

        if (!paths.isEmpty()){
            var rectanguloEscala = PathParser.createPathFromPathData(paths[0])
            val boundsRectangulo = RectF()
            rectanguloEscala.computeBounds(boundsRectangulo, true)
            val scaleX = (bounds.width()+276) / boundsRectangulo.width()
            val scaleY = (bounds.height()+276) / boundsRectangulo.height()
            val scaleFactor = minOf(scaleX, scaleY)
            matrix.setScale(scaleFactor, scaleFactor, boundsRectangulo.centerX(), boundsRectangulo.centerY())
            rectanguloEscala.computeBounds(boundsRectangulo, true)
            val dx = (bounds.centerX() - boundsRectangulo.centerX())
            val dy = (bounds.centerY() - boundsRectangulo.centerY())
            matrix.postTranslate(dx, dy)
        }

        var count = 0
        for (path in paths){
            var dibujos = PathParser.createPathFromPathData(path)
            dibujos.transform(matrix)
            listLimpio.add(dibujos)
            var color = ColorBrush.colorList[count]
            color = ColorUtils.setAlphaComponent(color, (0.8f * 255).toInt())
            bPaint.color = color
            bPaint.strokeWidth = 7.2f
            count = (count + 1) % ColorBrush.colorList.size
            canvas.drawPath(dibujos, bPaint)
        }
        canvas.drawPath(cPath, cPaint)

        matrix.reset()
    }
    fun calcularTotalPixeles(tipoMapa:String): Map<String, List<Float>> {
        return InterpretationHelper.calcularTotalPixeles(width, height, listLimpio, bPaint, cPath, optimization=5, tipoMapa=tipoMapa)
    }
}