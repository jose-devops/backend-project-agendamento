package com.api.app.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("base")
public class BaseController {
    @GetMapping("/minharota")
    public String retornaTexto(){
        return "MInha requisição deu certo";
    }
}
