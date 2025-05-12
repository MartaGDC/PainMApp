package com.mgd.painmapp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.room.Room
import com.google.android.material.slider.Slider
import com.mgd.painmapp.R
import com.mgd.painmapp.model.database.entities.toDatabase
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.Symptom
import com.mgd.painmapp.databinding.ActivitySensorialSurveyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SensorialSurveyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySensorialSurveyBinding
    private lateinit var cvSave: CardView
    private lateinit var cvDelete: CardView
    private lateinit var cvSlider: Slider
    private lateinit var rgSymptom1: RadioGroup
    private lateinit var rbPain: RadioButton
    private lateinit var rbOtherSymptom: RadioButton
    private lateinit var rgSymptom2: RadioGroup
    private lateinit var rgSymptom : RadioGroup
    private lateinit var etOtherSymptom: EditText
    private lateinit var cbAgitating : CheckBox
    private lateinit var cbMiserable : CheckBox
    private lateinit var cbAnnoying : CheckBox
    private lateinit var cbPiercing : CheckBox
    private lateinit var cbUnbearable : CheckBox
    private lateinit var cbFatiguing : CheckBox
    private lateinit var cbOtherChar : CheckBox
    private lateinit var etOtherChar : EditText
    private lateinit var rgTime : RadioGroup
    private lateinit var rbContinuous : RadioButton
    private lateinit var tvWhen : TextView
    private lateinit var etWhen : EditText
    private lateinit var database: PatientDatabase
    private var idGeneratedMap: Long = -1
    private var idGeneratedEvaluation: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorialSurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idGeneratedEvaluation = intent.getLongExtra("idGeneratedEvaluation", -1)
        idGeneratedMap = intent.getLongExtra("idGeneratedMap", -1)
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()

        initComponents()
        initListeners()
    }
    private fun initComponents(){
        cvSave = binding.cvSave
        cvDelete = binding.cvDelete
        cvSlider = binding.slScore
        rgSymptom1 = binding.rgSymptom1
        rgSymptom2 = binding.rgSymptom2
        rgSymptom = rgSymptom1 //por defecto esta marcado como sintoma Dolor
        rbPain = binding.rbPain
        rbOtherSymptom = binding.rbOtherSymptom
        etOtherSymptom = binding.etOtherSymptom
        cbAgitating = binding.cbAgitating
        cbMiserable = binding.cbMiserable
        cbFatiguing = binding.cbFatiguing
        cbPiercing = binding.cbPiercing
        cbUnbearable = binding.cbUnbearable
        cbAnnoying = binding.cbAnnoying
        cbOtherChar = binding.cbOtherCharact
        etOtherChar = binding.etOtherCharact
        rgTime = binding.rgTime
        rbContinuous = binding.rbContinuous
        tvWhen = binding.tvWhen
        etWhen = binding.etWhen
    }

    private fun initListeners() {
        //Guardado
        cvSave.setOnClickListener {
            if (validateFields()) {
                fillDatabase()
                val intent = Intent(this, SensorialActivity::class.java).apply {
                    putExtra("idGeneratedEvaluation", idGeneratedEvaluation)
                }
                startActivity(intent)
            }
        }
        //Eliminar datos antes de guardarlos: devolver la pantalla al estado inicial
        cvDelete.setOnClickListener {
            resetValues()
        }
        //Slider
        cvSlider.addOnChangeListener { _, value, _ ->
            cvSlider.value = value
        }
        //Radiogroup divido en dos filas
        var actualizando = false
        rgSymptom1.setOnCheckedChangeListener { _, _ ->
            if (!actualizando && rgSymptom1.checkedRadioButtonId != -1) {
                actualizando = true
                rgSymptom2.clearCheck()
                rgSymptom = rgSymptom1
                actualizando = false
            }
        }
        rgSymptom2.setOnCheckedChangeListener { _, _ ->
            if (!actualizando && rgSymptom2.checkedRadioButtonId != -1) {
                actualizando = true
                rgSymptom1.clearCheck()
                rgSymptom = rgSymptom2
                actualizando = false
            }
            //Borrar texto en Otro sintoma, si se selecciona alguno
            if (rgSymptom1.checkedRadioButtonId != rbOtherSymptom.id || rgSymptom2.checkedRadioButtonId != -1) {
                etOtherSymptom.text.clear()
                etOtherSymptom.error = null
            }
        }

        //Seleccionar rb Otro si se va a escribir sobre el editText
        etOtherSymptom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    rgSymptom2.clearCheck()
                    rbOtherSymptom.isChecked = true
                    rgSymptom1.check(rbOtherSymptom.id)
                    rgSymptom = rgSymptom1
                    etOtherSymptom.error = null
                }
            }
        })

        etOtherChar.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    cbOtherChar.isChecked = true
                    etOtherChar.error = null
                }
            }
        })

        //Si se deselecciona el checkboxOtro, se borra el texto
        cbOtherChar.setOnCheckedChangeListener { _, _ ->
            if (!cbOtherChar.isChecked) {
                etOtherChar.text.clear()
                etOtherChar.error = null
            }
        }
        //Radiobuttons que hacen visible otro componente
        rgTime.setOnCheckedChangeListener { _, _ ->
            if (rgTime.checkedRadioButtonId != rbContinuous.id) {
                tvWhen.visibility = TextView.VISIBLE
                etWhen.visibility = EditText.VISIBLE
            } else {
                tvWhen.visibility = TextView.INVISIBLE
                etWhen.visibility = EditText.INVISIBLE
                etWhen.text.clear()
                etWhen.error = null
            }
        }
    }

    private fun resetValues() {
        cvSlider.value = 0.0f
        rgSymptom2.clearCheck()
        rgSymptom1.check(rbPain.id)
        rgTime.check(rbContinuous.id)
        cbAgitating.isChecked = false
        cbMiserable.isChecked = false
        cbFatiguing.isChecked = false
        cbPiercing.isChecked = false
        cbUnbearable.isChecked = false
        cbAnnoying.isChecked = false
        cbOtherChar.isChecked = false
        etOtherSymptom.text.clear()
        etOtherChar.text.clear()
        tvWhen.visibility = View.INVISIBLE
        etWhen.text.clear()
        etWhen.visibility = View.INVISIBLE
    }

    private fun fillDatabase() {
        val selectedSymptom = rgSymptom.checkedRadioButtonId
        val selectedSymptomRB = findViewById<RadioButton>(selectedSymptom)
        val selectedTime = rgTime.checkedRadioButtonId
        val selectedTimeRB : RadioButton = findViewById(selectedTime)
        val symptomEntity = Symptom(
            idGeneratedMap,
            cvSlider.value,
            selectedSymptomRB.text.toString(),
            etOtherSymptom.text.toString(),
            cbAgitating.isChecked,
            cbMiserable.isChecked,
            cbAnnoying.isChecked,
            cbUnbearable.isChecked,
            cbFatiguing.isChecked,
            cbPiercing.isChecked,
            cbOtherChar.isChecked,
            etOtherChar.text.toString(),
            selectedTimeRB.text.toString(),
            etWhen.text.toString()).toDatabase()
        CoroutineScope(Dispatchers.IO).launch {
            database.getSymptomDao().insertSymptom(symptomEntity)
        }
    }

    private fun validateFields(): Boolean {
        var valid = true
        if (rbOtherSymptom.isChecked && etOtherSymptom.text.isNullOrBlank()) {
            etOtherSymptom.error = getString(R.string.empty_field)
            valid = false
        }
        if (!cbAgitating.isChecked && !cbMiserable.isChecked && !cbAnnoying.isChecked && !cbPiercing.isChecked &&
            !cbUnbearable.isChecked && !cbFatiguing.isChecked && !cbOtherChar.isChecked && cvSlider.value == 0.0f) {
            Toast.makeText(this, getString(R.string.intensity_over0) + "\n" + getString(R.string.select_characteristics), Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (!cbAgitating.isChecked && !cbMiserable.isChecked && !cbAnnoying.isChecked && !cbPiercing.isChecked &&
            !cbUnbearable.isChecked && !cbFatiguing.isChecked && !cbOtherChar.isChecked){
            Toast.makeText(this, getString(R.string.select_characteristics), Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (cvSlider.value == 0.0f) {
            Toast.makeText(this, getString(R.string.intensity_over0), Toast.LENGTH_SHORT).show()
            valid = false
        }
        if (cbOtherChar.isChecked && etOtherChar.text.isNullOrBlank()) {
            etOtherChar.error = getString(R.string.empty_field)
            valid = false
        }
        if (rgTime.checkedRadioButtonId != rbContinuous.id && etWhen.text.isNullOrBlank()) {
            etWhen.error = getString(R.string.empty_field)
            valid = false
        }
        return valid
    }

}