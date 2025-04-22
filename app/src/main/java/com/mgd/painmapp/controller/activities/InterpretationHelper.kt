package com.mgd.painmapp.controller.activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region

object InterpretationHelper {
    //Boton de guardar: debe guardar los dibujos del usuarios e interpretarlos en el canvas
    fun obtenerZonas(path: Path) : List<Pair<String, Region>> {
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


    fun calcularPorcentaje(width: Int, height: Int, bPaths: List<Path>, bPaint: Paint, cPath: Path, optimization: Int = 10): Map<String, Float> { //Map es lo que sería un diccionario en python. En vez de acceder con indice se accede con clave
        /*Optimizacion porque si se revisan todos los pixels la aplicacion se bloque y tarda demasiado
        Sigue detectando el trazo de un punto del pincel, si se perdiera precisión, elegiría hacerlo en un hilo o coroutine
         */
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvasCalculos = Canvas(bitmap) //Lo que se haga en canvasCalculos se estará haciendo también en el bitmap
        canvasCalculos.clipPath(cPath) //mascara para darle forma humana
        for (path in bPaths) { //trazos dibujados con las caracteristicas del pincel
            canvasCalculos.drawPath(path, bPaint)
        }
        val zonas = obtenerZonas(cPath)
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