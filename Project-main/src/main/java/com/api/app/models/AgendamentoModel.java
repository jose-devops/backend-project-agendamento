package com.api.app.models;

import com.api.app.models.enums.StatusAgendamento;
import com.api.app.models.enums.TipoAcesso;
import com.api.app.models.enums.TipoAgendamento;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
@Data
public class AgendamentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoAgendamento tipoAgendamento;

    @Enumerated(EnumType.STRING)
    private TipoAcesso tipoAcesso;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;

    private String observacao;

    private String local;

    @ManyToOne
    @JoinColumn(name = "proprietario_id")
    private ProprietarioModel proprietario;

    @ManyToOne
    @JoinColumn(name = "morador_id")
    private MoradorModel morador;
}
