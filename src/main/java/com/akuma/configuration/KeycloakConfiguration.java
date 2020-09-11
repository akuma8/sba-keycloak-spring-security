package com.akuma.configuration;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;

@Configuration
public class KeycloakConfiguration {

    @Bean
    public HttpHeadersProvider keycloakBearerAuthHeaderProvider( Keycloak keycloak ) {
        return ( instance ) -> {
            String accessToken = keycloak.tokenManager().getAccessTokenString();
            HttpHeaders headers = new HttpHeaders();
            headers.add( HttpHeaders.AUTHORIZATION, "Bearer " + accessToken );
            return headers;
        };
    }

    @Bean
    public Keycloak keycloakInstance( KeycloakProperties properties ) {
        return KeycloakBuilder.builder()
                .serverUrl( properties.getAuthServerUrl() )
                .realm( properties.getRealm() )
                .clientId( properties.getResource() )
                .clientSecret( properties.getCredentials().get( "secret" ) )
                .grantType( OAuth2Constants.CLIENT_CREDENTIALS )
                .build();
    }
}
