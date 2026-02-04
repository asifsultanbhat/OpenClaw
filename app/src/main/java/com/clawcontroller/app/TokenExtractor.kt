package com.clawcontroller.app

import java.util.regex.Pattern

object TokenExtractor {
    
    private val TOKEN_PATTERN = Pattern.compile("Gateway auth token:\\s*([a-zA-Z0-9\\-_]+)")
    
    /**
     * Extracts the gateway auth token from terminal output.
     * Returns null if no token is found.
     */
    fun extractToken(output: String): String? {
        val matcher = TOKEN_PATTERN.matcher(output)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }
    
    /**
     * Checks if the given line contains a token.
     */
    fun containsToken(line: String): Boolean {
        return line.contains("Gateway auth token:", ignoreCase = true)
    }
}
