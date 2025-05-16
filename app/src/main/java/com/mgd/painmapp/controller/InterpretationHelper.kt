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
    private fun getElements(context: Context, imgSource:Int):MutableMap<String,String>{
        val xml_zone = mutableMapOf<String, String>()
        val resources = context.resources
        val parser = resources.getXml(imgSource)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
                val name = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android","name"
                )
                val pathData = parser.getAttributeValue(
                    "http://schemas.android.com/apk/res/android","pathData")
                if (!name.isNullOrEmpty()) {
                    xml_zone[name] = pathData
                }
            }
            eventType = parser.next()
        }
        return xml_zone
    }
    @SuppressLint("ResourceType")
    fun getRegions(context: Context, scale: Matrix, imgSource: Int):MutableMap<String,Path> {
        val nerves = mutableMapOf<String, Path>()
        val elementos = getElements(context, imgSource)
        for ((name, pathData) in elementos) {
            if (!pathData.isNullOrEmpty()) {
                val path = PathParser.createPathFromPathData(pathData)
                path.transform(scale)
                nerves[name] = path
            }
        }
        return nerves
    }

    @SuppressLint("ResourceType")
    private fun getFrontPeripheralNerves(context:Context) : List<String> {
        val imgSource:Int = R.drawable.front_nerves
        return getElements(context, imgSource).keys.toList()
    }
    @SuppressLint("ResourceType")
    private fun getBackPeripheralNerves(context:Context) : List<String> {
        val imgSource:Int = R.drawable.back_nerves
        return getElements(context, imgSource).keys.toList()
    }
    fun getPeripheralNerves(context:Context): List<String> {
        val nerveNames = (getFrontPeripheralNerves(context) + getBackPeripheralNerves(context)).toSet().toList() //To set elimina elementos duplicados.
        return nerveNames
    }

    @SuppressLint("ResourceType")
    private fun getFrontDermatomes(context: Context): List<String>{
        val imgSource:Int = R.drawable.front_dermatomes
        return getElements(context, imgSource).keys.toList()
    }
    private fun getBackDermatomes(context:Context): List<String>{
        val imgSource:Int = R.drawable.back_dermatomes
        return getElements(context, imgSource).keys.toList()
    }
    fun getDermatomes(context:Context): List<String> {
        val dermatomeNames = (getFrontDermatomes(context) + getBackDermatomes(context)).toSet().toList() //To set elimina elementos duplicados.
        return dermatomeNames
    }


    private fun getFrontZones(path:Path, context: Context, escala: Matrix): Map<String, Region> {
        val zones = mutableMapOf<String, Region>()
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val imgFrontNerves = R.drawable.front_nerves
        val imgFrontDermatomes = R.drawable.front_dermatomes

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
        for ((nameNerve, pathNerve) in getRegions(context, escala, imgFrontNerves)){
            pathNerve.computeBounds(bounds, true)
            zones[nameNerve] = Region().apply {
                setPath(pathNerve, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            }
        }
        //Dermatomas
        for ((nameDermatome, pathDermatome) in getRegions(context, escala, imgFrontDermatomes)){
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
        val imgBackNerves = R.drawable.back_nerves
        val imgBackDermatomes = R.drawable.back_dermatomes
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
        for ((nameNerve, pathNerve) in getRegions(context, escala, imgBackNerves)){
            pathNerve.computeBounds(bounds, true)
            zones[nameNerve] = Region().apply {
                setPath(pathNerve, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            }
        }
        //Dermatomas
        for ((nameDermatome, pathDermatome) in getRegions(context, escala, imgBackDermatomes)){
            pathDermatome.computeBounds(bounds, true)
            zones[nameDermatome] = Region().apply {
                setPath(pathDermatome, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            }
        }
        return zones
    }


    fun calculatePixels(context: Context, width: Int, height: Int, paths: List<Path>, bPaint: Paint, path: Path, optimization: Int = 10,
                        tipoMapa: String, escala: Matrix, tipoCalculo:String): Map<String, List<Float>> {
        /*Map es lo que sería un diccionario en python. En vez de acceder con indice se accede con clave
        Optimizacion porque si se revisan todos los pixels la aplicacion se bloque y tarda demasiado*/

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

        if(tipoCalculo!="zonas"){ //Calculo de todos los sintomas a la vez, solo se calcularán afectaciones generales no por zonas
            zones = zones.entries
                .take(3)
                .associate { it.toPair() }
        }
        for ((name, _) in zones){ //Se guardará en cada zona sus datos correspondientes en una sola lectura (código más adelante)
            pixelsZona[name] = 0 //inicializa la cuenta de pixeles cuya clave recibe el string (first) del primer par (zona) que contiene zonas
            pintadosZona[name] = 0
        }
        for (x in 0 until bitmap.width step optimization) {
            for (y in 0 until bitmap.height step optimization) {
                val pixel = bitmap.getPixel(x, y)
                for ((nameZona, regionZona) in zones) {
                    if (regionZona.contains(x, y)) {
                        pixelsZona[nameZona] = pixelsZona[nameZona]!! + 1
                        if (Color.alpha(pixel) != 0) {
                            pintadosZona[nameZona] = pintadosZona[nameZona]!! + 1
                        }
                    }
                }
            }
        }
        for ((name, pixels) in pixelsZona) {
            if (pixels==0){
                for(x in 0 until bitmap.width step 5) {
                    for (y in 0 until bitmap.height step 5) {
                        val pixel = bitmap.getPixel(x, y)
                        if(zones[name]?.contains(x,y) == true){
                            pixelsZona[name] = pixelsZona[name]!! + 1
                            if (Color.alpha(pixel) != 0) {
                                pintadosZona[name] = pintadosZona[name]!! + 1
                            }
                        }
                    }
                }
            }
        }


        val mapResults = mutableMapOf<String, List<Float>>()
        for ((name, _) in zones) {
            val total = pixelsZona[name] ?: 1 //para hacer toFloat hay que hacer tratamiento de nulos
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
}