package com.api.app.controllers;

import com.api.app.models.enums.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enums")
@CrossOrigin(origins = "http://localhost:5173")

public class EnumController {

    @GetMapping("/tipo-agendamento")
    public TipoAgendamento[] getTiposAgendamento() {
        return TipoAgendamento.values();
    }

    @GetMapping("/status-agendamento")
    public StatusAgendamento[] getStatusAgendamento() {
        return StatusAgendamento.values();
    }



}
