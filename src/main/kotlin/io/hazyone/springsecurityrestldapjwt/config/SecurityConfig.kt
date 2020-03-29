package io.hazyone.springsecurityrestldapjwt.config

import io.hazyone.springsecurityrestldapjwt.security.config.CustomLdapAuthoritiesPopulator
import io.hazyone.springsecurityrestldapjwt.security.filter.JwtFilter
import io.hazyone.springsecurityrestldapjwt.security.filter.RestAccessDeniedHandler
import io.hazyone.springsecurityrestldapjwt.security.filter.SecurityAuthEntryPoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.*
import org.springframework.ldap.core.ContextSource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.io.BufferedReader
import java.io.File

@Configuration
@EnableWebSecurity
class SecurityConfig @Autowired constructor(val contextSource: ContextSource, val ldapConfigProperties: LdapConfigProperties): WebSecurityConfigurerAdapter(true) {

    override fun configure(http: HttpSecurity) {
        val secret = File("secret.key").inputStream().bufferedReader().use(BufferedReader::readText)

        http.addFilterAfter(JwtFilter("/**", secret), ExceptionTranslationFilter::class.java)
            .addFilterAfter(corsFilter(), ExceptionTranslationFilter::class.java)
            .exceptionHandling()
            .authenticationEntryPoint(SecurityAuthEntryPoint())
            .accessDeniedHandler(RestAccessDeniedHandler())
            .and()
            .anonymous()
            .and()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated().and().cors()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.ldapAuthentication()
            .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator())
            .userDnPatterns(ldapConfigProperties.userDnPatterns)
            .contextSource()
                .url(ldapConfigProperties.url)
                .managerDn(ldapConfigProperties.managerDn)
                .managerPassword(ldapConfigProperties.managerPassword)
    }

    private fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.allowedHeaders = listOf(ORIGIN, CONTENT_TYPE, ACCEPT, ACCEPT, AUTHORIZATION)
        config.allowedMethods = listOf("GET", "PUT", "POST", "OPTIONS", "DELETE", "PATCH")
        config.maxAge = 3600L
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    fun ldapAuthoritiesPopulator() : LdapAuthoritiesPopulator? {
        if (!ldapConfigProperties.groupOfSearch) {
            //Specifies DefaultLdapAuthoritiesPopulator, which gets the Role by reading the members of available groups
            return null
        }
        //Specifies CustomLdapAuthoritiesPopulator, which retrieves the groups from the field memberOf of the user
        return CustomLdapAuthoritiesPopulator(contextSource)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }





}