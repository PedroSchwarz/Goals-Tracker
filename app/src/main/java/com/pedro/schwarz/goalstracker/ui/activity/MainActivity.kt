package com.pedro.schwarz.goalstracker.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.pedro.schwarz.goalstracker.R

class MainActivity : AppCompatActivity() {

    private val controller: NavController by lazy {
        findNavController(R.id.nav_host)
    }

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration.Builder(R.id.loginFragment, R.id.goalsFragment).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller.addOnDestinationChangedListener { _, destination, arguments ->
            title = destination.label
            if (destination.id == R.id.goalDetailsFragment) {
                arguments?.let {
                    destination.label = "${it.getString("title")} Details"
                }
            }
        }

        setupActionBarWithNavController(controller, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean =
        controller.navigateUp() || super.onSupportNavigateUp()
}
