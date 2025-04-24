package com.mgd.painmapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat.*
import androidx.core.graphics.PathParser
import com.mgd.painmapp.R
import com.mgd.painmapp.model.storage.getColorIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mgd.painmapp.model.storage.saveColorIndex

class MapViews(context: Context, attrs: AttributeSet) : MapResponsiveViews(context, attrs) {
    lateinit var paths: List<String>
    private fun crearDibujos(){

    }
    override fun onDraw(canvas: Canvas) {
        val bounds = RectF()
        val cDrawable = getDrawable(context, imgFuente)
        canvas.clipPath(cPath)
        cPath.computeBounds(bounds, true)
        cDrawable?.setBounds(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
        cDrawable?.draw(canvas)
        var count : Int = 0
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
            bPaint.color = colorList[count]
            count = (count + 1) % colorList.size
            canvas.drawPath(dibujoLimpio, bPaint)
        }
        canvas.drawPath(cPath, cPaint)
        CoroutineScope(Dispatchers.IO).launch {
            actualizarColor()
        }
    }

    private suspend fun actualizarColor() {
        //Funcion llamada para cambiar el color del pincel para MapResponsiveViews, pero lo hago aquí, porque prefiero que el color quede definido de forma definitiva antes de iniciar
        // LocationActivity (fundamentalmente para evitar gestión de hilos)
        if(imgFuente == R.drawable.front){ //Para asegurar que solo se calcula el color una vez (dado que hay dos mapviews en la misma actividad)
            colorIndex =  context.getColorIndex()
            bPaint.color = colorList[colorIndex]
            colorIndex = (colorIndex + 1) % colorList.size
            context.saveColorIndex(colorIndex)
            Log.d("colorIndex", colorIndex.toString())
        }
    }
}