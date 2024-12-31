package com.mathias8dev.webfluxblognotificationsender.beans.gateway

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope


@Component
@RequestScope
data class GatewayContext(
    var gatewayUrl: String? = null
)