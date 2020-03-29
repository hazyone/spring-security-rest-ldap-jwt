package io.hazyone.springsecurityrestldapjwt.security.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.core.ContextSource
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.ldap.SpringSecurityLdapTemplate
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator
import javax.naming.ldap.LdapName

class CustomLdapAuthoritiesPopulator @Autowired constructor(contextSource: ContextSource) : LdapAuthoritiesPopulator {

    private val GROUP_ATTRIBUTE = "cn"
    private val GROUP_MEMBER_OF = "memberof"
    private val ldapTemplate: SpringSecurityLdapTemplate = SpringSecurityLdapTemplate(contextSource)

    override fun getGrantedAuthorities(userData: DirContextOperations, username: String): Collection<GrantedAuthority> {
        val groupDns = userData.getStringAttributes(GROUP_MEMBER_OF)

        val roles = groupDns.map { groupDn ->
            {
                val groupLdapName = ldapTemplate.retrieveEntry(groupDn, arrayOf(GROUP_ATTRIBUTE)).dn as LdapName
                groupLdapName.rdns.map { it.type }.reduce { _, b -> b } ?: ""
            }
        }

        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles.joinToString(separator = ","))
    }


}