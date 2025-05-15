package com.mgd.painmapp.controller.activities

import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.room.Room
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

        val nerveNamesFront = InterpretationHelper.getFrontPeripheralNerves(this)
        val nerveNamesBack = InterpretationHelper.getBackPeripheralNerves(this)
        nerveNames = (nerveNamesFront + nerveNamesBack).toSet().toList() //To set elimina elementos duplicados.
        Log.d("nerveNames", nerveNames.toString())
        val dermatomeNamesFront = InterpretationHelper.getFrontDermatomes(this)
        val dermatomeNamesBack = InterpretationHelper.getBackDermatomes(this)
        dermatomeNames = (dermatomeNamesFront + dermatomeNamesBack).toSet().toList()
        Log.d("dermatomeNames", dermatomeNames.toString())

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
            nerves[nerveNames[0]] ?: 0f,
            nerves[nerveNames[1]] ?: 0f,
            nerves[nerveNames[2]] ?: 0f,
            nerves[nerveNames[3]] ?: 0f,
            nerves[nerveNames[4]] ?: 0f,
            nerves[nerveNames[5]] ?: 0f,
            nerves[nerveNames[6]] ?: 0f,
            nerves[nerveNames[7]] ?: 0f,
            nerves[nerveNames[8]] ?: 0f,
            nerves[nerveNames[9]] ?: 0f,
            nerves[nerveNames[10]] ?: 0f,
            nerves[nerveNames[11]] ?: 0f,
            nerves[nerveNames[12]] ?: 0f,
            nerves[nerveNames[13]] ?: 0f,
            nerves[nerveNames[14]] ?: 0f,
            nerves[nerveNames[15]] ?: 0f,
            nerves[nerveNames[16]] ?: 0f,
            nerves[nerveNames[17]] ?: 0f,
            nerves[nerveNames[18]] ?: 0f,
            nerves[nerveNames[19]] ?: 0f,
            nerves[nerveNames[20]] ?: 0f,
            nerves[nerveNames[21]] ?: 0f,
            nerves[nerveNames[22]] ?: 0f,
            nerves[nerveNames[23]] ?: 0f,
            nerves[nerveNames[24]] ?: 0f,
            nerves[nerveNames[25]] ?: 0f,
            nerves[nerveNames[26]] ?: 0f,
            nerves[nerveNames[27]] ?: 0f,
            nerves[nerveNames[28]] ?: 0f,
            nerves[nerveNames[29]] ?: 0f,
            nerves[nerveNames[30]] ?: 0f,
            nerves[nerveNames[31]] ?: 0f,
            nerves[nerveNames[32]] ?: 0f,
            nerves[nerveNames[33]] ?: 0f,
            nerves[nerveNames[34]] ?: 0f,
            nerves[nerveNames[35]] ?: 0f,
            nerves[nerveNames[36]] ?: 0f,
            nerves[nerveNames[37]] ?: 0f,
            nerves[nerveNames[38]] ?: 0f,
            nerves[nerveNames[39]] ?: 0f,
            nerves[nerveNames[40]] ?: 0f,
            nerves[nerveNames[41]] ?: 0f,
            nerves[nerveNames[42]] ?: 0f,
            nerves[nerveNames[43]] ?: 0f,
            nerves[nerveNames[44]] ?: 0f,
            nerves[nerveNames[45]] ?: 0f,
            nerves[nerveNames[46]] ?: 0f,
            nerves[nerveNames[47]] ?: 0f,
            nerves[nerveNames[48]] ?: 0f,
            nerves[nerveNames[49]] ?: 0f,
            nerves[nerveNames[50]] ?: 0f,
            nerves[nerveNames[51]] ?: 0f,
            nerves[nerveNames[52]] ?: 0f,
            nerves[nerveNames[53]] ?: 0f,
            nerves[nerveNames[54]] ?: 0f,
            nerves[nerveNames[55]] ?: 0f,
            nerves[nerveNames[56]] ?: 0f,
            nerves[nerveNames[57]] ?: 0f,
            nerves[nerveNames[58]] ?: 0f,
            nerves[nerveNames[59]] ?: 0f,
            nerves[nerveNames[60]] ?: 0f,
            nerves[nerveNames[61]] ?: 0f,
            nerves[nerveNames[62]] ?: 0f,
            nerves[nerveNames[63]] ?: 0f,
            nerves[nerveNames[64]] ?: 0f,
            nerves[nerveNames[65]] ?: 0f,
            nerves[nerveNames[66]] ?: 0f,
            nerves[nerveNames[67]] ?: 0f,
            nerves[nerveNames[68]] ?: 0f,
            nerves[nerveNames[69]] ?: 0f,
            nerves[nerveNames[70]] ?: 0f,
            nerves[nerveNames[71]] ?: 0f,
            nerves[nerveNames[72]] ?: 0f,
            nerves[nerveNames[73]] ?: 0f,
            nerves[nerveNames[74]] ?: 0f,
            nerves[nerveNames[75]] ?: 0f,
            nerves[nerveNames[76]] ?: 0f,
            nerves[nerveNames[77]] ?: 0f,
            nerves[nerveNames[78]] ?: 0f,
            nerves[nerveNames[79]] ?: 0f,
            nerves[nerveNames[80]] ?: 0f,
            nerves[nerveNames[81]] ?: 0f,
            nerves[nerveNames[82]] ?: 0f,
            nerves[nerveNames[83]] ?: 0f,
            nerves[nerveNames[84]] ?: 0f,
            nerves[nerveNames[85]] ?: 0f,
            nerves[nerveNames[86]] ?: 0f,
            nerves[nerveNames[87]] ?: 0f,
            nerves[nerveNames[88]] ?: 0f,
            nerves[nerveNames[89]] ?: 0f,
            nerves[nerveNames[90]] ?: 0f,
            nerves[nerveNames[91]] ?: 0f,
            nerves[nerveNames[92]] ?: 0f,
            nerves[nerveNames[93]] ?: 0f,
            nerves[dermatomeNames[0]] ?: 0f,
            nerves[dermatomeNames[1]] ?: 0f,
            nerves[dermatomeNames[2]] ?: 0f,
            nerves[dermatomeNames[3]] ?: 0f,
            nerves[dermatomeNames[4]] ?: 0f,
            nerves[dermatomeNames[5]] ?: 0f,
            nerves[dermatomeNames[6]] ?: 0f,
            nerves[dermatomeNames[7]] ?: 0f,
            nerves[dermatomeNames[8]] ?: 0f,
            nerves[dermatomeNames[9]] ?: 0f,
            nerves[dermatomeNames[10]] ?: 0f,
            nerves[dermatomeNames[11]] ?: 0f,
            nerves[dermatomeNames[12]] ?: 0f,
            nerves[dermatomeNames[13]] ?: 0f,
            nerves[dermatomeNames[14]] ?: 0f,
            nerves[dermatomeNames[15]] ?: 0f,
            nerves[dermatomeNames[16]] ?: 0f,
            nerves[dermatomeNames[17]] ?: 0f,
            nerves[dermatomeNames[18]] ?: 0f,
            nerves[dermatomeNames[19]] ?: 0f,
            nerves[dermatomeNames[20]] ?: 0f,
            nerves[dermatomeNames[21]] ?: 0f,
            nerves[dermatomeNames[22]] ?: 0f,
            nerves[dermatomeNames[23]] ?: 0f,
            nerves[dermatomeNames[24]] ?: 0f,
            nerves[dermatomeNames[25]] ?: 0f,
            nerves[dermatomeNames[26]] ?: 0f,
            nerves[dermatomeNames[27]] ?: 0f,
            nerves[dermatomeNames[28]] ?: 0f,
            nerves[dermatomeNames[29]] ?: 0f,
            nerves[dermatomeNames[30]] ?: 0f,
            nerves[dermatomeNames[31]] ?: 0f,
            nerves[dermatomeNames[32]] ?: 0f,
            nerves[dermatomeNames[33]] ?: 0f,
            nerves[dermatomeNames[34]] ?: 0f,
            nerves[dermatomeNames[35]] ?: 0f,
            nerves[dermatomeNames[36]] ?: 0f,
            nerves[dermatomeNames[37]] ?: 0f,
            nerves[dermatomeNames[38]] ?: 0f,
            nerves[dermatomeNames[39]] ?: 0f,
            nerves[dermatomeNames[40]] ?: 0f,
            nerves[dermatomeNames[41]] ?: 0f,
            nerves[dermatomeNames[42]] ?: 0f,
            nerves[dermatomeNames[43]]?:0f,
            nerves[dermatomeNames[44]]?:0f,
            nerves[dermatomeNames[45]]?:0f,
            nerves[dermatomeNames[46]]?:0f,
            nerves[dermatomeNames[47]]?:0f,
            nerves[dermatomeNames[48]]?:0f,
            nerves[dermatomeNames[49]]?:0f,
            nerves[dermatomeNames[50]]?:0f,
            nerves[dermatomeNames[51]]?:0f,
            nerves[dermatomeNames[52]]?:0f,
            nerves[dermatomeNames[53]]?:0f,
            nerves[dermatomeNames[54]]?:0f,
            nerves[dermatomeNames[55]]?:0f,
            nerves[dermatomeNames[56]]?:0f,
            nerves[dermatomeNames[57]]?:0f
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

        //Nervios (solo parte frontal):
        for (name in (nerveNames+dermatomeNames)) {
            nerves[name] = results[name] ?: 0f
        }
    }
}