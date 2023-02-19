package com.example.demo

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
class SecurityWebFilter : CoWebFilter() {
    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        if (!exchange.request.queryParams.containsKey("user")) {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        }
        return chain.filter(exchange)
    }
}