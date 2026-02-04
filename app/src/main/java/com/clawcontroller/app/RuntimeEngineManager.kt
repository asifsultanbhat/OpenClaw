package com.clawcontroller.app

import android.content.Context
import java.io.IOException
import java.net.Socket

object RuntimeEngineManager {
    
    private const val GATEWAY_PORT = 18789
    private const val LOCALHOST = "127.0.0.1"
    
    /**
     * Check if Termux is installed on the device
     */
    fun isTermuxInstalled(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            pm.getPackageInfo("com.termux", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if Termux:API is installed
     */
    fun isTermuxApiInstalled(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            pm.getPackageInfo("com.termux.api", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if Termux:Tasker is installed
     */
    fun isTermuxTaskerInstalled(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            pm.getPackageInfo("com.termux.tasker", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if the gateway is running by attempting to connect to port 18789
     */
    fun isGatewayRunning(): Boolean {
        return try {
            Socket(LOCALHOST, GATEWAY_PORT).use {
                true
            }
        } catch (e: IOException) {
            false
        }
    }
    
    /**
     * Perform pre-flight checks before starting the gateway
     */
    fun performPreFlightChecks(context: Context): PreFlightResult {
        val errors = mutableListOf<String>()
        
        if (!isTermuxInstalled(context)) {
            errors.add("Termux not installed")
        }
        
        if (!isTermuxApiInstalled(context)) {
            errors.add("Termux:API not installed")
        }
        
        if (!isTermuxTaskerInstalled(context)) {
            errors.add("Termux:Tasker not installed")
        }
        
        return PreFlightResult(
            success = errors.isEmpty(),
            errors = errors
        )
    }
    
    data class PreFlightResult(
        val success: Boolean,
        val errors: List<String>
    )
}
