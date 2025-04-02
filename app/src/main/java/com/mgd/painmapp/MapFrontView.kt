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

class MapFrontView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //Variables para el pincel
    private var bPath : Path? = null
    private val bPaths = mutableListOf<Path>() //Guiado por las coordenadas del canva. Es el trazo
    private val bPaint: Paint = Paint().apply{ //Dar formato al trazo
        isAntiAlias = true //Bordes suavizados (evita pixelado)
        isDither = true //Mejora la calidad de los colores
        style = Paint.Style.STROKE //Hace formas, pero no pinta dentro de ellas
        strokeJoin = Paint.Join.ROUND //Las uniones entre lineas son redondeadas
        strokeCap = Paint.Cap.ROUND //Los extremos de las líneas son redondeados
        color = getColor(context, android.R.color.black) //Modificar colores!!!!!!!!!!!!!!!!!!!!!!!!!!!!!_
        strokeWidth = 12f // PENSAR LO DEL SLIDE PARA EL TAMAÑO DEL PICEL
    }
    //Variables para crear el canva con forma humana
    private val cPath = Path()
    private val scaleMatrix = Matrix()
    //Variables para respuesta tactil
    private var bX = 0f
    private var mY = 0f


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        loadHumanCanvas()
        scaleAndCenterHumanCanvas()
    }

    //CANVAS HUMANO (svg importado al proyecto como vector --> primer path de su xml es el relevante
    private fun scaleAndCenterHumanCanvas() {
        val bounds = RectF()
        cPath.computeBounds(bounds, true)  // Dimensiones del path actual
        // Calcula la escala
        val scaleX = width / bounds.width()
        val scaleY = height / bounds.height()
        var scaleFactor = scaleY*0.785f
        if(scaleFactor*bounds.width() > width){
            scaleFactor = scaleX
        }
        scaleMatrix.reset()
        scaleMatrix.setScale(scaleFactor, scaleFactor, bounds.centerX(), bounds.centerY())
        cPath.transform(scaleMatrix)

        // Desplazamiento necesario para centrar el path
        val dx = (width - bounds.width()) / 2 - bounds.left
        val dy = (height - bounds.height()) / 2 - bounds.top
        scaleMatrix.postTranslate(dx, dy)
        cPath.transform(scaleMatrix)
    }
    private fun loadHumanCanvas() {
        val pathData = getHumanCanvas(context, R.drawable.frente) ?: return
        val humanPath = PathParser.createPathFromPathData(pathData)
        cPath.set(humanPath)
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
        val cDrawable = context.getDrawable(R.drawable.frente)
        canvas.save()
        canvas.clipPath(cPath)
        val bounds = RectF()
        cPath.computeBounds(bounds, true)
        cDrawable?.setBounds(bounds.left.toInt()-20, bounds.top.toInt(), bounds.right.toInt()+20, bounds.bottom.toInt())
        cDrawable?.draw(canvas)

        drawPathsOnCanvas(canvas)
        canvas.restore()
        //canvas.drawPath(cPath, bPaint) //Borrar cuando tenga el dibujo del layout apañado
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