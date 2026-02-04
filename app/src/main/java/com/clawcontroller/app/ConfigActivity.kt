package com.clawcontroller.app

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class ConfigActivity : AppCompatActivity() {
    
    private lateinit var editGeminiKey: TextInputEditText
    private lateinit var editTelegramToken: TextInputEditText
    private lateinit var editTelegramId: TextInputEditText
    private lateinit var btnSaveConfig: Button
    
    private lateinit var configManager: ConfigManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        
        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        
        // Initialize views
        editGeminiKey = findViewById(R.id.editGeminiKey)
        editTelegramToken = findViewById(R.id.editTelegramToken)
        editTelegramId = findViewById(R.id.editTelegramId)
        btnSaveConfig = findViewById(R.id.btnSaveConfig)
        
        configManager = ConfigManager(this)
        
        // Load existing config
        loadConfiguration()
        
        // Save button listener
        btnSaveConfig.setOnClickListener { saveConfiguration() }
    }
    
    private fun loadConfiguration() {
        val config = configManager.loadConfig()
        editGeminiKey.setText(config.geminiApiKey)
        editTelegramToken.setText(config.telegramBotToken)
        editTelegramId.setText(config.telegramId)
    }
    
    private fun saveConfiguration() {
        val geminiKey = editGeminiKey.text.toString().trim()
        val telegramToken = editTelegramToken.text.toString().trim()
        val telegramId = editTelegramId.text.toString().trim()
        
        if (geminiKey.isEmpty() || telegramToken.isEmpty() || telegramId.isEmpty()) {
            Toast.makeText(this, R.string.config_error, Toast.LENGTH_SHORT).show()
            return
        }
        
        val config = OpenClawConfig(geminiKey, telegramToken, telegramId)
        configManager.saveConfig(config)
        
        Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show()
        finish()
    }
}
