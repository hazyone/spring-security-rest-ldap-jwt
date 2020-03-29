package io.hazyone.springsecurityrestldapjwt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "ldap")
data class LdapConfigProperties(
    val url: String,
    val managerDn: String,
    val managerPassword: String,
    val userDnPatterns: String,
    val memberOfSearch: Boolean
)