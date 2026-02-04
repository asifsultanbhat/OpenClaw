package com.clawcontroller.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    
    private lateinit var statusIcon: ImageView
    private lateinit var statusText: TextView
    private lateinit var tokenText: TextView
    private lateinit var btnCopyToken: Button
    private lateinit var btnFreshInstall: Button
    private lateinit var btnStartRuntime: Button
    private lateinit var btnStop: Button
    private lateinit var webViewContainer: View
    private lateinit var dashboardWebView: WebView
    private lateinit var logConsole: TextView
    
    private var isRunning = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        statusIcon = findViewById(R.id.statusIcon)
        statusText = findViewById(R.id.statusText)
        tokenText = findViewById(R.id.tokenText)
        btnCopyToken = findViewById(R.id.btnCopyToken)
        btnFreshInstall = findViewById(R.id.btnFreshInstall)
        btnStartRuntime = findViewById(R.id.btnStartRuntime)
        btnStop = findViewById(R.id.btnStop)
        webViewContainer = findViewById(R.id.webViewContainer)
        dashboardWebView = findViewById(R.id.dashboardWebView)
        logConsole = findViewById(R.id.logConsole)
        
        // Setup WebView
        dashboardWebView.settings.javaScriptEnabled = true
        dashboardWebView.settings.domStorageEnabled = true
        
        // Button listeners
        btnCopyToken.setOnClickListener { copyTokenToClipboard() }
        btnFreshInstall.setOnClickListener { startFreshInstall() }
        btnStartRuntime.setOnClickListener { startRuntime() }
        btnStop.setOnClickListener { stopRuntime() }
        
        // Register broadcast receiver for updates from service
        registerServiceReceiver()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, ConfigActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun copyTokenToClipboard() {
        val token = tokenText.text.toString()
        if (token != getString(R.string.token_placeholder)) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Gateway Token", token)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Token copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startFreshInstall() {
        // Start Fresh Install Worker
        FreshInstallWorker.startInstallation(this)
        appendLog("[*] Starting Fresh Install...")
        btnFreshInstall.isEnabled = false
    }
    
    private fun startRuntime() {
        // Start Termux Command Service
        val intent = Intent(this, TermuxCommandService::class.java)
        intent.action = TermuxCommandService.ACTION_START_GATEWAY
        startService(intent)
        
        appendLog("[*] Starting OpenClaw Gateway...")
        btnStartRuntime.isEnabled = false
        btnStop.isEnabled = true
    }
    
    private fun stopRuntime() {
        val intent = Intent(this, TermuxCommandService::class.java)
        intent.action = TermuxCommandService.ACTION_STOP_GATEWAY
        startService(intent)
        
        appendLog("[*] Stopping Gateway...")
        btnStartRuntime.isEnabled = true
        btnStop.isEnabled = false
        updateStatus(false)
    }
    
    private fun updateStatus(online: Boolean) {
        isRunning = online
        if (online) {
            statusIcon.setImageResource(R.drawable.ic_status_online)
            statusText.text = getString(R.string.status_online)
            statusText.setTextColor(getColor(R.color.status_online))
        } else {
            statusIcon.setImageResource(R.drawable.ic_status_offline)
            statusText.text = getString(R.string.status_offline)
            statusText.setTextColor(getColor(R.color.status_offline))
        }
    }
    
    private fun updateToken(token: String) {
        tokenText.text = token
    }
    
    private fun showWebView() {
        webViewContainer.visibility = View.VISIBLE
        dashboardWebView.loadUrl("http://127.0.0.1:18789")
    }
    
    private fun appendLog(message: String) {
        val currentLog = logConsole.text.toString()
        val newLog = if (currentLog == getString(R.string.log_empty)) {
            message
        } else {
            "$currentLog\n$message"
        }
        logConsole.text = newLog
        
        // Auto-scroll to bottom
        logConsole.post {
            val scrollView = logConsole.parent as? android.widget.ScrollView
            scrollView?.fullScroll(View.FOCUS_DOWN)
        }
    }
    
    private fun registerServiceReceiver() {
        // This would typically use LocalBroadcastManager or LiveData
        // For simplicity, we'll handle direct updates from the service
    }
    
    companion object {
        const val TAG = "MainActivity"
    }
}
