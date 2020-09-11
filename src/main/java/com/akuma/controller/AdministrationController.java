package com.akuma.controller;

import org.keycloak.admin.client.Keycloak;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class AdministrationController {

    private final Keycloak keycloak;

    @PostMapping( "/logout" )
    public String logout() {
        return "redirect:/login";
    }

}
