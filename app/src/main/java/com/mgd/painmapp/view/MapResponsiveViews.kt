package com.mgd.painmapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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
import android.graphics.PathMeasure
import android.graphics.Region
import android.util.Log
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.InterpretationHelper
import com.mgd.painmapp.model.storage.ColorBrush
import com.mgd.painmapp.model.storage.ColorBrush.getColorIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class MapResponsiveViews(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //Variables para el pincel (brush)
    private var bPath : Path? = null
    private var bPaths = mutableListOf<Path>() //Son los trazos del pincel
    protected val bPaint: Paint = Paint().apply{ //Formato al trazo
        isAntiAlias = true //Bordes suavizados (evita pixelado)
        isDither = true //Mejora la calidad de los colores
        style = Paint.Style.STROKE //Hace formas, pero no pinta dentro de ellas
        strokeJoin = Paint.Join.ROUND //Las uniones entre lineas son redondeadas
        strokeCap = Paint.Cap.ROUND //Los extremos de las líneas son redondeados
        strokeWidth = 12f // PENSAR LO DEL SLIDE PARA EL TAMAÑO DEL PICEL. Si no hago zoom sobre el canvas, este tamaño esta bien, y no lo cambiaria
    }
    private var colorIndex = 0
    private val colorList = ColorBrush.colorList
    //Variables para crear el canva con forma humana
    private val bounds = RectF()
    val cPath = Path() //El trazo de la forma humana (en el xml del drawable seleccionado, será el primer path)
    protected val cPaint: Paint = Paint().apply{
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = getColor(context, R.color.black)
        strokeWidth = 1f
    }
    private val scaleMatrix = Matrix()

    //Variables para respuesta tactil
    private var bX = 0f
    private var bY = 0f

    //Para que sea reutilizable en frente y espalda
    protected var imgFuente: Int = 0
    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MapResponsiveViews)
        imgFuente = array.getResourceId(R.styleable.MapResponsiveViews_map, 0)
        array.recycle()
        CoroutineScope(Dispatchers.IO).launch {
            colorIndex =  context.getColorIndex()
            bPaint.color = colorList[colorIndex]
        }
    }

    override fun onSizeChanged(widthC: Int, heightC: Int, oldWidth: Int, oldHeight: Int) {
        Log.d("MapResponsiveViews", "Ancho: $oldWidth, $widthC, Alto: $oldHeight, $heightC")

        loadHumanCanvas()
        scaleAndCenterHumanCanvas()
    }

    //CANVAS HUMANO (svg importado al proyecto como vector --> primer path de su xml es el relevante
    private fun scaleAndCenterHumanCanvas() {
        cPath.computeBounds(bounds, true) // Dimensiones del path actual
        // Calcula la escala
        val scaleX = width / bounds.width()
        val scaleY = height / bounds.height()
        var scaleFactor = scaleY
        if(scaleFactor*bounds.width() > width){
            scaleFactor = scaleX
        }
        scaleMatrix.reset()
        scaleMatrix.setScale(scaleFactor, scaleFactor, bounds.centerX(), bounds.centerY())
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
    }

    //RESPUESTA TACTIL
    override fun onDraw(canvas: Canvas) {
        val cDrawable = getDrawable(context, imgFuente)
        canvas.save()
        canvas.clipPath(cPath)
        Log.d("thisondraw", "Ancho: $width, Alto: $height")
        cPath.computeBounds(bounds, true)
        cDrawable?.setBounds(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
        cDrawable?.draw(canvas)
        drawPathsOnCanvas(canvas)
        canvas.restore()
        /*for ((_, path) in InterpretationHelper.obtenerNerviosyRegionsFrente(context, scaleMatrix)) {
            canvas.drawPath(path, Paint().apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 1f
            })
        }*/
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
            MotionEvent.ACTION_DOWN -> touchDown(x, y) //Coloca el pincel en este punto
            MotionEvent.ACTION_MOVE -> touchMove(x, y) //Mueve el pincel
            MotionEvent.ACTION_UP -> touchUp(x, y) //Levanta el pincel
        }
        invalidate()
        return true
    }

    private fun touchDown(x: Float, y: Float) {
        bPath = Path()
        bPaths.add(bPath!!)
        bPath!!.apply { //bPath es un objeto mutable, por lo que la referencia presente de este objeto en la lista de paths también se modifica
            reset()
            moveTo(x, y) //Coloca el pincel en este punto
        }
        bX = x
        bY = y
    }

    private fun touchMove(x: Float, y: Float) {
        bPath?.quadTo(bX, bY, (x + bX) / 2, (y + bY) / 2) //Crea una curva suave entre los puntos
        bX = x //Sobreescribe la coordenada del punto inicial bX por la coordenada actual x
        bY = y
    }

    private fun touchUp(x: Float, y: Float) {
        bPath?.quadTo(bX, bY, (x + bX) / 2, (y + bY) / 2) //Finaliza de forma suave el trazo
        //bPath = null
    }

    //Boton de borrar. Llamado en LocationActivity
    fun deleteDrawing(){
        bPaths.clear()
        invalidate()
    }
    //Interpretación al guardar.
    fun calcularPixeles(tipoMapa:String): Map<String, List<Float>> {
        return InterpretationHelper.calcularPixeles(context, width, height, bPaths, bPaint, cPath, tipoMapa=tipoMapa, escala=scaleMatrix)
    }

    //SVG del dibujo
    fun pathToSVGString(): String {
        val sb = StringBuilder()
        val pos = FloatArray(2)
        for (bPath in bPaths) {
            val pm = PathMeasure(bPath, false)
            val length = pm.length
            val step = 5f
            var distance = 0f
            while (distance <= length) {
                pm.getPosTan(distance, pos, null)
                val cmd = if (distance == 0f) "M" else "L"
                sb.append("$cmd${pos[0]},${pos[1]} ")
                distance += step
            }
        }
        //Añado el recuadro de la view para que al reescarlarlo en SensorialActivity tenga en cuenta el recuadro en el que el dibujo del usuario se encuentra:

        val bounds = RectF()
        cPath.computeBounds(bounds, true)
        val xMin = bounds.left-150
        val yMin = bounds.top-150
        val xMax = bounds.right+150
        val yMax = bounds.bottom+150
        val boundsPath = "M${xMin},${yMin} L${xMax},${yMin} L${xMax},${yMax} L${xMin},${yMax} Z"
        sb.append(boundsPath)
        return sb.toString().trim()
    }

    fun validarMapa(): Boolean {
        return bPaths.isNotEmpty()
    }
}