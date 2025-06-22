package com.api.app.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Data
public class MoradorDTO {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private LocalDate dataAniversario;
    private String telefonePrincipal;
    private String telefoneSecundario;
    private String profissao;
    private BigDecimal rendaMensal;
    private String observacao;
    private Long usuarioId;
    private Long imovelId;
}
