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
    fun obtenerNerviosyRegionsFrente(context: Context, escala: Matrix):List<Pair<String,Path>>{
        val nervios = mutableListOf<Pair<String, Path>>()
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
                    nervios.add(Pair(nameAttr, path))
                    path.transform(escala)
                }
            }
            eventType = parser.next()
        }
        return nervios
    }

    private fun obtenerZonasFrente(path:Path, context: Context, escala: Matrix): MutableList<Pair<String, Region>> {
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
        //Nervios perifericos
        for (nervio in obtenerNerviosyRegionsFrente(context, escala)){
            nervio.second.computeBounds(bounds, true)
            zonas.add(Pair(
                nervio.first, Region().apply {
                    setPath(nervio.second, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
                })
            )
        }

        //Dermatomas pendientes
        return zonas
    }

    private fun obtenerZonasEspalda(path:Path): MutableList<Pair<String, Region>> {
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
            "derechaEspalda", Region().apply {
                setPath(path, Region(mitadX, bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            })
        )
        zonas.add(Pair(
            "izquierdaEspalda", Region().apply {
                setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), mitadX, bounds.bottom.toInt()))
            })
        )
        //Dermatomas y nervios perifericos pendientes
        return zonas
    }


    fun calcularPorcentaje(context:Context, width: Int, height: Int, paths: List<Path>, bPaint: Paint, path: Path, optimization: Int = 5, tipoMapa:String, escala: Matrix): Map<String, Float> { //Map es lo que sería un diccionario en python. En vez de acceder con indice se accede con clave
        /*Optimizacion porque si se revisan todos los pixels la aplicacion se bloque y tarda demasiado
        Sigue detectando el trazo de un punto del pincel, si se perdiera precisión, elegiría hacerlo en un hilo o coroutine
         */
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvasCalculos = Canvas(bitmap) //Lo que se haga en canvasCalculos se estará haciendo también en el bitmap
        canvasCalculos.clipPath(path) //mascara para darle forma humana
        for (bpath in paths) { //trazos dibujados con las caracteristicas del pincel
            canvasCalculos.drawPath(bpath, bPaint)
        }
        var zonas: List<Pair<String, Region>>
        if (tipoMapa=="frente"){
            zonas = obtenerZonasFrente(path, context, escala)
        }
        else {
            zonas = obtenerZonasEspalda(path)
        }
        val pixelsZona = mutableMapOf<String, Int>()
        val pintadosZona = mutableMapOf<String, Int>()
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

        val mapResults = mutableMapOf<String, Float>()
        for (zona in zonas) {
            val total = pixelsZona[zona.first] ?: 1
            val pintado = pintadosZona[zona.first] ?: 0
            mapResults[zona.first] = pintado.toFloat() / total.toFloat() * 100f
        }
        return mapResults
    }
}