package com.rwawrzyniak.securephotos.ui.main.permissions

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rwawrzyniak.securephotos.R
import com.rwawrzyniak.securephotos.ext.getOrAddFragment
import kotlinx.coroutines.CompletableDeferred

// source: https://geoffreymetais.github.io/code/runtime-permissions/#deferred-behavior
class CameraPermissionFragment : Fragment() {
	suspend fun checkPermission(): Boolean = if (!hasPermissions(this.requireContext())) {
		true
	} else {
		awaitGrant()
	}

	private val deferredGrant = CompletableDeferred<Boolean>()
	private suspend fun awaitGrant(): Boolean = deferredGrant.await()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestCameraPermission()
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
				shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
					showRationaleDialog(
						getString(R.string.rationale_title),
						getString(R.string.rationale_description)
					)
				}
				else -> {
					deferredGrant.complete(false)
				}
			}
		}
	}

	private fun showRationaleDialog(
		title: String,
		message: String
	) {
		val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
		builder.setTitle(title)
			.setMessage(message)
			.setPositiveButton("Ok") { _, _ -> requestCameraPermission() }
		builder.create().show()
	}

    private fun requestCameraPermission() {
		// TODO deal with deprecation
		@Suppress("DEPRECATION")
		requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
	}

	private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
		ContextCompat.checkSelfPermission(
            this.requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
	}

	companion object {
		private const val REQUEST_CODE_PERMISSIONS = 10
		private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
		private fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
			ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
		}

		fun Fragment.createAndCommitPermissionFragment(tag: String) : CameraPermissionFragment =
			getOrAddFragment(tag = tag) { CameraPermissionFragment() }
	}
}
