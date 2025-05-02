package com.mgd.painmapp.controller

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.activities.MainActivity
import com.mgd.painmapp.controller.activities.SensorialActivity
import com.mgd.painmapp.controller.activities.SummaryActivity

object NavigationHelper {
    fun navigateToSensorial(context: Context, patientName: String, researcherName: String, currentDate: String, idGeneradoEvaluation: Long) {
        val intent = Intent(context, SensorialActivity::class.java).apply {
            putExtra("patient_name", patientName)
            putExtra("researcher_name", researcherName)
            putExtra("date", currentDate)
            putExtra("type", "sensorial")
            putExtra("idGeneradoEvaluation", idGeneradoEvaluation)
        }
        context.startActivity(intent)
    }
    fun navigateToMotor() {
        TODO()
    }
    fun navigateToPsychosocial() {
        TODO()
    }
    fun navigateToSummary(context: Context, idGeneradoEvaluation: Long) {
        val intent = Intent(context, SummaryActivity::class.java).apply {
            putExtra("idGeneradoEvaluation", idGeneradoEvaluation)
        }
        context.startActivity(intent)
    }
    fun navigateToNewPatient(context:Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }


    //Menu
    fun setupMenu(navView: NavigationView, drawerLayout: DrawerLayout, context: Context, patientName: String, researcherName: String, currentDate: String, idGeneradoEvaluation: Long) {
        navView.setNavigationItemSelectedListener { menuItem ->
            handleMenuItemClick(menuItem, drawerLayout, context, patientName, researcherName, currentDate, idGeneradoEvaluation)
            true
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem, drawerLayout: DrawerLayout, context: Context, patientName: String, researcherName: String, currentDate: String, idGeneradoEvaluation: Long) {
        when (menuItem.itemId) {
            R.id.item_sensorial -> {
                navigateToSensorial(context, patientName, researcherName, currentDate, idGeneradoEvaluation)
            }
            R.id.item_motor -> {
                navigateToMotor()
            }
            R.id.item_psicosocial -> {
                navigateToPsychosocial()
            }
            R.id.item_resumen -> {
                if (idGeneradoEvaluation ==-1.toLong()) {
                    Toast.makeText(context, "No hay ningún síntoma que mostrar", Toast.LENGTH_SHORT).show()
                }
                else{
                    navigateToSummary(context, idGeneradoEvaluation)
                }
            }
            else -> {
                navigateToNewPatient(context)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}