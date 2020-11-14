package com.rwawrzyniak.securephotos.ui.main.permissions

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ext.getOrAddFragment
import kotlinx.coroutines.CompletableDeferred

// source: https://geoffreymetais.github.io/code/runtime-permissions/#deferred-behavior
class PermissionFragment : Fragment() {
	suspend fun checkPermission(): Boolean = if (hasPermissions()) {
		true
	} else {
		requestPermissions()
		awaitGrant()
	}

	// When showing app permission activity is paused, so onResume we show appCode
	// if there are no permission we know that permissons dialog will be shown, so we should skip appCode screen once.
	fun shouldSkipAppCode() = hasPermissions().not()

	private val deferredGrant = CompletableDeferred<Boolean>()
	private suspend fun awaitGrant(): Boolean = deferredGrant.await()
	private lateinit var permissions: Array<String>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		permissions = requireNotNull(requireArguments().getStringArray(PERMISSIONS_ARG))
	}

	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<String>, grantResults:
		IntArray
	) {
		if (requestCode == REQUEST_CODE_PERMISSIONS) {
			when {
				allPermissionsGranted() -> {
					deferredGrant.complete(true)
					return
				}
				shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
						|| shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)-> {
					showRationaleDialog(
						getString(R.string.rationale_title),
						getString(R.string.rationale_description) + permissions.reduce {
								acc, s -> "${translatePermission(acc)} , ${translatePermission(s)}"
						}
					)
				}
				else -> {
					deferredGrant.complete(false)
				}
			}
		}
	}

	private fun translatePermission(permission : String): CharSequence {
		val packageManager = requireContext().packageManager
		val permissionInfo = packageManager.getPermissionInfo(permission, 0)
		val permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0)
		return permissionGroupInfo.loadLabel(packageManager).toString()
	}

	private fun hasPermissions() = permissions.all {
		ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
	}

	private fun showRationaleDialog(
		title: String,
		message: String
	) {
		val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
		builder.setTitle(title)
			.setMessage(message)
			.setPositiveButton("Ok") { _, _ -> requestPermissions() }
		builder.create().show()
	}

    private fun requestPermissions() {
		// TODO deal with deprecation
		@Suppress("DEPRECATION")
		(requestPermissions(permissions, REQUEST_CODE_PERMISSIONS))
	}

	private fun allPermissionsGranted() = permissions.all {
		ContextCompat.checkSelfPermission(
            this.requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
	}

	companion object {
		private const val REQUEST_CODE_PERMISSIONS = 10
		private const val PERMISSIONS_ARG = "PERMISSIONS_BUDLE_ARG"

		fun Fragment.createAndCommitPermissionFragment(tag: String, permissions: Array<String>) : PermissionFragment =
			getOrAddFragment(tag = tag) { PermissionFragment()
				.apply {
					arguments = Bundle().apply {
						putStringArray(PERMISSIONS_ARG, permissions) }}
					}
			}
}
