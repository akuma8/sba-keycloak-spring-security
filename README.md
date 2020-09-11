# sba-keycloak-spring-security
Spring Boot Admin (https://codecentric.github.io/spring-boot-admin/current) secured by Spring Security OAuth2 with Keycloak as authorization server

To secure the application I directly use Spring Security 5 OAuth2 with the Authorization Code grant type, docs : https://docs.spring.io/spring-security/site/docs/current/reference/html5/#oauth2login

I don't use the Keycloak Security Adapter: https://www.keycloak.org/docs/latest/securing_apps/#_spring_boot_adapter

# How to run the project:
1- Run a Keycloak instance (you can use a docker container https://hub.docker.com/r/jboss/keycloak) at `localhost:8180` and create a `sba` realm. 

2- In that realm, create a `confidential` client `adminservice` which should have `Standard Flow Enabled`, `Direct Access Grants Enabled` and `Service Accounts Enabled` enabled.
The `Root URL` is equal to `http://localhost:8086` and the ` Valid Redirect URIs` is `/login/oauth2/code/keycloak`

3- Create a Roles named `actuator` and a User, lets name it `admin`

4- Add the role `actuator` to the `admin` user 

4- Create a `Client Scopes` named `actuator_access` and in the `Mappers` tab, it should have access to `realm roles`. And in the `Scopes` tab, assign it the `actuator` role.

5- Return back to the client `adminservice`, `Service Account Roles` tab and assign it the `actuator` role. In the `Client Scopes` tab, assign the `actuator_access` scope as `Default Client Scopes`

# Run the application :
Prerequired: JDK11-Maven 3.6. It's a Spring Boot application. 

Visit 'http://localhost:8086' and you should be redirected to Keyloak for authentication.

# Problems :
After authentication, Keycloak redirects us to our application, at `http://localhost:8086/login/oauth2/code/keycloak`.

Security rules are defined here:

```
package com.akuma.configuration;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure( HttpSecurity http ) throws Exception {
        http.csrf().disable().cors().disable();
        http.authorizeRequests()
                .antMatchers( "/error","/instances", "/**/*.css", "/**/img/**", "/**/third-party/**", "/*.js" )
                .permitAll()
                .anyRequest().hasRole( "ACTUATOR" )
                .and()
                .oauth2Login( oauth2 -> oauth2.userInfoEndpoint(
                        userInfo -> userInfo.oidcUserService( this.oidcUserService() ) ) );
    }


    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return ( userRequest ) -> {
            OidcUser oidcUser = delegate.loadUser( userRequest );
            //TODO find a way to extract roles
            OAuth2AccessToken accessToken = userRequest.getAccessToken();
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            oidcUser = new DefaultOidcUser( mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo() );

            return oidcUser;
        };
    }
}
```

As you can see the user should have the role `ACTUATOR` but Spring Security does not have access to our Keycloak realm roles.
I have to find a way to extract it from the `Access Token`. 
