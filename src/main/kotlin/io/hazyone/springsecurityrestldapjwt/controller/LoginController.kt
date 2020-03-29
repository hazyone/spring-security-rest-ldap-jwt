package io.hazyone.springsecurityrestldapjwt.controller

import app.docstamper.docstamper.utils.generateToken
import io.hazyone.springsecurityrestldapjwt.config.JwtConfigProperties
import io.hazyone.springsecurityrestldapjwt.security.model.LoginRequest
import io.hazyone.springsecurityrestldapjwt.security.model.LoginResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.io.File

@RestController
@RequestMapping("/api")
class LoginController @Autowired constructor(
    private val authenticationManager: AuthenticationManager,
    private val jwtConfigProperties: JwtConfigProperties
) {

    @PostMapping("/auth")
    fun loginRequest(@RequestBody loginRequest: LoginRequest) : ResponseEntity<*> {
        val username = loginRequest.username
        val password = loginRequest.password
        val authentication = this.authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )
        SecurityContextHolder.getContext().authentication = authentication
        val secret = File("secret.key").inputStream().bufferedReader().use(BufferedReader::readText)
        val token = generateToken(username, authentication.authorities, secret, jwtConfigProperties.expiresInMinutes)
        return ResponseEntity.ok(LoginResponse(token))
    }

    @GetMapping("/user")
    fun getUserInfo(@RequestParam name: String) : ResponseEntity<*> {
        return ResponseEntity.ok(mapOf("name" to name, "role" to "admin" ))
    }
}