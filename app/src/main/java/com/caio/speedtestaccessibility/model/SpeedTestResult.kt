package com.caio.speedtestaccessibility.model

class SpeedTestResult(
    var download: Double = 0.0,
    var downloadMetric: String = "",
    var upload: Double = 0.0,
    var uploadMetric: String = "",
    var responsiveness: Responsiveness = Responsiveness(),
)