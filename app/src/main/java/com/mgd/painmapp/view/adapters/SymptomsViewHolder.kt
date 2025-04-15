package com.mgd.painmapp.view.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mgd.painmapp.controller.activities.LocationActivity
import com.mgd.painmapp.model.database.Symptom
import com.mgd.painmapp.databinding.ItemSymptomBinding
import java.util.Locale

class SymptomsViewHolder (view: View, private val context: Context):RecyclerView.ViewHolder(view) {
    private val binding = ItemSymptomBinding.bind(view)

    fun render(symptom: Symptom, index: Int) {
        binding.tvNumSymptom.text = String.format(Locale.getDefault(), "%d", index + 1) //para solucionar el warning de manejo de números
        binding.tvScore.text = symptom.intensity.toString()
        binding.tvSymptom.text = symptom.symptom
        binding.cvSymptom.setOnClickListener{  //_______________________________________
            //Cambiar para incluir los intents necesarios
            context.startActivity(Intent(context, LocationActivity::class.java))
        }
    }
}