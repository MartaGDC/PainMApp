package com.mgd.painmapp

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.mgd.painmapp.databinding.ItemSymptomBinding

class SymptomsViewHolder (view: View, private val context: Context):RecyclerView.ViewHolder(view) {
    private val binding = ItemSymptomBinding.bind(view)

    fun render(symptom: Symptom, index: Int) {
        binding.numSymptom.text = (index + 1).toString()
        binding.score.text = symptom.intensity.toString()
        binding.symptom.text = symptom.symptomType
        binding.CVSymptom.setOnClickListener{
            context.startActivity(Intent(context, LocationActivity::class.java))
        }
    }
}