package com.wms.infrastructure.logging

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.slf4j.MDC
import org.slf4j.LoggerFactory

@Component
class QueryLoggingInterceptor : Filter {
    private val logger = LoggerFactory.getLogger(QueryLoggingInterceptor::class.java)
    
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        
        val correlationId = httpRequest.getHeader("X-Correlation-ID") 
            ?: httpRequest.getHeader("X-Request-ID") 
            ?: java.util.UUID.randomUUID().toString()
        
        val userId = httpRequest.getHeader("X-User-ID") ?: "anonymous"
        val method = httpRequest.method
        val path = httpRequest.requestURI
        val queryString = httpRequest.queryString
        
        val context = QueryContext(
            requestId = correlationId,
            correlationId = correlationId,
            userId = userId,
            action = "$method $path"
        )
        
        context.toMDC()
        
        val startTime = System.currentTimeMillis()
        
        try {
            logger.info(
                "Incoming request: method={}, path={}, query={}",
                method,
                path,
                queryString ?: "none"
            )
            
            chain.doFilter(request, response)
            
            val duration = System.currentTimeMillis() - startTime
            logger.info(
                "Request completed: method={}, path={}, status={}, duration={}ms",
                method,
                path,
                httpResponse.status,
                duration
            )
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error(
                "Request failed: method={}, path={}, duration={}ms",
                method,
                path,
                duration,
                e
            )
            throw e
        } finally {
            QueryContext.clearMDC()
        }
    }
}
