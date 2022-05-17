package com.riyazuddin.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.riyazuddin.smsforwarder.Constants.CHECKED
import com.riyazuddin.smsforwarder.Constants.CONTAINS
import com.riyazuddin.smsforwarder.Constants.FORWARD_TO_NUMBER
import com.riyazuddin.smsforwarder.Constants.RECIPIENT
import com.riyazuddin.smsforwarder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var permissionContract: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionContract = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if (it[Manifest.permission.RECEIVE_SMS] == true)
                logger("Received Granted")
            if (it[Manifest.permission.SEND_SMS] == true)
                logger("Send Granted")
        }
        requestPermission()

        val sharedPreferences = getSharedPreferences(RECIPIENT, MODE_PRIVATE)
        sharedPreferences.getString(FORWARD_TO_NUMBER, null)?.let {
            binding.forwardToTIE.text = Editable.Factory().newEditable(it)
        }
        sharedPreferences.getString(CONTAINS, null)?.let {
            binding.containsTIE.text = Editable.Factory().newEditable(it)
        }
        sharedPreferences.getBoolean(CHECKED, false).let {
            binding.onOffSwitch.isChecked = it
            binding.layout.isVisible = it
        }

        binding.onOffSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layout.isVisible = true
            }else{
                binding.layout.isVisible = false
                sharedPreferences.edit().putBoolean(CHECKED, false).apply()
            }
        }
        binding.btnSave.setOnClickListener {
            if (binding.onOffSwitch.isChecked) {
                val text = binding.forwardToTIE.text
                if (text != null && text.length == 10) {
                    sharedPreferences.edit().apply {
                        putString(FORWARD_TO_NUMBER, text.toString())
                        putString(CONTAINS, binding.containsTIE.text.toString())
                        putBoolean(CHECKED, true)
                        apply()
                    }
                    Toast.makeText(this, getString(R.string.save), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.failed_to_save), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logger(message: String) {
        Log.i("TAG", "logger: $message")
    }

    private fun requestPermission() {
        val permissionList = arrayListOf<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ){
            permissionList.add(Manifest.permission.RECEIVE_SMS)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ){
            permissionList.add(Manifest.permission.SEND_SMS)
        }

        if (permissionList.isNotEmpty()) {
            permissionContract.launch(permissionList.toTypedArray())
        }
    }

}