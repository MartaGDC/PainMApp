package com.mgd.painmapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SymptomsAdapter (var symptoms: List<Symptom>):RecyclerView.Adapter<SymptomsViewHolder>(){

    fun updateList(list: List<Symptom>){
        symptoms = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_symptom, parent, false)
        return SymptomsViewHolder(view)
    }

    override fun getItemCount() = symptoms.size

    override fun onBindViewHolder(holder: SymptomsViewHolder, position: Int) {
        holder.render(symptoms[position])
    }

}