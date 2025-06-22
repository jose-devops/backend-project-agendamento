package com.api.app.dtos;

import com.api.app.models.enums.StatusAgendamento;
import com.api.app.models.enums.TipoAcesso;
import com.api.app.models.enums.TipoAgendamento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendamentoResponseDTO {

    private Long id;
    private String descricao;
    private TipoAgendamento tipoAgendamento;
    private TipoAcesso tipoUsuario;
    private String local;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusAgendamento status;
    private String observacao;

    private String nomeProprietario;
    private String nomeMorador;
}
