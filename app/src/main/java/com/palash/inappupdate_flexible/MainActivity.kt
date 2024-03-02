package com.palash.inappupdate_flexible

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class MainActivity : AppCompatActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private val MY_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Create the AppUpdateManager
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Check for update availability
        checkForAppUpdate()
    }

    private fun checkForAppUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the flexible update
                startAppUpdate(appUpdateInfo)
            }
        }
    }

    private fun startAppUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            this,
            MY_REQUEST_CODE
        )
    }

    // Override onActivityResult to handle the update flow result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // The user accepted the update
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the update
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    // The update failed or was canceled by the user
                }
            }
        }
    }

    // Register an InstallStateUpdatedListener to track updates
    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // The update has been downloaded, and the app is ready to be updated
            // You can show a prompt to the user to restart the app for the update to take effect
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.registerListener(listener)
    }

    override fun onPause() {
        super.onPause()
        appUpdateManager.unregisterListener(listener)
    }
}