package com.pedro.schwarz.goalstracker.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TITLE_ARGUMENT_KEY = "title"

class MainActivity : AppCompatActivity() {

    private val controller: NavController by lazy {
        findNavController(R.id.nav_host)
    }

    private val appViewModel by viewModel<AppViewModel>()

    private val mainBottomNav: BottomNavigationView by lazy {
        findViewById<BottomNavigationView>(R.id.main_bottom_nav)
    }

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration.Builder(
            R.id.loginFragment,
            R.id.goalsFragment,
            R.id.completedGoalsFragment
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUIComponents()
        configTitleDestinationListener()
        setupActionBarWithNavController(controller, appBarConfiguration)
        mainBottomNav.setupWithNavController(controller)
    }

    private fun configTitleDestinationListener() {
        controller.addOnDestinationChangedListener { _, destination, arguments ->
            title = destination.label
            if (destination.id == R.id.goalDetailsFragment) {
                arguments?.let {
                    destination.label =
                        "${it.getString(TITLE_ARGUMENT_KEY)} ${getString(R.string.nav_label_goal_details)}"
                }
            }
        }
    }

    private fun setUIComponents() {
        appViewModel.components.observe(this, Observer { components ->
            components?.apply {
                if (appBar) supportActionBar?.show()
                else supportActionBar?.hide()
                if (bottomNav) mainBottomNav.visibility = View.VISIBLE
                else mainBottomNav.visibility = View.GONE
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean =
        controller.navigateUp() || super.onSupportNavigateUp()
}
