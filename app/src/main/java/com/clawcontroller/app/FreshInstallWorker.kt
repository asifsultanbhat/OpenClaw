package com.clawcontroller.app

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class FreshInstallWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        try {
            // Step 1: Clean old installation
            setProgress(workDataOf(PROGRESS to "Step 1/5: Cleaning old installation..."))
            executeCommand("rm -rf ~/.openclaw")
            executeCommand("rm -rf ~/.npm/_npx")
            
            // Step 2: Update packages
            setProgress(workDataOf(PROGRESS to "Step 2/5: Updating packages..."))
            executeCommand("pkg update -y")
            
            // Step 3: Install dependencies
            setProgress(workDataOf(PROGRESS to "Step 3/5: Installing dependencies..."))
            val packages = arrayOf(
                "nodejs-lts",
                "python",
                "build-essential",
                "libvips",
                "binutils",
                "pkg-config",
                "xorgproto"
            )
            executeCommand("pkg install -y ${packages.joinToString(" ")}")
            
            // Step 4: Create Gyp dummy config
            setProgress(workDataOf(PROGRESS to "Step 4/5: Creating Gyp config..."))
            executeCommand("mkdir -p ~/.gyp")
            executeCommand("echo \"{ 'variables': { 'android_ndk_path': '' } }\" > ~/.gyp/include.gypi")
            
            // Step 5: Install OpenClaw
            setProgress(workDataOf(PROGRESS to "Step 5/5: Installing OpenClaw..."))
            executeCommand("npm install -g openclaw@latest")
            
            return Result.success()
        } catch (e: Exception) {
            return Result.failure(
                workDataOf(ERROR to e.message)
            )
        }
    }
    
    private fun executeCommand(command: String) {
        // Execute command via Termux
        // This is a simplified version - in production, you'd use proper Termux integration
        val runtime = Runtime.getRuntime()
        runtime.exec(arrayOf("/data/data/com.termux/files/usr/bin/bash", "-c", command))
    }
    
    companion object {
        const val PROGRESS = "progress"
        const val ERROR = "error"
        const val WORK_NAME = "fresh_install_work"
        
        fun startInstallation(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<FreshInstallWorker>()
                .build()
            
            WorkManager.getInstance(context)
                .enqueue(workRequest)
        }
    }
}
