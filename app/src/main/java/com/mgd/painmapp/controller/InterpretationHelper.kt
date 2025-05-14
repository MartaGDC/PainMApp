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
import org.xmlpull.v1.XmlPullParser

object  InterpretationHelper {
    @SuppressLint("ResourceType")
    fun getFrontPeripheralNerves(context:Context) : List<String> {
        val nerveNames = mutableListOf<String>()
        val imgSource:Int = R.drawable.front_nerves
        val resources = context.resources
        val parser = resources.getXml(imgSource)
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
    fun getFrontDermatomes(context:Context): List<String>{
        val dermatomeNames = mutableListOf<String>()
        val imgSource:Int = R.drawable.front_dermatomes
        val resources = context.resources
        val parser = resources.getXml(imgSource)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
                val dermatome = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android",
                    "name"
                )
                if (!dermatome.isNullOrEmpty()) {
                    dermatomeNames.add(dermatome)
                }
            }
            eventType = parser.next()
        }
        return dermatomeNames
    }
    
    @SuppressLint("ResourceType")
    fun getFrontNerves_Regions(context: Context, scale: Matrix):MutableMap<String,Path>{
        val nerves = mutableMapOf<String, Path>()
        val imgSource:Int = R.drawable.front_nerves
        val resources = context.resources
        val parser = resources.getXml(imgSource)
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
                    nerves[nameAttr] = path
                    path.transform(scale)
                }
            }
            eventType = parser.next()
        }
        return nerves
    }
    
    @SuppressLint("ResourceType")
    fun getFrontDermatomes_Regions(context: Context, scale: Matrix): MutableMap<String, Path>{
        val dermatomes = mutableMapOf<String, Path>()
        val imgSource:Int = R.drawable.front_dermatomes
        val resources = context.resources
        val parser = resources.getXml(imgSource)
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
                    dermatomes[nameAttr] = path
                    path.transform(scale)
                }
            }
            eventType = parser.next()
        }
        return dermatomes
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


    private fun getFrontZones(path:Path, context: Context, escala: Matrix): Map<String, Region> {
        val zones = mutableMapOf<String, Region>()
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val mitadX = (bounds.right.toInt()+bounds.left.toInt())/2
        zones["total"] = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        zones["derecha"] = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt()))
        }
        zones["izquierda"] = Region().apply {
            setPath(path, Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        //Nervios perifericos
        for ((nameNerve, pathNerve) in getFrontNerves_Regions(context, escala)){
            pathNerve.computeBounds(bounds, true)
            zones[nameNerve] = Region().apply {
                setPath(pathNerve, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            }
        }
        //Dermatomas
        for ((nameDermatome, pathDermatome) in getFrontDermatomes_Regions(context, escala)){
            pathDermatome.computeBounds(bounds, true)
            zones[nameDermatome] = Region().apply {
                setPath(pathDermatome, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            }
        }
        return zones
    }

    private fun getBackZones(path:Path, context: Context, escala: Matrix): MutableMap<String, Region> {
        val zones = mutableMapOf<String, Region>()
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val mitadX = (bounds.right.toInt()+bounds.left.toInt())/2
        zones["total"] = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        zones["derecha"] = Region().apply {
            setPath(path, Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        }
        zones["izquierda"] = Region().apply {
            setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt()))
        }
        //Dermatomas y nervios perifericos pendientes
        return zones
    }


    fun calculatePixels(context: Context, width: Int, height: Int, paths: List<Path>, bPaint: Paint, path: Path, optimization: Int = 5,
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
        val zones: Map<String, Region>
        zones = if (tipoMapa=="frente"){
            getFrontZones(path, context, escala)
        } else {
            getBackZones(path, context, escala)
        }
        val pixelsZona = mutableMapOf<String, Int>()
        val pintadosZona = mutableMapOf<String, Int>()
        for ((name, _) in zones){ //Se guardará en cada zona sus datos correspondientes en una sola lectura (código más adelante)
            pixelsZona[name] = 0 //inicializa la cuenta de pixeles cuya clave recibe el string (first) del primer par (zona) que contiene zonas
            pintadosZona[name] = 0
        }
        for (x in 0 until bitmap.width step optimization) {
            for (y in 0 until bitmap.height step optimization) {
                val pixel = bitmap.getPixel(x, y)
                for ((nameZona, pathZona) in zones) {
                    if (pathZona.contains(x, y)) {
                        pixelsZona[nameZona] = pixelsZona[nameZona]!! + 1
                        if (Color.alpha(pixel) != 0) {
                            pintadosZona[nameZona] = pintadosZona[nameZona]!! + 1
                        }
                    }
                }
            }
        }

        val mapResults = mutableMapOf<String, List<Float>>()
        for ((name, _) in zones) {
            val total = pixelsZona[name] ?: 1
            val pintado = pintadosZona[name] ?: 0
            mapResults[name] = listOf(total.toFloat(), pintado.toFloat())
        }
        return mapResults
    }
    fun calculatePercentage(mapResultsFrente: Map<String, List<Float>>, mapResultsEspalda: Map<String, List<Float>>): Map<String, Float> {
        val mapTotales = mutableMapOf<String, List<Float>>()
        val resultadoPorcentajes = mutableMapOf<String, Float>()
        for (name in mapResultsFrente.keys.intersect(mapResultsEspalda.keys)) { //claves en comun --> nervios en común, zonas en comun
            val resultadosFrente = mapResultsFrente[name]!!
            val resultadosEspalda = mapResultsEspalda[name]!!
            val resultadosTotales = resultadosFrente.zip(resultadosEspalda) { a, b -> a + b }
            mapTotales[name] = resultadosTotales
            val total = resultadosTotales[0]
            val pintado = resultadosTotales[1]
            resultadoPorcentajes[name] =  pintado / total * 100f
        }

        for (name in mapResultsFrente.keys - mapResultsEspalda.keys) {
            val resultadosFrente = mapResultsFrente[name]!!
            val total = resultadosFrente[0]
            val pintado = resultadosFrente[1]
            resultadoPorcentajes[name] =  pintado / total * 100f
        }
        for (name in mapResultsEspalda.keys - mapResultsFrente.keys) {
            val resultadosEspalda = mapResultsEspalda[name]!!
            val total = resultadosEspalda[0]
            val pintado = resultadosEspalda[1]
            resultadoPorcentajes[name] =  pintado / total * 100f
        }
        return resultadoPorcentajes
    }

    fun calculateTotalPixels(width: Int, height: Int, paths: List<Path>, bPaint: Paint, cPath: Path, optimization: Int = 5,
                             tipoMapa:String) : Map<String, List<Float>> {
        val mapResults = mutableMapOf<String, List<Float>>()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvasCalculos = Canvas(bitmap)
        canvasCalculos.clipPath(cPath)
        for (bpath in paths) {
            canvasCalculos.drawPath(bpath, bPaint)
        }
        val zones = mutableMapOf<String, Region>()
        if (tipoMapa == "frente") {
            val bounds = RectF()
            cPath.computeBounds(bounds, true)
            val mitadX = (bounds.right.toInt() + bounds.left.toInt()) / 2
            zones["total"] = Region().apply {
                setPath(
                    cPath,
                    Region(
                        bounds.left.toInt(),
                        bounds.top.toInt(),
                        bounds.right.toInt(),
                        bounds.bottom.toInt()
                    )
                )
            }
            zones["derecha"] = Region().apply {
                setPath(
                    cPath,
                    Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt())
                )
            }
            zones["izquierda"] = Region().apply {
                setPath(
                    cPath,
                    Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
                )
            }
        } else {
            val bounds = RectF()
            cPath.computeBounds(bounds, true)
            val mitadX = (bounds.right.toInt() + bounds.left.toInt()) / 2
            zones["total"] = Region().apply {
                setPath(
                    cPath,
                    Region(
                        bounds.left.toInt(),
                        bounds.top.toInt(),
                        bounds.right.toInt(),
                        bounds.bottom.toInt()
                    )
                )
            }
            zones["derecha"] = Region().apply {
                setPath(
                    cPath,
                    Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
                )
            }
            zones["izquierda"] = Region().apply {
                setPath(
                    cPath,
                    Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt())
                )
            }
        }
        val pixelsZona = mutableMapOf<String, Int>()
        val pintadosZona = mutableMapOf<String, Int>()
        for ((name, _) in zones) {
            pixelsZona[name] = 0
            pintadosZona[name] = 0
        }
        for (x in 0 until bitmap.width step optimization) {
            for (y in 0 until bitmap.height step optimization) {
                val pixel = bitmap.getPixel(x, y)
                for ((name, path) in zones) {
                    if (path.contains(x, y)) {
                        pixelsZona[name] = pixelsZona[name]!! + 1
                        if (Color.alpha(pixel) != 0) {
                            pintadosZona[name] = pintadosZona[name]!! + 1
                        }
                    }
                }
            }
        }
        for ((name, _) in zones) {
            val total = pixelsZona[name] ?: 1
            val pintado = pintadosZona[name] ?: 0
            mapResults[name] = listOf(total.toFloat(), pintado.toFloat())
        }
        return mapResults
    }
}