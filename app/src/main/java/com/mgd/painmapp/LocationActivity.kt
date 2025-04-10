package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.room.Room
import com.mgd.painmapp.Database.Entities.toDatabase
import com.mgd.painmapp.Database.PatientDatabase
import com.mgd.painmapp.databinding.ActivityLocationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var cvSave: CardView
    private lateinit var cvDelete: CardView
    private lateinit var mvFront: MapViews
    private lateinit var mvBack: MapViews
    private var idGeneradoEvaluation: Long = -1
    private var idGeneradoMap: Long = -1
    private lateinit var database: PatientDatabase
    private var porcentajeFrente: Float = 0.0f
    private var porcentajeEspalda: Float = 0.0f
    private var porcentajeTotal: Float = 0.0f
    private var porcentajedchaFrente: Float = 0.0f
    private var porcentajedchaEspalda: Float = 0.0f
    private var porcentajedchaTotal: Float = 0.0f
    private var porcentajeizdaFrente: Float = 0.0f
    private var porcentajeizdaEspalda: Float = 0.0f
    private var porcentajeizdaTotal: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idGeneradoEvaluation = intent.getLongExtra("idGeneradoEvaluation", -1)
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()

        initComponents()
        initListeners()
    }

    private fun initComponents() {
        cvSave = binding.cvSave
        cvDelete = binding.cvDelete
        mvFront = binding.mvFront
        mvBack = binding.mvBack
    }

    private fun initListeners() {
        cvSave.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { //Creamos aqui la coroutine, llamando a una funcion suspend
                fillDatabase()
                val intent = Intent(this@LocationActivity, SensorialSurveyActivity::class.java).apply {
                    putExtra("idGeneradoMap", idGeneradoMap)
                    putExtra("idGeneradoEvaluation", idGeneradoEvaluation)
                }
                startActivity(intent)
            }
        }
        cvDelete.setOnClickListener {
            mvFront.deleteDrawing()
            mvBack.deleteDrawing()
        }
    }

    suspend private fun fillDatabase() {
        mapCalculate()
        val mapEntity = MapInterpretation(
            idGeneradoEvaluation,
            porcentajeTotal,
            porcentajedchaTotal,
            porcentajeizdaTotal
            ).toDatabase()
        idGeneradoMap = database.getMapDao().insertMap(mapEntity)
    }

    private fun mapCalculate(){
        porcentajeFrente = mvFront.calcularPorcentaje(calculo="total")
        porcentajeEspalda = mvBack.calcularPorcentaje(calculo="total")
        porcentajeTotal = (porcentajeFrente + porcentajeEspalda) / 2
        porcentajedchaFrente = mvFront.calcularPorcentaje(calculo="derecha")
        porcentajedchaEspalda = mvBack.calcularPorcentaje(calculo="izquierda")
        porcentajedchaTotal = (porcentajedchaFrente + porcentajedchaEspalda) / 2
        porcentajeizdaFrente = mvFront.calcularPorcentaje(calculo="izquierda")
        porcentajeizdaEspalda = mvBack.calcularPorcentaje(calculo="derecha")
        porcentajeizdaTotal = (porcentajeizdaFrente + porcentajeizdaEspalda) / 2
    }
}