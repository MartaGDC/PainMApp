package com.mgd.painmapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mgd.painmapp.databinding.ItemSymptomBinding

class SymptomsViewHolder (view: View):RecyclerView.ViewHolder(view) {
    private val binding = ItemSymptomBinding.bind(view)
    var number :Int = 0

    fun render(symptom: Symptom) {
        binding.numSymptom.text = (number + 1).toString()
        binding.score.text = symptom.intensity.toString()
        binding.symptom.text = symptom.symptomType
    }
}