package io.hazyone.springsecurityrestldapjwt.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.hazyone.springsecurityrestldapjwt.model.ErrorMessage
import io.hazyone.springsecurityrestldapjwt.model.ResponseWrapper
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

class SecurityAuthEntryPoint : AuthenticationEntryPoint {

    override fun commence(rq: HttpServletRequest, rs: HttpServletResponse, authException: AuthenticationException) {
        val responseWrapper = ResponseWrapper(
            metadata = mapOf("status" to HttpServletResponse.SC_UNAUTHORIZED),
            errors = listOf(ErrorMessage(401, authException.message ?: "", ""))
        )
        val objMapper = ObjectMapper()

        val wrapper = HttpServletResponseWrapper(rs)

        wrapper.status = HttpServletResponse.SC_UNAUTHORIZED
        wrapper.contentType = MediaType.APPLICATION_JSON_VALUE
        wrapper.writer.println(objMapper.writeValueAsString(responseWrapper))
        wrapper.writer.flush()
    }
}