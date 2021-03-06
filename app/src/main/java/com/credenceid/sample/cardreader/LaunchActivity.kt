package com.credenceid.sample.cardreader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.credenceid.biometrics.Biometrics
import com.credenceid.biometrics.Biometrics.ResultCode.*
import com.credenceid.biometrics.BiometricsManager

class LaunchActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.initBiometrics()
    }

    private fun initBiometrics() {

        /*  Create new biometrics object. */
        App.BioManager = BiometricsManager(this)
        /* Initialize object, meaning tell CredenceService to bind to this application. */
        App.BioManager!!.initializeBiometrics { rc: Biometrics.ResultCode,
                                                _: String,
                                                _: String ->

            when {
                OK == rc -> {
                    Toast.makeText(this, getString(R.string.bio_init), LENGTH_SHORT).show()

                    App.DevFamily = App.BioManager!!.deviceFamily
                    App.DevType = App.BioManager!!.deviceType

                    /* Launch main activity. */
                    val intent = Intent(this, CardReaderActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    this.finish()

                }
                INTERMEDIATE == rc -> {
                    /* This code is never returned here. */
                }
                FAIL == rc ->
                    Toast.makeText(this, getString(R.string.bio_fail_init), LENGTH_LONG).show()
            }
        }
    }
}
