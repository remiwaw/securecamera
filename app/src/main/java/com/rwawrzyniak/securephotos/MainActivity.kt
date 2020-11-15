package com.rwawrzyniak.securephotos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.rwawrzyniak.securephotos.core.android.OnBackPressedListener
import com.rwawrzyniak.securephotos.core.android.ShouldSkipAppCodeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity) {
	private lateinit var appBarConfiguration: AppBarConfiguration
	private var lastOrientation = 0

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		lastOrientation = savedInstanceState.getInt(KEY_LAST_ORIENTATION)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putInt(KEY_LAST_ORIENTATION, lastOrientation)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (savedInstanceState == null) {
			lastOrientation = resources.configuration.orientation;
		}
		val navController = getNavController()
		appBarConfiguration = AppBarConfiguration(navController.graph)
		setupActionBarWithNavController(navController, appBarConfiguration)

	}

	override fun onBackPressed() {
		val currentFragment = getTopFragment() as? OnBackPressedListener
		currentFragment?.ignoreBackPress()?.takeIf { !it }?.let {
			super.onBackPressed()
		}
	}

	override fun onResume() {
		super.onResume()
		val navController = getNavController()
		navigateToAppCodeScreenIfNotAlreadyDisplayed(navController)
	}

	override fun onSupportNavigateUp(): Boolean {
		return getNavController().navigateUp(appBarConfiguration)
				|| super.onSupportNavigateUp()
	}

	private fun getTopFragment(): Fragment? {
		val navHostFragment = this.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
		return navHostFragment?.childFragmentManager?.fragments?.get(0)
	}

	private fun getNavController(): NavController {
		val navHostFragment =
			supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		val navController = navHostFragment.navController
		return navController
	}

	private fun navigateToAppCodeScreenIfNotAlreadyDisplayed(navController: NavController) {
		// TODO avoid hardcoded label for fragment
		if (shouldDisplayAppCode())
			navController.navigate(R.id.action_global_appCodeFragment)
	}

	private fun shouldDisplayAppCode(): Boolean {
		val currentFragment = getTopFragment() as? ShouldSkipAppCodeListener
		val skipAppCodeForTopFragment = currentFragment?.shouldSkipAppCode() ?: false
		return 	isOrientationChanged().not() && skipAppCodeForTopFragment.not()
	}


	private fun isOrientationChanged(): Boolean {
		val currentOrientation = resources.configuration.orientation
		if (currentOrientation != lastOrientation) {
			lastOrientation = currentOrientation
			return true
		}
		return false
	}

	companion object {
		const val KEY_LAST_ORIENTATION = "last_orientation"
	}
}
