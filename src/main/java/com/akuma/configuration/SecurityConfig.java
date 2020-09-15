package com.akuma.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuer;

    @Override
    protected void configure( HttpSecurity http ) throws Exception {
        http.csrf().disable().cors().disable();
        http.authorizeRequests()
                .antMatchers( "/error","/instances", "/**/*.css", "/**/img/**", "/**/third-party/**", "/*.js" )
                .permitAll()
                .anyRequest().hasRole( "ACTUATOR" )
                .and()
                .oauth2Login( oauth2 -> oauth2.userInfoEndpoint(
                        userInfo -> userInfo.userService( this.oidcUserService() ) ) );
    }


    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oidcUserService() {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return ( userRequest ) -> {
            OAuth2User oAuth2User = delegate.loadUser( userRequest );
            OAuth2AccessToken accessToken = userRequest.getAccessToken();
            Jwt jwt = JwtDecoders.fromIssuerLocation(issuer).decode(accessToken.getTokenValue());
            //TODO parse the JWT to extract roles
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
          return  new DefaultOAuth2User( mappedAuthorities, oAuth2User.getAttributes(), "preferred_username" );

        };
    }

}
