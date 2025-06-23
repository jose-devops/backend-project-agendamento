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

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_id")
    @JsonIgnore
    private ProprietarioModel proprietario;

    @ManyToOne
    @JoinColumn(name = "imovel_id")
    private ImovelModel imovel;
}
