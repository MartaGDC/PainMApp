package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.room.Room
import com.google.android.material.slider.Slider
import com.mgd.painmapp.Database.Entities.toDatabase
import com.mgd.painmapp.Database.PatientDatabase
import com.mgd.painmapp.databinding.ActivitySensorialSurveyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SensorialSurveyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySensorialSurveyBinding
    private lateinit var database: PatientDatabase
    private var idGeneradoLocation: Long = 0
    private lateinit var cvSave: CardView
    private lateinit var cvDelete: CardView
    private lateinit var cvSlider: Slider
    private lateinit var rgSymptom1: RadioGroup
    private lateinit var rbPain: RadioButton
    private lateinit var rbItch: RadioButton
    private lateinit var rbBurn: RadioButton
    private lateinit var rbSharp: RadioButton
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
    private lateinit var cbOtherCharact : CheckBox
    private lateinit var etOtherCharact : EditText
    private lateinit var rgTime : RadioGroup
    private lateinit var rbContinuous : RadioButton
    private lateinit var rbMomentary : RadioButton
    private lateinit var rbIntermittent : RadioButton
    private lateinit var tvWhen : TextView
    private lateinit var etWhen : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorialSurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idGeneradoLocation = intent.getLongExtra("ID", 0)

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
        rbItch = binding.rbItch
        rbBurn = binding.rbBurn
        rbSharp = binding.rbSharp
        rbOtherSymptom = binding.rbOtherSymptom
        etOtherSymptom = binding.etOtherSymptom
        cbAgitating = binding.cbAgitating
        cbMiserable = binding.cbMiserable
        cbFatiguing = binding.cbFatiguing
        cbPiercing = binding.cbPiercing
        cbUnbearable = binding.cbUnbearable
        cbAnnoying = binding.cbAnnoying
        cbOtherCharact = binding.cbOtherCharact
        etOtherCharact = binding.etOtherCharact
        rgTime = binding.rgTime
        rbContinuous = binding.rbContinuous
        rbMomentary = binding.rbMomentary
        rbIntermittent = binding.rbIntermittent
        tvWhen = binding.tvWhen
        etWhen = binding.etWhen
    }

    private fun initListeners() {
        //Guardado
        cvSave.setOnClickListener {
            fillDatabase()
            val intent = Intent(this, SensorialActivity::class.java).apply {
                putExtra(
                    "ID",
                    idGeneradoLocation
                ) //Habra que inlcuir más informaicón aqui, derivada de consultas a la base de datos
            }
            startActivity(intent)
        }
        //Eliminar datos antes de guardarlos: devolver la pantalla al estado inicial
        cvDelete.setOnClickListener {
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
            cbOtherCharact.isChecked = false
            etOtherSymptom.text.clear()
            etOtherCharact.text.clear()
            tvWhen.visibility = TextView.INVISIBLE
            etWhen.text.clear()
            etWhen.visibility = EditText.INVISIBLE
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
                }
            }
        })

        etOtherCharact.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    cbOtherCharact.isChecked = true
                }
            }
        })

        //Si se deselecciona el checkboxOtro, se borra el texto
        cbOtherCharact.setOnCheckedChangeListener { _, _ ->
            if (!cbOtherCharact.isChecked) {
                etOtherCharact.text.clear()
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
            }
        }
    }
    private fun fillDatabase() {
        val selectedSymptom = rgSymptom.checkedRadioButtonId
        val selectedSymptomRB : RadioButton = findViewById(selectedSymptom)
        val selectedTime = rgTime.checkedRadioButtonId
        val selectedTimeRB : RadioButton = findViewById(selectedTime)
        val symptomEntity = Symptom(
            idGeneradoLocation,
            cvSlider.value,
            selectedSymptomRB.text.toString(),
            etOtherSymptom.text.toString(),
            cbAgitating.isActivated,
            cbMiserable.isActivated,
            cbAnnoying.isActivated,
            cbUnbearable.isActivated,
            cbFatiguing.isActivated,
            cbPiercing.isActivated,
            cbOtherCharact.isActivated,
            etOtherCharact.text.toString(),
            selectedTimeRB.text.toString(),
            etWhen.text.toString()).toDatabase()
        CoroutineScope(Dispatchers.IO).launch {
            database.getSymptomDao().insertSymptom(symptomEntity)
        }
    }
}