package com.clawcontroller.app

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class OpenClawConfig(
    val geminiApiKey: String = "",
    val telegramBotToken: String = "",
    val telegramId: String = ""
)

class ConfigManager(private val context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveConfig(config: OpenClawConfig) {
        encryptedPrefs.edit().apply {
            putString(KEY_GEMINI_API, config.geminiApiKey)
            putString(KEY_TELEGRAM_TOKEN, config.telegramBotToken)
            putString(KEY_TELEGRAM_ID, config.telegramId)
            apply()
        }
    }
    
    fun loadConfig(): OpenClawConfig {
        return OpenClawConfig(
            geminiApiKey = encryptedPrefs.getString(KEY_GEMINI_API, "") ?: "",
            telegramBotToken = encryptedPrefs.getString(KEY_TELEGRAM_TOKEN, "") ?: "",
            telegramId = encryptedPrefs.getString(KEY_TELEGRAM_ID, "") ?: ""
        )
    }
    
    fun generateOnboardScript(): String {
        val config = loadConfig()
        return """
            #!/data/data/com.termux/files/usr/bin/bash
            export GEMINI_API_KEY="${config.geminiApiKey}"
            export TELEGRAM_BOT_TOKEN="${config.telegramBotToken}"
            export TELEGRAM_ID="${config.telegramId}"
            
            # Run openclaw onboard with environment variables
            openclaw onboard --headless \
                --gemini-key="${'$'}GEMINI_API_KEY" \
                --telegram-token="${'$'}TELEGRAM_BOT_TOKEN" \
                --telegram-id="${'$'}TELEGRAM_ID"
        """.trimIndent()
    }
    
    companion object {
        private const val KEY_GEMINI_API = "gemini_api_key"
        private const val KEY_TELEGRAM_TOKEN = "telegram_bot_token"
        private const val KEY_TELEGRAM_ID = "telegram_id"
    }
}
