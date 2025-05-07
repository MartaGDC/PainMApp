package com.mgd.painmapp.controller.activities

import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.room.Room
import com.mgd.painmapp.controller.InterpretationHelper
import com.mgd.painmapp.model.database.entities.toDatabase
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.MapInterpretation
import com.mgd.painmapp.databinding.ActivityLocationBinding
import com.mgd.painmapp.model.storage.ColorBrush
import com.mgd.painmapp.model.storage.ColorBrush.getColorIndex
import com.mgd.painmapp.model.storage.ColorBrush.saveColorIndex
import com.mgd.painmapp.view.MapResponsiveViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var cvSave: CardView
    private lateinit var cvDelete: CardView
    private lateinit var mrvFront: MapResponsiveViews
    private lateinit var mrvBack: MapResponsiveViews
    private var idGeneradoEvaluation: Long = -1
    private var idGeneradoMap: Long = -1
    private lateinit var database: PatientDatabase
    private var porcentajeTotal: Float = 0f
    private var porcentajedchaTotal: Float = 0f
    private var porcentajeizdaTotal: Float = 0f
    private lateinit var nerviosNombres: List<String>
    private val nervios: MutableMap<String, Float> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idGeneradoEvaluation = intent.getLongExtra("idGeneradoEvaluation", -1)
        CoroutineScope(Dispatchers.IO).launch {
            saveColorIndex((getColorIndex()+1) % ColorBrush.colorList.size)
        }
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()

        nerviosNombres = InterpretationHelper.obtenerNerviosPerifericosFrente(this)
        initComponents()
        initListeners()
    }

    private fun initComponents() {
        cvSave = binding.cvSave
        cvDelete = binding.cvDelete
        mrvFront = binding.mrvFront
        mrvBack = binding.mrvBack
    }

    private fun initListeners() {
        cvSave.setOnClickListener {
            if (!mrvFront.validarMapa() && !mrvBack.validarMapa()) {
                Toast.makeText(this,"Debe dibujar la localización del síntoma",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                Toast.makeText(this,"Calculando...", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch { //Creamos aqui la coroutine, llamando a una funcion suspend
                    fillDatabase()
                    val intent = Intent(this@LocationActivity, SensorialSurveyActivity::class.java).apply {
                        putExtra("idGeneradoMap", idGeneradoMap)
                        putExtra("idGeneradoEvaluation", idGeneradoEvaluation)
                    }
                    startActivity(intent)
                }
            }
        }

        cvDelete.setOnClickListener {
            mrvFront.deleteDrawing()
            mrvBack.deleteDrawing()
        }
    }

    private suspend fun fillDatabase() {
        mapCalculate()
        val mapEntity = MapInterpretation(
            idGeneradoEvaluation,
            mrvFront.pathToSVGString(),
            mrvBack.pathToSVGString(),
            null,
            null,
            null,
            porcentajeTotal,
            porcentajedchaTotal,
            porcentajeizdaTotal,
            nervios[nerviosNombres[0]] ?: 0f,
            nervios[nerviosNombres[1]] ?: 0f,
            nervios[nerviosNombres[2]] ?: 0f,
            nervios[nerviosNombres[3]] ?: 0f,
            nervios[nerviosNombres[4]] ?: 0f,
            nervios[nerviosNombres[5]] ?: 0f,
            nervios[nerviosNombres[6]] ?: 0f,
            nervios[nerviosNombres[7]] ?: 0f,
            nervios[nerviosNombres[8]] ?: 0f,
            nervios[nerviosNombres[9]] ?: 0f,
            nervios[nerviosNombres[10]] ?: 0f,
            nervios[nerviosNombres[11]] ?: 0f,
            nervios[nerviosNombres[12]] ?: 0f,
            nervios[nerviosNombres[13]] ?: 0f,
            nervios[nerviosNombres[14]] ?: 0f,
            nervios[nerviosNombres[15]] ?: 0f,
            nervios[nerviosNombres[16]] ?: 0f,
            nervios[nerviosNombres[17]] ?: 0f,
            nervios[nerviosNombres[18]] ?: 0f,
            nervios[nerviosNombres[19]] ?: 0f,
            nervios[nerviosNombres[20]] ?: 0f,
            nervios[nerviosNombres[21]] ?: 0f,
            nervios[nerviosNombres[22]] ?: 0f,
            nervios[nerviosNombres[23]] ?: 0f,
            nervios[nerviosNombres[24]] ?: 0f,
            nervios[nerviosNombres[25]] ?: 0f,
            nervios[nerviosNombres[26]] ?: 0f,
            nervios[nerviosNombres[27]] ?: 0f,
            nervios[nerviosNombres[28]] ?: 0f,
            nervios[nerviosNombres[29]] ?: 0f,
            nervios[nerviosNombres[30]] ?: 0f,
            nervios[nerviosNombres[31]] ?: 0f,
            nervios[nerviosNombres[32]] ?: 0f,
            nervios[nerviosNombres[33]] ?: 0f
        ).toDatabase()
        idGeneradoMap = database.getMapDao().insertMap(mapEntity)
    }

    private fun mapCalculate() {
        var resultFront = mrvFront.calcularPixeles("frente")
        var resultBack = mrvBack.calcularPixeles("")
        var results = InterpretationHelper.calcularPorcentaje(resultFront, resultBack)
        porcentajeTotal = results["total"] ?: 0f
        porcentajedchaTotal = results["derecha"] ?: 0f
        porcentajeizdaTotal = results["izquierda"] ?: 0f

        //Nervios (solo parte frontal):
        for (nombre in nerviosNombres) {
            nervios[nombre] = results[nombre] ?: 0f
            Log.d("Nervio", "$nombre: ${results[nombre]}")
        }

        Log.d("n mediano derecho:", "${nervios["nervioMedianoDerecho"]}" )
    }

}