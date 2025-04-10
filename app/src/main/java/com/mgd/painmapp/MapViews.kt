package com.mgd.painmapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat.*
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Region
import android.util.Log


class MapViews(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //Variables para el pincel
    private var bPath : Path? = null
    private val bPaths = mutableListOf<Path>() //Guiado por las coordenadas del canva. Es el trazo
    private val bPaint: Paint = Paint().apply{ //Dar formato al trazo
        isAntiAlias = true //Bordes suavizados (evita pixelado)
        isDither = true //Mejora la calidad de los colores
        style = Paint.Style.STROKE //Hace formas, pero no pinta dentro de ellas
        strokeJoin = Paint.Join.ROUND //Las uniones entre lineas son redondeadas
        strokeCap = Paint.Cap.ROUND //Los extremos de las líneas son redondeados
        color = getColor(context, R.color.dark_blue) //Modificar colores!!!!!!!!!!!!!!!!!!!!!!!!!!!!!_
        strokeWidth = 12f // PENSAR LO DEL SLIDE PARA EL TAMAÑO DEL PICEL. Si no hago zoom sobre el canvas, este tamaño esta bien, y no lo cambiaria
    }
    //Variables para crear el canva con forma humana
    private val bounds = RectF()
    private val cPath = Path()
    private val scaleMatrix = Matrix()
    private val cPaint: Paint = Paint().apply{ //Dar formato al trazo
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = getColor(context, R.color.black)
        strokeWidth = 1f
    }
    //Variables para respuesta tactil
    private var bX = 0f
    private var mY = 0f

    //Para que sea reutilizable en frente y espalda
    private var imgFuente: Int = 0
    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MapViews)
        imgFuente = array.getResourceId(R.styleable.MapViews_map, 0)
        array.recycle()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        loadHumanCanvas()
        scaleAndCenterHumanCanvas()
    }

    //CANVAS HUMANO (svg importado al proyecto como vector --> primer path de su xml es el relevante
    private fun scaleAndCenterHumanCanvas() {
        cPath.computeBounds(bounds, true) // Dimensiones del path actual
        // Calcula la escala
        val scaleX = width / bounds.width() *0.75f
        val scaleY = height / bounds.height()*0.75f
        var scaleFactor = scaleY
        if(scaleFactor*bounds.width() > width){
            scaleFactor = scaleX
        }
        scaleMatrix.reset()
        scaleMatrix.setScale(scaleFactor, scaleFactor, bounds.centerX(), bounds.centerY())
        cPath.transform(scaleMatrix)
        cPath.computeBounds(bounds, true)
        // Desplazamiento necesario para centrar el path
        val dx = (width - bounds.width()) / 2 - bounds.left
        val dy = (height - bounds.height()) / 2 - bounds.top
        scaleMatrix.postTranslate(dx, dy)
        cPath.transform(scaleMatrix)
    }

    private fun loadHumanCanvas() {
        val pathData = getHumanCanvas(context, imgFuente) ?: return
        val humanPath = PathParser.createPathFromPathData(pathData)
        cPath.set(humanPath)
    }

    private fun getHumanCanvas(context: Context, imgFuente: Int): String? {
        val resources = context.resources
        val parser = resources.getXml(imgFuente)
        try {
            var eventType = parser.eventType
            var pathData : String? = null
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
                    pathData = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData")
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
        val cDrawable = getDrawable(context, imgFuente)
        canvas.save()
        canvas.clipPath(cPath)
        cPath.computeBounds(bounds, true)
        cDrawable?.setBounds(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
        cDrawable?.draw(canvas)

        drawPathsOnCanvas(canvas)
        canvas.restore()
        canvas.drawPath(cPath, cPaint)
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

    fun deleteDrawing(){
        bPaths.clear()
        invalidate()
    }

    fun calcularPorcentaje(optimization: Int = 10, calculo:String): Float {
        //Optimizacion porque si se revisan todos los pixels la aplicacion se bloque y tarda demasiado
        //Sigue detectando el trazo de un punto del pincel, si se perdiera precisión, elegiría hacerlo en un hilo o coroutine
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvasCalculos = Canvas(bitmap) //Lo que se haga en canvasCalculos se estará haciendo también en el bitmap
        canvasCalculos.clipPath(cPath) //mascara para darle forma humana, pero aun así será necesario usar isPixelInHuman
        for (path in bPaths) { //trazos dibujados con las caracteristicas del pincel
            canvasCalculos.drawPath(path, bPaint)
        }
        var pixelsPintados = 0
        var pixelsCanvas = 0
        for (x in 0 until bitmap.width step optimization) {
            for (y in 0 until bitmap.height step optimization) {
                when(calculo) {
                    "total" ->
                        if (isPixelInHuman(cPath, x, y)) {
                            pixelsCanvas++
                            val pixel = bitmap.getPixel(x, y)
                            if (Color.alpha(pixel) != 0) { //pixels con color (trazos del usuario)
                                pixelsPintados++
                            }
                        }

                    "derecha" ->
                        if (isPixelInRightHumanFront(cPath, x, y)) {
                            pixelsCanvas++
                            val pixel = bitmap.getPixel(x, y)
                            if (Color.alpha(pixel) != 0) {
                                pixelsPintados++
                            }
                        }

                    "izquierda" ->
                        if (isPixelInLeftHumanFront(cPath, x, y)) {
                            pixelsCanvas++
                            val pixel = bitmap.getPixel(x, y)
                            if (Color.alpha(pixel) != 0) {
                                pixelsPintados++
                            }
                        }

                }
            }
        }
        return pixelsPintados.toFloat() / pixelsCanvas.toFloat() * 100f
    }


    private fun isPixelInHuman(path: Path, x: Int, y: Int): Boolean {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val region = Region().apply { //contruye region con los paths de cPaths
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        return region.contains(x, y) //devuelve si el pixel xy está dentro de la region
    }

    private fun isPixelInRightHumanFront(path: Path, x: Int, y: Int): Boolean {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val region = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), (bounds.right.toInt()+bounds.left.toInt())/2, bounds.bottom.toInt()))
        }
        return region.contains(x, y)
    }
    private fun isPixelInLeftHumanFront(path: Path, x: Int, y: Int): Boolean {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val region = Region().apply {
            setPath(path, Region((bounds.right.toInt()+bounds.left.toInt())/2, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        return region.contains(x, y)
    }

}