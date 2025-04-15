package com.mgd.painmapp.controller

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.activities.SensorialActivity

object NavigationHelper {
    fun navigateToSensorial(context: Context, patientName: String, researcherName: String, currentDate: String) {
        val intent = Intent(context, SensorialActivity::class.java).apply {
            putExtra("PATIENT_NAME", patientName)
            putExtra("RESEARCHER_NAME", researcherName)
            putExtra("DATE", currentDate)
            putExtra("TYPE", "sensorial")
        }
        context.startActivity(intent)
    }
    fun navigateToMotor() {
        TODO()
    }
    fun navigateToPsychosocial() {
        TODO()
    }

    //Menu
    fun setupMenu(navView: NavigationView, drawerLayout: DrawerLayout, context: Context, patientName: String, researcherName: String, currentDate: String) {
        navView.setNavigationItemSelectedListener { menuItem ->
            handleMenuItemClick(menuItem, drawerLayout, context, patientName, researcherName, currentDate)
            true
        }
    }

    private fun handleMenuItemClick(item: MenuItem, drawerLayout: DrawerLayout, context: Context, patientName: String, researcherName: String, currentDate: String) {
        when (item.itemId) {
            R.id.item_sensorial -> {
                navigateToSensorial(context, patientName, researcherName, currentDate)
            }
            R.id.item_motor -> {
                navigateToMotor()
            }
            else -> {
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}