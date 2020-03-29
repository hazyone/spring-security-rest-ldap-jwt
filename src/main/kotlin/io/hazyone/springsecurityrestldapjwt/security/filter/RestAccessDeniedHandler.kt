package io.hazyone.springsecurityrestldapjwt.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.hazyone.springsecurityrestldapjwt.model.ErrorMessage
import io.hazyone.springsecurityrestldapjwt.model.ResponseWrapper
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

class RestAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(rq: HttpServletRequest, rs: HttpServletResponse, accessDeniedException: AccessDeniedException) {
        val responseWrapper = ResponseWrapper(
            metadata = mapOf("status" to HttpServletResponse.SC_FORBIDDEN),
            errors = listOf(ErrorMessage(401, accessDeniedException.message ?: "", ""))
        )
        val objMapper = ObjectMapper()

        val wrapper = HttpServletResponseWrapper(rs)

        wrapper.status = HttpServletResponse.SC_FORBIDDEN
        wrapper.contentType = MediaType.APPLICATION_JSON_VALUE
        wrapper.writer.println(objMapper.writeValueAsString(responseWrapper))
        wrapper.writer.flush()
    }
}