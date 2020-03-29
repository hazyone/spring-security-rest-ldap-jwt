package io.hazyone.springsecurityrestldapjwt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SpringSecurityRestLdapJwtApplication

fun main(args: Array<String>) {
	runApplication<SpringSecurityRestLdapJwtApplication>(*args)
}
