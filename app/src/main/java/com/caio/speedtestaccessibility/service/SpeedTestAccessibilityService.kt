package com.caio.speedtestaccessibility.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.caio.speedtestaccessibility.SharedState.testResult
import com.caio.speedtestaccessibility.SharedState.testState
import com.caio.speedtestaccessibility.util.Util.requestScreenshotPermission
import com.caio.speedtestaccessibility.constants.AppPackageNames
import com.caio.speedtestaccessibility.constants.TestState
import com.caio.speedtestaccessibility.constants.ViewIds
import com.caio.speedtestaccessibility.model.SpeedTestResult
import com.caio.speedtestaccessibility.util.MyApplication

class SpeedTestAccessibilityService : AccessibilityService() {
    private val tag = "SpeedTestAccessibility"
    private var readyButton = ""
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(tag, "onAccessibilityEvent")
        val rootNode = rootInActiveWindow
        if (event.packageName == AppPackageNames.ookla_speed_test) {
            rootNode?.let { node ->
                Log.d(tag, "testState: $testState")
                when (testState) {
                    TestState.IDLE -> {
                        readyButton = ""
                        testState = isAppReady(node).takeIf { it }?.let { TestState.READY } ?: testState
                    }
                    TestState.READY -> {
                        pressButton(node, readyButton)
                        testState = TestState.RUNNING
                        readyButton = ""
                        Thread.sleep(1000)
                    }
                    TestState.RUNNING -> {
                        testState = isTestCompleted(node).takeIf { it }?.let { TestState.TAKE_SCREENSHOT } ?: testState
                    }
                    TestState.TAKE_SCREENSHOT -> {
                        saveScreenShot()
                        testState = TestState.WAIT_SCREENSHOT
                    }
                    TestState.FINISHED -> {
                        pressButton(node, ViewIds.detailedResult)
                        testState = TestState.DETAILED_RESULTS
                    }
                    TestState.DETAILED_RESULTS -> {
                        testState = TestState.PARSING_RESULTS
                        pressButton(node, ViewIds.detailedResult)
                        testResult.value = getTestResult(node)
                    }
                    TestState.PARSING_RESULTS -> {
                        pressButton(node, ViewIds.closeDetailsButton)
                        testState = TestState.NOT_RUNNING
                        pressButton(node, ViewIds.closeTestButton)
                        launchPoCApp()
                    }
                    else -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d(tag, "Service interrupted")
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            packageNames = arrayOf(AppPackageNames.ookla_speed_test)
        }
        serviceInfo = info
        Log.d(tag, "Service connected")
    }

    private fun saveScreenShot() {
        requestScreenshotPermission(MyApplication.instance)
    }

    private fun getValidNode(node: AccessibilityNodeInfo, id: String): AccessibilityNodeInfo? {
        Log.d(tag, "getValidNode")
        val nodeList = node.findAccessibilityNodeInfosByViewId(id)
        if (nodeList.isNotEmpty()) {
            return nodeList.first()
        }
        return null
    }

    private fun getValidNodes(node: AccessibilityNodeInfo, id: String): List<AccessibilityNodeInfo>? {
        Log.d(tag, "getValidNode")
        var waitTime = 0
        var nodeList = node.findAccessibilityNodeInfosByViewId(id)

        while (nodeList.isNullOrEmpty() && waitTime < 3) {
            Thread.sleep(1000)
            waitTime += 1
            nodeList = node.findAccessibilityNodeInfosByViewId(id)
        }
        Log.d(tag, "waitTime: $waitTime")
        if (nodeList == null) {
            Log.d(tag, "nodeList is null. size")
        } else {
            Log.d(tag, "nodeList is not null. size: ${nodeList.size}")
        }

        if (nodeList.isNotEmpty()) {
            return nodeList
        }
        return null
    }

    private fun isViewOnScreen(node: AccessibilityNodeInfo, id: String): Boolean {
        Log.d(tag, "isViewOnScreen")
        val validNode = getValidNode(node, id)
        validNode?.let {
            return it.isVisibleToUser
        }
        return false
    }

    private fun isAppReady(node: AccessibilityNodeInfo): Boolean {
        Log.d(tag, "isAppReady")
        val isGoButtonVisible = isViewOnScreen(node, ViewIds.goButton)
        val isTestAgainVisible = isViewOnScreen(node, ViewIds.testAgain)
        if (isGoButtonVisible) readyButton = ViewIds.goButton
        else if (isTestAgainVisible) readyButton = ViewIds.testAgain
        return isGoButtonVisible || isTestAgainVisible
    }

    private fun isTestCompleted(node: AccessibilityNodeInfo): Boolean {
        Log.d(tag, "isTestCompleted")
        return isViewOnScreen(node, ViewIds.detailedResult)
    }

    private fun pressButton(node: AccessibilityNodeInfo, id: String) {
        Log.d(tag, "pressButton")
        val buttonNode = getValidNode(node, id)
        buttonNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: kotlin.run {
            Log.d(tag, "Button not found")
            throw Exception("Button not found")
        }
    }

    private fun getTestValues(node: AccessibilityNodeInfo, result: SpeedTestResult) {
        Log.d(tag, "getTestValues")
        val list = getValidNodes(node, ViewIds.testResult)
        list?.forEachIndexed { index, itemNode ->
            Log.d(tag, "result: ${itemNode.text}")

            when (index) {
                0 -> result.download = itemNode.text.toString().toDouble()
                1 -> result.upload = itemNode.text.toString().toDouble()
                2 -> result.responsiveness.ping.idle.value = itemNode.text.toString().toDouble()
                3 -> result.responsiveness.ping.download.value = itemNode.text.toString().toDouble()
                4 -> result.responsiveness.ping.upload.value = itemNode.text.toString().toDouble()
                5 -> result.responsiveness.packetLoss = itemNode.text.toString().toDouble()
            }
        }
    }

    private fun getMetricValues(node: AccessibilityNodeInfo, result: SpeedTestResult) {
        Log.d(tag, "getMetricValues")
        val list = getValidNodes(node, ViewIds.metricResult)
        list?.forEachIndexed { index, itemNode ->
            Log.d(tag, "metric: ${itemNode.text}")

            when (index) {
                0 -> result.downloadMetric = itemNode.text.toString()
                1 -> result.uploadMetric = itemNode.text.toString()
            }
        }
    }

    private fun getPingLowValues(node: AccessibilityNodeInfo, result: SpeedTestResult) {
        Log.d(tag, "getPingLowValues")
        val list = getValidNodes(node, ViewIds.pingLowResult)
        list?.forEachIndexed { index, itemNode ->
            Log.d(tag, "low: ${itemNode.text}")

            when (index) {
                0 -> result.responsiveness.ping.idle.low = itemNode.text.toString().toDouble()
                1 -> result.responsiveness.ping.download.low = itemNode.text.toString().toDouble()
                2 -> result.responsiveness.ping.upload.low = itemNode.text.toString().toDouble()
            }
        }
    }

    private fun getPingHighValues(node: AccessibilityNodeInfo, result: SpeedTestResult) {
        Log.d(tag, "getPingHighValues")
        val list = getValidNodes(node, ViewIds.pingHighResult)
        list?.forEachIndexed { index, itemNode ->
            Log.d(tag, "high: ${itemNode.text}")

            when (index) {
                0 -> result.responsiveness.ping.idle.high = itemNode.text.toString().toDouble()
                1 -> result.responsiveness.ping.download.high = itemNode.text.toString().toDouble()
                2 -> result.responsiveness.ping.upload.high = itemNode.text.toString().toDouble()
            }
        }
    }

    private fun getPingJitterValues(node: AccessibilityNodeInfo, result: SpeedTestResult) {
        Log.d(tag, "getPingJitterValues")
        val list = getValidNodes(node, ViewIds.pingJitterResult)
        list?.forEachIndexed { index, itemNode ->
            Log.d(tag, "jitter: ${itemNode.text}")

            when (index) {
                0 -> result.responsiveness.ping.idle.jitter = itemNode.text.toString().toDouble()
                1 -> result.responsiveness.ping.download.jitter = itemNode.text.toString().toDouble()
                2 -> result.responsiveness.ping.upload.jitter = itemNode.text.toString().toDouble()
            }
        }
    }

    private fun getPingMetric(node: AccessibilityNodeInfo, result: SpeedTestResult) {
        val validNode = getValidNode(node, ViewIds.pingMetric)
        validNode?.let {
            Log.d(tag, "ping metric: ${it.text}")
            result.responsiveness.ping.metric = it.text.toString()
        }
    }

    private fun getConnectionsValues(node: AccessibilityNodeInfo, result: SpeedTestResult) {
        Log.d(tag, "getConnectionsValues")
        val list = getValidNodes(node, ViewIds.connectionsResult)
        list?.forEachIndexed { index, itemNode ->
            Log.d(tag, "connection: ${itemNode.text}")

            when (index) {
                0 -> result.responsiveness.connections.type = itemNode.text.toString()
                1 -> result.responsiveness.connections.device = itemNode.text.toString()
                2 -> result.responsiveness.connections.nexusTelecom = itemNode.text.toString()
                3 -> result.responsiveness.connections.connections = itemNode.text.toString()
            }
        }
    }

    private fun getTestResult(node: AccessibilityNodeInfo): SpeedTestResult {
        Log.d(tag, "getTestResult")
        val result = SpeedTestResult()

        getTestValues(node, result)
        getMetricValues(node, result)
        getPingLowValues(node, result)
        getPingHighValues(node, result)
        getPingJitterValues(node, result)
        getPingMetric(node, result)
        getConnectionsValues(node, result)

        return result
    }

    private fun launchPoCApp() {
        Log.d(tag, "launchPoCApp")
        val launchIntent = packageManager.getLaunchIntentForPackage("com.caio.speedtestaccessibility")
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launchIntent)
    }
}