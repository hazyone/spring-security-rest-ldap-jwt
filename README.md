# Kotlin Spring Boot application with stateless security using JWT and LDAP 
Forked from the [Spring Security REST JWT LDAP repository](https://github.com/SNCF-SIV/spring-security-rest-jwt-ldap) which is coded with JAVA and has no internal LDAP server for testing purposes.

Uses an internal or a suitable external LDAP implementation. It also allows you to choose how to obtain a user role in LDAP: 
- check for the presence of a user in available groups
- retrieve user memberof attribute

#### Requirements
 * Kotlin 1.3 (jvm 1.8)
 * Docker (in case you need an openldap container)

### LDAP Settings
Change your settings inside application.yml if you need an external LDAP server. In this case you have to set managerDn parameter to uid only string (without RDN, just username)

### Usage examples
- GET request to restricted path with no JWT
```shell script
# HTTP 401 Forbidden status is expected
curl http://localhost:8080/api/user?name=admin
```
response:
```json
{"data":"","metadata":{"status":401},"errors":[{"code":401,"message":"Full authentication is required to access this resource","detail":""}]}
```
- Login with existing user creds (see test-server.ldif)
```shell script
curl -i -H "Content-Type: application/json" -X POST -d '{
    "username": "ben",
    "password": "ben"
}' http://localhost:8080/api/auth
```
response:
```json
{"token":"xxx.yyyy.zzz"}
```
- GET request to restricted path with valid JWT token
```shell script
curl -H "Content-Type: application/json" \
-H "Authorization: Bearer ${token}" \
http://localhost:8080/api/user?name=admin
```
response:
```json
{"name":"admin","role":"admin"}
```
