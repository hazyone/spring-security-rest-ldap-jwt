package io.hazyone.springsecurityrestldapjwt.security.filter

import app.docstamper.docstamper.utils.getRoles
import app.docstamper.docstamper.utils.verifyAndDecodeJwt
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import io.hazyone.springsecurityrestldapjwt.security.exception.MalformedJwtToken
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(private val path: String, val secretKey: String) : GenericFilterBean() {

    private val TOKEN_TYPE = "Bearer "
    private val requestMatcher = AntPathRequestMatcher(path)

    override fun doFilter(rq: ServletRequest, rs: ServletResponse, chain: FilterChain) {

        val request = rq as HttpServletRequest
        val response = rs as HttpServletResponse

        if (!requestMatcher.matches(request)) {
            chain.doFilter(request, response)
            return
        }

        val authHeader: String = request.getHeader(AUTHORIZATION) ?: ""
        if (!authHeader.startsWith(TOKEN_TYPE)) {
            chain.doFilter(request, response)
            return
        }

        try {
            val token = verifyAndDecodeJwt(authHeader.substringAfter(TOKEN_TYPE), secretKey)
            SecurityContextHolder.getContext().authentication = buildJwtAuthentication(token, request)

            chain.doFilter(request, response)
        } catch (ex: Throwable) {
            when (ex)  {
                is AlgorithmMismatchException,
                is SignatureVerificationException,
                is InvalidClaimException -> throw MalformedJwtToken("Token is malformed")
                is TokenExpiredException -> throw AccountExpiredException("Token is expired")
                else -> throw ex
            }
        }

        SecurityContextHolder.clearContext()
    }

    private fun buildJwtAuthentication(token: DecodedJWT, request: HttpServletRequest) : Authentication {
        val userDetails = User.builder()
            .username(token.subject)
            .authorities(getRoles(token))
            .password("")
            .build()
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        //TODO: Check if details are needed in future
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        return authentication
    }

}