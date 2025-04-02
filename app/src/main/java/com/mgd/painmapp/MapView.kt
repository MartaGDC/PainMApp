package com.mgd.painmapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat.*
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser


private const val PENCIL_STROKE_WIDTH = 8f

class MapView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //Variables para el pincel
    private var bPath: Path? = null //Guiado por las coordenadas del canva. Es el trazo
    private val bPaths = mutableListOf<Path>()
    private val bPaint: Paint = Paint() //Dar formato al trazo

    //Variables para crear el canva con forma humana
    private val cPath : Path = Path()

    //Variables para respuesta tactil
    private var bX = 0f
    private var mY = 0f

    //PINCEL
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initialize()
    }
    private fun initialize() {
        bPaint.isAntiAlias = true //Bordes suavizados (evita pixelado)
        bPaint.isDither = true //Mejora la calidad de los colores
        bPaint.style = Paint.Style.STROKE //Hace formas, pero no pinta dentro de ellas
        bPaint.strokeJoin = Paint.Join.ROUND //Las uniones entre lineas son redondeadas
        bPaint.strokeCap = Paint.Cap.ROUND //Los extremos de las líneas son redondeados
        setPen() //Funcion creda para definir el color y el ancho del trazo
    }
        private fun setPen() { //Modificar colores!!!!!!!!!!!!!!!!!!!!!!!!!!!!!___________________________________ PENSAR LO DEL SLIDE PARA EL TAMAÑO DEL PICEL
        bPaint.color = getColor(context, android.R.color.black)
        bPaint.strokeWidth = PENCIL_STROKE_WIDTH
    }


    //CANVAS HUMANO (svg importado al proyecto como vector --> primer path de su xml es el relevante
    private fun drawHumanCanvas(canvas: Canvas) {
        val pathData = getHumanCanvas(context, R.drawable.frente).toString()
        val paredPath = PathParser.createPathFromPathData(pathData)
        cPath.addPath(paredPath)
        Log.d("pathData", pathData)
        canvas.drawPath(cPath, bPaint)
    }
    private fun getHumanCanvas(context: Context, drawableId: Int): String? {
        val resources = context.resources
        val parser = resources.getXml(drawableId)
        try {
            var eventType = parser.eventType
            var pathData : String? = null
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
                    Log.d("Primer path", "Encontrado")
                    pathData = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData")
                    Log.d("Primer pathData", pathData.toString())
                    break
                }
                eventType = parser.next()
            }
            return pathData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //RESPUESTA TACTIL
    override fun onDraw(canvas: Canvas) {
        canvas.let {
            it.save()
            drawHumanCanvas(it)
            drawPathsOnCanvas(it)
            it.restore()
        }
    }
    private fun drawPathsOnCanvas(canvas: Canvas) {
        for(path in bPaths) {
            canvas.drawPath(path, bPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        val x = event.x //Coordenada x del toque
        val y = event.y //Coordenada y del toque

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchDown(x, y)
            MotionEvent.ACTION_MOVE -> touchMove(x, y)
            MotionEvent.ACTION_UP -> touchUp(x, y)
        }
        invalidate()
        return true
    }

    private fun touchDown(x: Float, y: Float) {
        bPath = Path()
        bPaths.add(bPath!!)
        bPath?.apply {
            reset()
            moveTo(x, y) //Coloca el pincel en este punto
        }
        bX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        bPath?.quadTo(bX, mY, (x + bX) / 2, (y + mY) / 2) //Crea una curva suave entre los puntos
        bX = x
        mY = y
    }

    private fun touchUp(x: Float, y: Float) {
        bPath?.quadTo(bX, mY, (x + bX) / 2, (y + mY) / 2) //Finaliza de forma suave el trazo
        bPath = null
    }
}