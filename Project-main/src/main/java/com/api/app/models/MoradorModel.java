package com.api.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "morador")
@Data
public class MoradorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean ativo = true;

    private String nome;
    private String email;
    private String cpf;
    private LocalDate dataAniversario;
    private String telefonePrincipal;
    private String telefoneSecundario;
    private String profissao;

    @Column(precision = 10, scale = 2)
    private BigDecimal rendaMensal;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    // Relacionamentos simulados com stubs para evitar erro

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioModel usuario; // Pode ser substituído por Object ou removido se ainda não existir

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_id")
    private ProprietarioModel proprietario; // Pode ser substituído por Object ou removido se necessário
}
