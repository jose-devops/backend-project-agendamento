package com.api.app.dtos;

import lombok.Data;

@Data
public class ProprietarioResponseDTO {
    private Long id;
    private String nome;
    private String razaoSocial;
    private String cpfCnpj;
    private String tipoPessoa;
    private String telefonePrincipal;
    private String telefoneSecundario;
    private String email;
    private String observacao;
    private String senha;

}
