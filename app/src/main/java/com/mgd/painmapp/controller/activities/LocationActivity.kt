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
    private var porcentajeTotal: Float = 0.0f
    private var porcentajedchaTotal: Float = 0.0f
    private var porcentajeizdaTotal: Float = 0.0f
    private var nervioMedianoDerecho: Float = 0.0f
    private var nervioRadialSuperficialDerecho : Float= 0.0f
    private var nervioCubitalDerecho : Float= 0.0f
    private var nervioMusculoCutaneoDerecho : Float= 0.0f
    private var nerviosSupraclavicularesDerechos : Float= 0.0f
    private var nervioFemoralDerecho : Float= 0.0f
    private var nervioGenitalDerecho : Float= 0.0f
    private var nervioIlioinguinoDerecho : Float= 0.0f
    private var nervioObturadoDerecho : Float= 0.0f
    private var nervioFemoralAnteriorDerecho: Float= 0.0f
    private var nervioSafenoDerecho : Float= 0.0f
    private var nervioPeroneoDerecho: Float= 0.0f
    private var nervioSuralDerecho : Float= 0.0f
    private var nervioBraquialDerecho : Float= 0.0f
    private var nervioAntebrazoDerecho : Float= 0.0f
    private var nervioRadialDerecho : Float= 0.0f
    private var nervioAxilarDerecho: Float= 0.0f
    private var nervioMedianoIzquierdo : Float= 0.0f
    private var nervioRadialSuperficialIzquierdo : Float= 0.0f
    private var nervioCubitalIzquierdo : Float= 0.0f
    private var nervioMusculoCutaneoIzquierdo : Float= 0.0f
    private var nerviosSupraclavicularesIzquierdos : Float= 0.0f
    private var nervioFemoralIzquierdo : Float= 0.0f
    private var nervioGenitalIzquierdo : Float= 0.0f
    private var nervioIlioinguinoIzquierdo : Float= 0.0f
    private var nervioObturadoIzquierdo : Float= 0.0f
    private var nervioFemoralAnteriorIzquierdo : Float= 0.0f
    private var nervioSafenoIzquierdo : Float= 0.0f
    private var nervioPeroneoIzquierdo : Float= 0.0f
    private var nervioSuralIzquierdo : Float= 0.0f
    private var nervioBraquialIzquierdo : Float= 0.0f
    private var nervioAntebrazoIzquierdo: Float= 0.0f
    private var nervioRadialIzquierdo : Float= 0.0f
    private var nervioAxilarIzquierdo : Float= 0.0f

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
            porcentajeTotal,
            porcentajedchaTotal,
            porcentajeizdaTotal,
            nervioMedianoDerecho,
            nervioRadialSuperficialDerecho,
            nervioCubitalDerecho,
            nervioMusculoCutaneoDerecho,
            nerviosSupraclavicularesDerechos,
            nervioFemoralDerecho,
            nervioGenitalDerecho,
            nervioIlioinguinoDerecho,
            nervioObturadoDerecho,
            nervioFemoralAnteriorDerecho,
            nervioSafenoDerecho,
            nervioPeroneoDerecho,
            nervioSuralDerecho,
            nervioBraquialDerecho,
            nervioAntebrazoDerecho,
            nervioRadialDerecho,
            nervioAxilarDerecho,
            nervioMedianoIzquierdo,
            nervioRadialSuperficialIzquierdo,
            nervioCubitalIzquierdo,
            nervioMusculoCutaneoIzquierdo,
            nerviosSupraclavicularesIzquierdos,
            nervioFemoralIzquierdo,
            nervioGenitalIzquierdo,
            nervioIlioinguinoIzquierdo,
            nervioObturadoIzquierdo,
            nervioFemoralAnteriorIzquierdo,
            nervioSafenoIzquierdo,
            nervioPeroneoIzquierdo,
            nervioSuralIzquierdo,
            nervioBraquialIzquierdo,
            nervioAntebrazoIzquierdo,
            nervioRadialIzquierdo,
            nervioAxilarIzquierdo
        ).toDatabase()
        idGeneradoMap = database.getMapDao().insertMap(mapEntity)
    }

    private fun mapCalculate() {
        //Lo haria con map y zip. Pero estoy usando derechaFrente para calcular la derecha de frente y la izquierda de espaldas. Por lo que los indices y valores no coinciden
        var resultFront = mrvFront.calcularPixeles("frente")
        var resultBack = mrvBack.calcularPixeles("")
        var results = InterpretationHelper.calcularPorcentaje(resultFront, resultBack)
        porcentajeTotal = results["total"] ?: 0.0f
        porcentajedchaTotal = results["derecha"] ?: 0.0f
        porcentajeizdaTotal = results["izquierda"] ?: 0.0f

        //Nervios (solo parte frontal):
        val nervios = InterpretationHelper.obtenerNerviosPerifericosFrente(this)
        var count=0
        nervioMedianoDerecho = results[nervios[count++]] ?: 0.0f
        nervioRadialSuperficialDerecho = results[nervios[count++]] ?: 0.0f
        nervioCubitalDerecho = results[nervios[count++]] ?: 0.0f
        nervioMusculoCutaneoDerecho = results[nervios[count++]] ?: 0.0f
        nerviosSupraclavicularesDerechos = results[nervios[count++]] ?: 0.0f
        nervioFemoralDerecho = results[nervios[count++]] ?: 0.0f
        nervioGenitalDerecho = results[nervios[count++]] ?: 0.0f
        nervioIlioinguinoDerecho = results[nervios[count++]] ?: 0.0f
        nervioObturadoDerecho = results[nervios[count++]] ?: 0.0f
        nervioFemoralAnteriorDerecho = results[nervios[count++]] ?: 0.0f
        nervioSafenoDerecho = results[nervios[count++]] ?: 0.0f
        nervioPeroneoDerecho = results[nervios[count++]] ?: 0.0f
        nervioSuralDerecho = results[nervios[count++]] ?: 0.0f
        nervioBraquialDerecho = results[nervios[count++]] ?: 0.0f
        nervioAntebrazoDerecho = results[nervios[count++]] ?: 0.0f
        nervioRadialDerecho = results[nervios[count++]] ?: 0.0f
        nervioAxilarDerecho = results[nervios[count++]] ?: 0.0f
        nervioMedianoIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioRadialSuperficialIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioCubitalIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioMusculoCutaneoIzquierdo = results[nervios[count++]] ?: 0.0f
        nerviosSupraclavicularesIzquierdos = results[nervios[count++]] ?: 0.0f
        nervioFemoralIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioGenitalIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioIlioinguinoIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioObturadoIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioFemoralAnteriorIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioSafenoIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioPeroneoIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioSuralIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioBraquialIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioAntebrazoIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioRadialIzquierdo = results[nervios[count++]] ?: 0.0f
        nervioAxilarIzquierdo = results[nervios[count]] ?: 0.0f
    }


}