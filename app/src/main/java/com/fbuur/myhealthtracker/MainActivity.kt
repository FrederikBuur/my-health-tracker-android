package com.fbuur.myhealthtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.TranslateAnimation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fbuur.myhealthtracker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup bottom navigation and app bar with nav controller
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.eventsFragment,
                R.id.statisticsFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)

//        // soft keyboard visibility listener
//        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
//            val viewHeight = binding.bottomNavigationView.height.toFloat()
//            if (isOpen) {
//                // animate bottom nav  out
//                val animateOut = TranslateAnimation(0f, 0f, 0f, viewHeight)
//                animateOut.duration = 200L
//                animateOut.fillAfter = true
//                animateOut.interpolator = AccelerateDecelerateInterpolator()
//                binding.bottomNavigationView.startAnimation(animateOut)
////                binding.bottomNavigationView.visibility = View.GONE
//            } else {
//                // animate bottom nav in
//                val animateIn = TranslateAnimation(0f, 0f, viewHeight, 0f)
//                animateIn.duration = 200L
//                animateIn.fillAfter = true
//                animateIn.interpolator = AccelerateDecelerateInterpolator()
//                binding.bottomNavigationView.startAnimation(animateIn)
////                binding.bottomNavigationView.visibility = View.VISIBLE
//            }
//
//        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}