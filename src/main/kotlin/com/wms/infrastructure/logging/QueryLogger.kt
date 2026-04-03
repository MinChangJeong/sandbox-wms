package com.wms.infrastructure.logging

import org.slf4j.MDC
import java.util.*

data class QueryContext(
    val requestId: String = UUID.randomUUID().toString(),
    val correlationId: String? = null,
    val userId: String? = null,
    val action: String = "UNKNOWN",
    val timestamp: Long = System.currentTimeMillis()
) {
    
    fun toMDC() {
        MDC.put("requestId", requestId)
        MDC.put("correlationId", correlationId ?: "")
        MDC.put("action", action)
        MDC.put("timestamp", timestamp.toString())
    }
    
    companion object {
        fun clearMDC() {
            MDC.clear()
        }
        
        fun fromRequest(correlationId: String?, userId: String?, action: String): QueryContext {
            return QueryContext(
                correlationId = correlationId,
                userId = userId,
                action = action
            )
        }
    }
}

class QueryLogger {
    private val logger = org.slf4j.LoggerFactory.getLogger(QueryLogger::class.java)
    
    fun logQuery(context: QueryContext, query: String, params: Map<String, Any?> = emptyMap()) {
        context.toMDC()
        logger.info(
            "Query executed: action={}, query={}, params={}",
            context.action,
            query,
            params.toString()
        )
    }
    
    fun logCommand(context: QueryContext, command: String, result: Any? = null) {
        context.toMDC()
        logger.info(
            "Command executed: action={}, command={}, result={}",
            context.action,
            command,
            result?.toString() ?: "null"
        )
    }
    
    fun logError(context: QueryContext, message: String, error: Throwable? = null) {
        context.toMDC()
        if (error != null) {
            logger.error("Error: action={}, message={}", context.action, message, error)
        } else {
            logger.error("Error: action={}, message={}", context.action, message)
        }
    }
}
