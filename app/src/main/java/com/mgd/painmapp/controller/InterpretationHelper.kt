package com.mgd.painmapp.controller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.util.Log
import androidx.core.graphics.PathParser
import com.mgd.painmapp.R
import com.mgd.painmapp.model.database.PatientDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser

object InterpretationHelper {
    @SuppressLint("ResourceType")
    fun obtenerNerviosPerifericosFrente(context:Context) : List<String> {
        val nerveNames = mutableListOf<String>()
        val imgFuente:Int = R.drawable.nerviosanterior
        val resources = context.resources
        val parser = resources.getXml(imgFuente)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
                val nerve = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android",
                    "name"
                )
                if (!nerve.isNullOrEmpty()){
                    nerveNames.add(nerve)
                }
            }
            eventType = parser.next()
        }
        return nerveNames
    }
    @SuppressLint("ResourceType")
    fun obtenerNerviosyRegionsFrente(context: Context, escala: Matrix):MutableMap<String,Path>{
        val nervios = mutableMapOf<String, Path>()
        val imgFuente:Int = R.drawable.nerviosanterior
        val resources = context.resources
        val parser = resources.getXml(imgFuente)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
                val nameAttr = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android",
                    "name"
                ) // XML con el atributo nombre --> pertenece a nervio
                val pathData = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android",
                    "pathData"
                )
                if (!nameAttr.isNullOrEmpty()) {
                    val path = PathParser.createPathFromPathData(pathData)
                    nervios[nameAttr] = path
                    path.transform(escala)
                }
            }
            eventType = parser.next()
        }
        return nervios
    }
    @SuppressLint("ResourceType")
    /* fun obtenerNerviosyRegionsEspalda(context: Context, escala: Matrix):List<Pair<String,Path>>{
        val nervios = mutableListOf<Pair<String, Path>>()
        val imgFuente:Int = R.drawable.nerviosposterior
        val resources = context.resources
        val parser = resources.getXml(imgFuente)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
                val nameAttr = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android",
                    "name"
                )
                val pathData = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android",
                    "pathData"
                )
                if (!nameAttr.isNullOrEmpty()) {
                    val path = PathParser.createPathFromPathData(pathData)
                    nervios.add(Pair(nameAttr, path))
                    path.transform(escala)
                }
            }
            eventType = parser.next()
        }
        return nervios
    } */

    private fun obtenerZonasFrente(path:Path, context: Context, escala: Matrix): Map<String, Region> {
        val zonas = mutableMapOf<String, Region>()
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val mitadX = (bounds.right.toInt()+bounds.left.toInt())/2
        zonas["total"] = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        zonas["derecha"] = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt()))
        }
        zonas["izquierda"] = Region().apply {
            setPath(path, Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        //Nervios perifericos
        for ((name, path) in obtenerNerviosyRegionsFrente(context, escala)){
            path.computeBounds(bounds, true)
            zonas[name] = Region().apply {
                setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            }
        }
        //Dermatomas pendientes
        return zonas
    }

    private fun obtenerZonasEspalda(path:Path, context: Context, escala: Matrix): MutableMap<String, Region> {
        val zonas = mutableMapOf<String, Region>()
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val mitadX = (bounds.right.toInt()+bounds.left.toInt())/2
        zonas["total"] = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        zonas["derecha"] = Region().apply {
            setPath(path, Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        zonas["izquierda"] = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt()))
        }
        //Dermatomas y nervios perifericos pendientes
        return zonas
    }


    fun calcularPixeles(context: Context, width: Int, height: Int, paths: List<Path>, bPaint: Paint, path: Path, optimization: Int = 5,
                        tipoMapa: String, escala: Matrix): Map<String, List<Float>> {
                           //Map es lo que sería un diccionario en python. En vez de acceder con indice se accede con clave
        /*Optimizacion porque si se revisan todos los pixels la aplicacion se bloque y tarda demasiado
        Sigue detectando el trazo de un punto del pincel, si se perdiera precisión, elegiría hacerlo en un hilo o coroutine
         */
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvasCalculos = Canvas(bitmap) //Lo que se haga en canvasCalculos se estará haciendo también en el bitmap
        canvasCalculos.clipPath(path) //mascara para darle forma humana
        for (bpath in paths) { //trazos dibujados con las caracteristicas del pincel
            canvasCalculos.drawPath(bpath, bPaint)
        }
        var zonas: Map<String, Region>
        if (tipoMapa=="frente"){
            zonas = obtenerZonasFrente(path, context, escala)
        }
        else {
            zonas = obtenerZonasEspalda(path, context, escala)
        }
        val pixelsZona = mutableMapOf<String, Int>()
        val pintadosZona = mutableMapOf<String, Int>()
        for ((name, path) in zonas){ //Se guardará en cada zona sus datos correspondientes en una sola lectura (código más adelante)
            pixelsZona[name] = 0 //inicializa la cuenta de pixeles cuya clave recibe el string (first) del primer par (zona) que contiene zonas
            pintadosZona[name] = 0
        }
        for (x in 0 until bitmap.width step optimization) {
            for (y in 0 until bitmap.height step optimization) {
                val pixel = bitmap.getPixel(x, y)
                for ((name, path) in zonas) {
                    if (path.contains(x, y)) {
                        pixelsZona[name] = pixelsZona[name]!! + 1
                        if (Color.alpha(pixel) != 0) {
                            pintadosZona[name] = pintadosZona[name]!! + 1
                        }
                    }
                }
            }
        }

        val mapResults = mutableMapOf<String, List<Float>>()
        for ((name, _) in zonas) {
            val total = pixelsZona[name] ?: 1
            val pintado = pintadosZona[name] ?: 0
            mapResults[name] = listOf(total.toFloat(), pintado.toFloat())
        }
        return mapResults
    }
    fun calcularPorcentaje(mapResultsFrente: Map<String, List<Float>>, mapResultsEspalda: Map<String, List<Float>>): Map<String, Float> {
        var mapTotales = mutableMapOf<String, List<Float>>()
        var resultadoPorcentajes = mutableMapOf<String, Float>()
        for (name in mapResultsFrente.keys.intersect(mapResultsEspalda.keys)) { //claves en comun --> nervios en común, zonas en comun
            val resultadosFrente = mapResultsFrente[name]!!
            val resultadosEspalda = mapResultsEspalda[name]!!
            val resultadosTotales = resultadosFrente.zip(resultadosEspalda) { a, b -> a + b }
            mapTotales[name] = resultadosTotales
            val total = resultadosTotales[0]
            val pintado = resultadosTotales[1]
            resultadoPorcentajes[name] = pintado / total * 100f
        }

        for (name in mapResultsFrente.keys - mapResultsEspalda.keys) {
            val resultadosFrente = mapResultsFrente[name]!!
            val total = resultadosFrente[0]
            val pintado = resultadosFrente[1]
            resultadoPorcentajes[name] = pintado / total * 100f
        }
        for (name in mapResultsEspalda.keys - mapResultsFrente.keys) {
            val resultadosEspalda = mapResultsEspalda[name]!!
            val total = resultadosEspalda[0]
            val pintado = resultadosEspalda[1]
            resultadoPorcentajes[name] = pintado / total * 100f
        }
        return resultadoPorcentajes
    }

    fun calcularTotalPixeles(database: PatientDatabase, cPath: Path, width: Int, height: Int, bPaint: Paint, tipoMapa:String,
                             optimization: Int = 5) : Map<String, List<Float>> {
        val mapResults = mutableMapOf<String, List<Float>>()
        CoroutineScope(Dispatchers.IO).launch {
            var stringFrontPaths = listOf<String>()
            if (tipoMapa=="frente"){
                stringFrontPaths = database.getMapDao().getFrontPathsDrawn()
            }
            else{
                stringFrontPaths = database.getMapDao().getBackPathsDrawn()
            }
            val combinedPathData = StringBuilder()
            for (path in stringFrontPaths){
                val dibujosSinRect = path.replace(Regex("M[\\d.,\\s]+L[\\d.,\\s]+L[\\d.,\\s]+L[\\d.,\\s]+Z"), "") //Expresion regex M con digito, punto, coma o espacio + ... (Doble \ para escape)
                combinedPathData.append(dibujosSinRect)
            }
            Log.d("combinedPathData", combinedPathData.toString())
            val combinedPath = PathParser.createPathFromPathData(combinedPathData.toString())
            val bounds = RectF()
            cPath.computeBounds(bounds, true) //Para tener el ancho real del rectangulo mostrado en pantalla (variable segun la activity y el layout)
            var dibujos = PathParser.createPathFromPathData(stringFrontPaths[0]) //Para tener ancho del rectangulo tal y como se ha guardado
            val boundsDibujo = RectF()
            dibujos.computeBounds(boundsDibujo, true)
            val scaleX = bounds.width() / boundsDibujo.width()
            val scaleY = bounds.height() / boundsDibujo.height()
            val scaleFactor = minOf(scaleX, scaleY)
            val matrix =Matrix()
            matrix.setScale(scaleFactor, scaleFactor, boundsDibujo.centerX(), boundsDibujo.centerY())
            combinedPath.transform(matrix)
            dibujos.computeBounds(boundsDibujo, true)
            val dx = (bounds.centerX() - boundsDibujo.centerX())
            val dy = (bounds.centerY() - boundsDibujo.centerY())
            matrix.reset()
            matrix.setTranslate(dx, dy)
            combinedPath.transform(matrix)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val canvasCalculos = Canvas(bitmap)
            canvasCalculos.clipPath(cPath)
            canvasCalculos.drawPath(combinedPath, bPaint)
            var zonas = mutableMapOf<String, Region>()
            if (tipoMapa=="frente"){
                val bounds = RectF()
                cPath.computeBounds(bounds, true)
                val mitadX = (bounds.right.toInt()+bounds.left.toInt())/2
                zonas["total"] = Region().apply {
                    setPath(cPath, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
                }
                zonas["derecha"] = Region().apply {
                    setPath(cPath, Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt()))
                }
                zonas["izquierda"] = Region().apply {
                    setPath(cPath, Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
                }
            }
            else {
                val bounds = RectF()
                cPath.computeBounds(bounds, true)
                val mitadX = (bounds.right.toInt()+bounds.left.toInt())/2
                zonas["total"] = Region().apply {
                    setPath(cPath, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
                }
                zonas["derecha"] = Region().apply {
                    setPath(cPath, Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
                }
                zonas["izquierda"] = Region().apply {
                    setPath(cPath, Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt()))
                }
            }
            val pixelsZona = mutableMapOf<String, Int>()
            val pintadosZona = mutableMapOf<String, Int>()
            for ((name, _) in zonas){
                pixelsZona[name] = 0
                pintadosZona[name] = 0
            }
            for (x in 0 until bitmap.width step optimization) {
                for (y in 0 until bitmap.height step optimization) {
                    val pixel = bitmap.getPixel(x, y)
                    for ((name, path) in zonas) {
                        if (path.contains(x, y)) {
                            pixelsZona[name] = pixelsZona[name]!! + 1
                            if (Color.alpha(pixel) != 0) {
                                pintadosZona[name] = pintadosZona[name]!! + 1
                            }
                        }
                    }
                }
            }
            for ((name, _) in zonas) {
                val total = pixelsZona[name] ?: 1
                val pintado = pintadosZona[name] ?: 0
                mapResults[name] = listOf(total.toFloat(), pintado.toFloat())
            }
        }
        return mapResults
    }
}