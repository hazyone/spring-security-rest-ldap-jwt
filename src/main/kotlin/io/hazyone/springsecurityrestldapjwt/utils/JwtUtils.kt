package app.docstamper.docstamper.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils

import java.util.*

const val ROLES_CLAIM = "roles"
const val AUDIENCE_WEB = "web"

fun generateToken(subject: String, authorities: Collection<GrantedAuthority>, secret: String, expirationInMinutes: Int) : String {
    val algorithm = Algorithm.HMAC256(secret)
    val currentDate = Date(System.currentTimeMillis())
    val expireDate = Date(System.currentTimeMillis() + expirationInMinutes * 60000)
    val roles = AuthorityUtils.authorityListToSet(authorities).joinToString()
    return JWT.create()
        .withSubject(subject)
        .withExpiresAt(expireDate)
        .withIssuedAt(currentDate)
        .withClaim(ROLES_CLAIM, roles)
        .withAudience(AUDIENCE_WEB)
        .sign(algorithm)
}

fun verifyAndDecodeJwt(token: String, secret: String) : DecodedJWT {
    val algorithm = Algorithm.HMAC256(secret)
    return JWT.require(algorithm).build().verify(token)
}

fun getRoles(jwt: DecodedJWT) : Collection<GrantedAuthority> {
    return AuthorityUtils.commaSeparatedStringToAuthorityList(jwt.getClaim(ROLES_CLAIM).asString())
}
