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
    private lateinit var rbDolor: RadioButton
    private lateinit var rbPicor: RadioButton
    private lateinit var rbQuemazon: RadioButton
    private lateinit var rbPunzante: RadioButton
    private lateinit var rbOtroSintoma: RadioButton
    private lateinit var rbCalambres: RadioButton
    private lateinit var rbEscozor: RadioButton
    private lateinit var rbHormigueo: RadioButton
    private lateinit var rgSymptom2: RadioGroup
    private lateinit var rgSymptom : RadioGroup
    private lateinit var etOtroSintoma: EditText
    private lateinit var cbAgitador : CheckBox
    private lateinit var cbMiserable : CheckBox
    private lateinit var cbFastidioso : CheckBox
    private lateinit var cbPenetrante : CheckBox
    private lateinit var cbInsoportable : CheckBox
    private lateinit var cbFatigador : CheckBox
    private lateinit var cbOtraInterpretacion : CheckBox
    private lateinit var etOtraInterpretacion : EditText
    private lateinit var rgTime : RadioGroup
    private lateinit var rbContinuo : RadioButton
    private lateinit var rbMomentaneo : RadioButton
    private lateinit var rbIntermitente : RadioButton
    private lateinit var tvCuando : TextView
    private lateinit var etTime : EditText

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
        cvSave = binding.CVsave
        cvDelete = binding.CVdelete
        cvSlider = binding.slider
        rgSymptom1 = binding.rgSymptom1
        rgSymptom2 = binding.rgSymptom2
        rgSymptom = rgSymptom1 //por defecto esta marcado como sintoma Dolor
        rbDolor = binding.btnDolor
        rbPicor = binding.btnPicor
        rbQuemazon = binding.btnQuemazon
        rbPunzante = binding.btnPunzante
        rbOtroSintoma = binding.btnOtroSintoma
        etOtroSintoma = binding.ETOtroSintoma
        cbAgitador = binding.cbAgitador
        cbMiserable = binding.cbMiserable
        cbFatigador = binding.cbFatigador
        cbPenetrante = binding.cbPenetrante
        cbInsoportable = binding.cbInsoportable
        cbFastidioso = binding.cbFastidioso
        cbOtraInterpretacion = binding.cbOtro
        etOtraInterpretacion = binding.ETOtraInterpretacion
        rgTime = binding.rgTime
        rbContinuo = binding.rbContinuo
        rbMomentaneo = binding.rbMomentaneo
        rbIntermitente = binding.rbIntermitente
        tvCuando = binding.tvCuando
        etTime = binding.ETCuando
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
            rgSymptom1.check(rbDolor.id)
            rgTime.check(rbContinuo.id)
            cbAgitador.isChecked = false
            cbMiserable.isChecked = false
            cbFatigador.isChecked = false
            cbPenetrante.isChecked = false
            cbInsoportable.isChecked = false
            cbFastidioso.isChecked = false
            cbOtraInterpretacion.isChecked = false
            etOtroSintoma.text.clear()
            etOtraInterpretacion.text.clear()
            tvCuando.visibility = TextView.INVISIBLE
            etTime.text.clear()
            etTime.visibility = EditText.INVISIBLE
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
            if (rgSymptom2.checkedRadioButtonId != rbOtroSintoma.id || rgSymptom1.checkedRadioButtonId != -1) {
                etOtroSintoma.text.clear()
            }
        }

        //Seleccionar rb Otro si se va a escribir sobre el editText
        etOtroSintoma.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    rgSymptom1.clearCheck()
                    rbOtroSintoma.isChecked = true
                    rgSymptom2.check(rbOtroSintoma.id)
                    rgSymptom = rgSymptom2
                }
            }
        })

        etOtraInterpretacion.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    cbOtraInterpretacion.isChecked = true
                }
            }
        })

        //Si se deselecciona el checkboxOtro, se borra el texto
        cbOtraInterpretacion.setOnCheckedChangeListener { _, _ ->
            if (!cbOtraInterpretacion.isChecked) {
                etOtraInterpretacion.text.clear()
            }
        }
        //Radiobuttons que hacen visible otro componente
        rgTime.setOnCheckedChangeListener { _, _ ->
            if (rgTime.checkedRadioButtonId != rbContinuo.id) {
                tvCuando.visibility = TextView.VISIBLE
                etTime.visibility = EditText.VISIBLE
            } else {
                tvCuando.visibility = TextView.INVISIBLE
                etTime.visibility = EditText.INVISIBLE
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
            etOtroSintoma.text.toString(),
            cbAgitador.isActivated,
            cbMiserable.isActivated,
            cbFastidioso.isActivated,
            cbInsoportable.isActivated,
            cbFatigador.isActivated,
            cbPenetrante.isActivated,
            cbOtraInterpretacion.isActivated,
            etOtraInterpretacion.text.toString(),
            selectedTimeRB.text.toString(),
            etTime.text.toString()).toDatabase()
        CoroutineScope(Dispatchers.IO).launch {
            database.getSymptomDao().insertSymptom(symptomEntity)
        }
    }
}