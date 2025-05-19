package com.mgd.painmapp.controller.activities

import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.room.Room
import com.google.gson.Gson
import com.mgd.painmapp.R
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
    private var idGeneratedEvaluation: Long = -1
    private var idGeneratedMap: Long = -1
    private lateinit var database: PatientDatabase
    private var totalPercentage: Float = 0f
    private var totalRightPercentage: Float = 0f
    private var totalLeftPercentage: Float = 0f
    private lateinit var nerveNames: List<String>
    private lateinit var dermatomeNames: List<String>
    private val nerves: MutableMap<String, Float> = mutableMapOf()
    private val dermatomes: MutableMap<String, Float> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idGeneratedEvaluation = intent.getLongExtra("idGeneratedEvaluation", -1)
        CoroutineScope(Dispatchers.IO).launch {
            saveColorIndex((getColorIndex()+1) % ColorBrush.colorList.size)
        }
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()

        nerveNames = InterpretationHelper.getPeripheralNerves(this)
        dermatomeNames = InterpretationHelper.getDermatomes(this)

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
            if (!mrvFront.validateMap() && !mrvBack.validateMap()) {
                Toast.makeText(this, getString(R.string.must_draw_symptom),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                Toast.makeText(this, getString(R.string.calculating), Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch { //Creamos aqui la coroutine, llamando a una funcion suspend
                    fillDatabase()
                    val intent = Intent(this@LocationActivity, SensorialSurveyActivity::class.java).apply {
                        putExtra("idGeneratedMap", idGeneratedMap)
                        putExtra("idGeneratedEvaluation", idGeneratedEvaluation)
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
            idGeneratedEvaluation,
            mrvFront.pathToSVGString(),
            mrvBack.pathToSVGString(),
            null,
            null,
            null,
            totalPercentage,
            totalRightPercentage,
            totalLeftPercentage,
            Gson().toJson(nerves),
            Gson().toJson(dermatomes)
        ).toDatabase()
        idGeneratedMap = database.getMapDao().insertMap(mapEntity)
    }

    private fun mapCalculate() {
        var resultFront = mrvFront.calcularPixeles("frente")
        var resultBack = mrvBack.calcularPixeles("")
        var results = InterpretationHelper.calculatePercentage(resultFront, resultBack)
        totalPercentage = results["total"] ?: 0f
        totalRightPercentage = results["derecha"] ?: 0f
        totalLeftPercentage = results["izquierda"] ?: 0f
        for (name in nerveNames) {
            if(results[name]?.isNaN() == true){
                nerves[name] = 0f
            }
            else{
                nerves[name] = results[name] ?: 0f
            }
        }
        for (name in dermatomeNames) {
            if(results[name]?.isNaN() == true){
                dermatomes[name] = 0f
            }
            else{
                dermatomes[name] = results[name] ?: 0f
            }
        }
    }
}