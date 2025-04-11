package com.mgd.painmapp

import android.content.Intent

import android.os.Bundle
import android.util.Log
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
    private lateinit var mrvFront: MapResponsiveViews
    private lateinit var mrvBack: MapResponsiveViews
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
        mrvFront = binding.mrvFront
        mrvBack = binding.mrvBack

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
            mrvFront.deleteDrawing()
            mrvBack.deleteDrawing()
        }
    }

    private suspend fun fillDatabase() {
        mapCalculate()
        val mapEntity = MapInterpretation(
            idGeneradoEvaluation,
            porcentajeTotal,
            porcentajedchaTotal,
            porcentajeizdaTotal
            ).toDatabase()
        idGeneradoMap = database.getMapDao().insertMap(mapEntity)
    }

    private fun mapCalculate() {
        //Lo haria con map y zip. Pero estoy usando derechaFrente para calcular la derecha de frente y la izquierda de espaldas. Por lo que los indices y valores no coinciden
        var resultFront = mrvFront.calcularPorcentaje()
        var resultBack = mrvBack.calcularPorcentaje()
        porcentajeFrente = resultFront["total"] ?: 0.0f
        porcentajeEspalda = resultBack["total"] ?: 0.0f
        porcentajeTotal = (porcentajeFrente + porcentajeEspalda) / 2
        Log.d("porcentajeTotal", porcentajeTotal.toString())
        porcentajedchaFrente = resultFront["derechaFrente"] ?: 0.0f
        porcentajedchaEspalda = resultBack["izquierdaFrente"] ?: 0.0f
        porcentajedchaTotal = (porcentajedchaFrente + porcentajedchaEspalda) / 2
        Log.d("porcentajedchaTotal", porcentajedchaTotal.toString())
        porcentajeizdaFrente = resultFront["izquierdaFrente"] ?: 0.0f
        porcentajeizdaEspalda = resultBack["derechaFrente"] ?: 0.0f
        porcentajeizdaTotal = (porcentajeizdaFrente + porcentajeizdaEspalda) / 2
        Log.d("porcentajeizdaTotal", porcentajeizdaTotal.toString())
    }

}