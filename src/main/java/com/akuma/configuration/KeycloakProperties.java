package com.akuma.configuration;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors
@Configuration
@ConfigurationProperties( prefix = "keycloak" )
public class KeycloakProperties {
    private String              authServerUrl;
    private String              realm;
    private String              realmKey;
    private String              secret;
    private String              resource;
    private Map<String, String> credentials;
    private boolean             useResourceRoleMappings;
    private String              sslRequired;
    private String              principalAttribute;
    private int                 confidentialPort;
}
