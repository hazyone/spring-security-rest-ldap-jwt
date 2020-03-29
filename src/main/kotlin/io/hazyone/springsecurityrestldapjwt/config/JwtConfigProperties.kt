package io.hazyone.springsecurityrestldapjwt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
data class JwtConfigProperties(val expiresInMinutes: Int)