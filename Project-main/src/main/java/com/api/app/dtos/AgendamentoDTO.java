package com.api.app.dtos;

import com.api.app.models.enums.StatusAgendamento;
import com.api.app.models.enums.TipoAcesso;
import com.api.app.models.enums.TipoAgendamento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendamentoDTO {

    private Long id;
    private String descricao;
    private TipoAgendamento tipoAgendamento;
    private TipoAcesso tipoAcesso;
    private String local;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusAgendamento status;
    private String observacao;

    private Long proprietarioId;
    private Long moradorId;
}
