package com.caio.speedtestaccessibility.model

class Ping(
    var metric: String = "",
    var idle: PingData = PingData(),
    var download: PingData = PingData(),
    var upload: PingData = PingData()
)