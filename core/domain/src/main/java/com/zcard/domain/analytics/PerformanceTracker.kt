package com.zcard.domain.analytics

interface PerformanceTracker {
    fun startTrace(traceName: String): String
    fun stopTrace(traceId: String, isSuccess: Boolean = true, errorMessage: String? = null)
    fun putMetric(traceId: String, metricName: String, value: Long)
    fun putAttribute(traceId: String, key: String, value: String)
}