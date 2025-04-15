package com.mgd.painmapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import com.mgd.painmapp.R
import com.mgd.painmapp.model.storage.getColorIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class MapResponsiveViews(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //Variables para el pincel (brush)
    private var bPath : Path? = null
    private var bPaths = mutableListOf<Path>() //Son los trazos del pince
    val bPaint: Paint = Paint().apply{ //Formato al trazo
        isAntiAlias = true //Bordes suavizados (evita pixelado)
        isDither = true //Mejora la calidad de los colores
        style = Paint.Style.STROKE //Hace formas, pero no pinta dentro de ellas
        strokeJoin = Paint.Join.ROUND //Las uniones entre lineas son redondeadas
        strokeCap = Paint.Cap.ROUND //Los extremos de las líneas son redondeados
        strokeWidth = 12f // PENSAR LO DEL SLIDE PARA EL TAMAÑO DEL PICEL. Si no hago zoom sobre el canvas, este tamaño esta bien, y no lo cambiaria
    }
    var colorIndex = 0
    val colorList = mutableListOf(
        getColor(context, R.color.dark_blue),
        getColor(context, R.color.blue),
        getColor(context, R.color.red),
        getColor(context, R.color.pink),
        getColor(context, R.color.yellow)
    )

    //Variables para crear el canva con forma humana
    private val bounds = RectF()
    val cPath = Path() //El trazo de la forma humana (en el xml del drawable seleccionado, será el primer path)
    val cPaint: Paint = Paint().apply{
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = getColor(context, R.color.black)
        strokeWidth = 1f
    }
    //Variables para respuesta tactil
    private var bX = 0f
    private var bY = 0f

    //Para que sea reutilizable en frente y espalda
    var imgFuente: Int = 0
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
        val scaleMatrix = Matrix()
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
        bPath = null
    }

    //Boton de borrar. Llamado en LocationActivity
    fun deleteDrawing(){
        bPaths.clear()
        invalidate()
    }

    //Boton de guardar: debe guardar los dibujos del usuarios e interpretarlos en el canvas
    private fun obtenerZonas(path: Path) : List<Pair<String, Region>> {
        val zonas = mutableListOf<Pair<String, Region>>()
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val mitadX = (bounds.right.toInt()+bounds.left.toInt())/2
        zonas.add(Pair(
            "total", Region().apply {
                setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            })
        )
        zonas.add(Pair(
            "derechaFrente", Region().apply {
                setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt()))
            })
        )
        zonas.add(Pair(
            "izquierdaFrente", Region().apply {
                setPath(path, Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            })
        )
        //El resto de las zonas si se añaden dermatomas
        return zonas
    }


    fun calcularPorcentaje(optimization: Int = 10, zonas:List<Pair<String, Region>> = obtenerZonas(cPath)): Map<String, Float> { //Map es lo que sería un diccionario en python. En vez de acceder con indice se accede con clave
        /*Optimizacion porque si se revisan todos los pixels la aplicacion se bloque y tarda demasiado
        Sigue detectando el trazo de un punto del pincel, si se perdiera precisión, elegiría hacerlo en un hilo o coroutine
         */
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvasCalculos = Canvas(bitmap) //Lo que se haga en canvasCalculos se estará haciendo también en el bitmap
        canvasCalculos.clipPath(cPath) //mascara para darle forma humana, pero aun así será necesario usar isPixelInHuman
        for (path in bPaths) { //trazos dibujados con las caracteristicas del pincel
            canvasCalculos.drawPath(path, bPaint)
        }

        var pixelsZona = mutableMapOf<String, Int>()
        var pintadosZona = mutableMapOf<String,Int>()
        for (zona in zonas){ //Se guardará en cada zona sus datos correspondientes en una sola lectura (código más adelante)
            pixelsZona[zona.first] = 0 //inicializa la cuenta de pixeles cuya clave recibe el string (first) del primer par (zona) que contiene zonas
            pintadosZona[zona.first] = 0
        }

        for (x in 0 until bitmap.width step optimization) {
            for (y in 0 until bitmap.height step optimization) {
                val pixel = bitmap.getPixel(x, y)
                for (zona in zonas) {
                    if (zona.second.contains(x, y)) {
                        pixelsZona[zona.first] = pixelsZona[zona.first]!! + 1
                        if (Color.alpha(pixel) != 0) {
                            pintadosZona[zona.first] = pintadosZona[zona.first]!! + 1
                        }
                    }
                }
            }
        }
        val mapResults: MutableMap<String, Float> = mutableMapOf()
        for (zona in zonas) {
            val total = pixelsZona[zona.first] ?: 1
            val pintado = pintadosZona[zona.first] ?: 0
            mapResults[zona.first] = pintado.toFloat() / total.toFloat() * 100f
        }
        return mapResults
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
        //Añado el recuadro del canvas para que al reescarlarlo en SensorialActivity tenga en cuenta el recuadro en el que el dibujo del usuario se encuentra:
        cPath.computeBounds(bounds, true)
        val xMin = bounds.left
        val yMin = bounds.top
        val xMax = bounds.right
        val yMax = bounds.bottom
        val boundsPath = "M${xMin},${yMin} L${xMax},${yMin} L${xMax},${yMax} L${xMin},${yMax} Z"
        sb.append(boundsPath)
        return sb.toString().trim()
    }
}