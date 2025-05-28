package com.caio.speedtestaccessibility.model

class Responsiveness(
    var ping: Ping = Ping(),
    var packetLoss: Double = 0.0,
    var connections: Connections = Connections()
)