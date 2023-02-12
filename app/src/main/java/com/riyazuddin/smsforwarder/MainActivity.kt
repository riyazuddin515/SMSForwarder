package com.riyazuddin.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.riyazuddin.smsforwarder.Constants.CHECKED
import com.riyazuddin.smsforwarder.Constants.CONTAINS
import com.riyazuddin.smsforwarder.Constants.FORWARD_TO_NUMBER
import com.riyazuddin.smsforwarder.Constants.RECIPIENT
import com.riyazuddin.smsforwarder.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var permissionContract: ActivityResultLauncher<Array<String>>

    private val viewModel: EntryViewModel by viewModels()
    private lateinit var entriesAdapter: EntriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        entriesAdapter = EntriesAdapter()
        setUpRecyclerView()

        permissionContract = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if (it[Manifest.permission.RECEIVE_SMS] == true)
                logger("Received Granted")
            if (it[Manifest.permission.SEND_SMS] == true)
                logger("Send Granted")
        }
        requestPermission()

        val sharedPreferences = getSharedPreferences(RECIPIENT, MODE_PRIVATE)
        sharedPreferences.getBoolean(CHECKED, false).let {
            binding.onOffSwitch.isChecked = it
            binding.layout.isVisible = it
        }

        binding.onOffSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layout.isVisible = true
                sharedPreferences.edit().putBoolean(CHECKED, true).apply()
            }else{
                binding.layout.isVisible = false
                sharedPreferences.edit().putBoolean(CHECKED, false).apply()
            }
        }
        binding.btnSave.setOnClickListener {
            if (binding.onOffSwitch.isChecked) {
                val forwardTo = binding.forwardToTIE.text.toString()
                val contains = binding.containsTIE.text.toString()
                val entry = Entry(forwardTo = forwardTo, contains = contains)
                viewModel.insertEntry(entry)
            }
        }

        viewModel.loadCurrentUserStatus.observe(this, {
            logger(it.size.toString())
            entriesAdapter.differ.submitList(it)
        })
    }

    private fun setUpRecyclerView() {
        binding.entriesRV.apply {
            adapter = entriesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = null
            this.addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )

            entriesAdapter.setOnEntryDelete {
                viewModel.deleteEntry(it)
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