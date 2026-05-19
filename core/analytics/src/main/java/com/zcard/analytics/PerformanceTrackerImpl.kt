package com.zcard.analytics

import com.google.firebase.Firebase
import com.google.firebase.perf.metrics.Trace
import com.google.firebase.perf.performance
import com.zcard.domain.analytics.PerformanceTracker
import javax.inject.Inject

class PerformanceTrackerImpl @Inject constructor(): PerformanceTracker {

    private val activeTraces = mutableMapOf<String, Trace>()

    override fun startTrace(traceName: String): String {
        val traceId = "${traceName}_${System.currentTimeMillis()}"
        val trace = Firebase.performance.newTrace(traceName)
        trace.start()
        activeTraces[traceId] = trace
        return traceId
    }

    override fun stopTrace(traceId: String, isSuccess: Boolean, errorMessage: String?) {
        activeTraces[traceId]?.let { trace ->
            trace.putAttribute("status", if (isSuccess) "success" else "failure")
            errorMessage?.let { trace.putAttribute("error", it) }

            trace.stop()
            activeTraces.remove(traceId)
        }
    }

    override fun putMetric(traceId: String, metricName: String, value: Long) {
        activeTraces[traceId]?.putMetric(metricName, value)
    }

    override fun putAttribute(traceId: String, key: String, value: String) {
        activeTraces[traceId]?.putAttribute(key, value.take(100))
    }
}