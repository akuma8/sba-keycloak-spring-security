package com.akuma.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

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
