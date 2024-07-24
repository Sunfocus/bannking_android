package com.bannking.app.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bannking.app.databinding.ActivityFirstScreenBinding

class FirstScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstScreenBinding
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1003
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        binding.btnSignUpFirst.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.tvHaveAnAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        //Amit
        if (Build.VERSION.SDK_INT > 32) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {}

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showForcePermissionDialog()
                }

                else -> {
                    // Request the permission
                    requestPermissions(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }


    //Amit
    private fun showForcePermissionDialog() {
        val forceDialog = AlertDialog.Builder(this).setTitle("Permission Required")
            .setMessage("To access notifications, you must grant the permission. Please go to Settings and grant the permission.")
            .setPositiveButton("Settings") { dialogInterface: DialogInterface, i: Int ->
                // Open the app settings when the user clicks "Open Settings"
                openAppSettings()
                dialogInterface.dismiss()
            }.setCancelable(false).create()

        forceDialog.show()
    }
    //Amit
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", this.packageName, null)
        startActivity(intent)
    }

    //Amit
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission denied
                // Handle the denied permission case
                showForcePermissionDialog()
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
            finishAffinity()
        } else if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask()
        }
    }
}