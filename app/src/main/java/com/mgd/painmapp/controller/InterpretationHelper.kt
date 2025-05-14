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
import androidx.core.graphics.PathParser
import com.mgd.painmapp.R
import org.xmlpull.v1.XmlPullParser

object  InterpretationHelper {
    /*__________NERVES__________*/
        /*___Front___*/
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

        /*___Back___*/
    @SuppressLint("ResourceType")
    fun getBackPeripheralNerves(context:Context) : List<String> {
        val nerveNames = mutableListOf<String>()
        val imgSource:Int = R.drawable.back_nerves
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
        fun getBackNerves_Regions(context: Context, scale: Matrix):List<Pair<String,Path>>{
            val nerves = mutableListOf<Pair<String, Path>>()
            val imgSource:Int = R.drawable.back_nerves
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
                        nerves.add(Pair(nameAttr, path))
                        path.transform(scale)
                    }
                }
                eventType = parser.next()
            }
            return nerves
        }


    /*__________DERMATOMES__________*/
        /*___Front___*/
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
        /*___Back___*/
    @SuppressLint("ResourceType")
    fun getBackDermatomes(context:Context): List<String>{
        val dermatomeNames = mutableListOf<String>()
        val imgSource:Int = R.drawable.back_dermatomes
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
    fun getBackDermatomes_Regions(context: Context, scale: Matrix): MutableMap<String, Path>{
        val dermatomes = mutableMapOf<String, Path>()
        val imgSource:Int = R.drawable.back_dermatomes
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
        //Nervios perifericos
        for ((nameNerve, pathNerve) in getBackNerves_Regions(context, escala)){
            pathNerve.computeBounds(bounds, true)
            zones[nameNerve] = Region().apply {
                setPath(pathNerve, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            }
        }
        //Dermatomas
        for ((nameDermatome, pathDermatome) in getBackDermatomes_Regions(context, escala)){
            pathDermatome.computeBounds(bounds, true)
            zones[nameDermatome] = Region().apply {
                setPath(pathDermatome, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            }
        }
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
        var zones = if (tipoMapa=="frente"){
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
    fun calculatePercentage(mapResultsFront: Map<String, List<Float>>, mapResultsBack: Map<String, List<Float>>): Map<String, Float> {
        val mapTotals = mutableMapOf<String, List<Float>>()
        val resultPercentages = mutableMapOf<String, Float>()
        for (name in mapResultsFront.keys.intersect(mapResultsBack.keys)) { //claves en comun --> nervios en común, zonas en comun
            val frontResults = mapResultsFront[name]!!
            val backResults = mapResultsBack[name]!!
            val totalResults = frontResults.zip(backResults) { a, b -> a + b }
            mapTotals[name] = totalResults
            val total = totalResults[0]
            val pintado = totalResults[1]
            resultPercentages[name] =  pintado / total * 100f
        }

        for (name in mapResultsFront.keys - mapResultsBack.keys) { //claves (zonas y nervios) que solo existen en el mapa frontal
            val frontResults = mapResultsFront[name]!!
            val total = frontResults[0]
            val pintado = frontResults[1]
            resultPercentages[name] =  pintado / total * 100f
        }
        for (name in mapResultsBack.keys - mapResultsFront.keys) { //claves (zonas y nervios) que solo existen en el mapa de espalda
            val backResults = mapResultsBack[name]!!
            val total = backResults[0]
            val pintado = backResults[1]
            resultPercentages[name] =  pintado / total * 100f
        }
        return resultPercentages
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