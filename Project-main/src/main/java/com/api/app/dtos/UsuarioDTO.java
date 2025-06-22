package com.api.app.dtos;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String email;
    private Boolean ativo;
    private String observacao;
    private String tipoAcesso;
    private String senha;




    public UsuarioDTO(Long id, String email, Boolean ativo, String tipoAcesso, String senha) {
        this.id = id;
        this.email = email;
        this.ativo = ativo;
        this.tipoAcesso = tipoAcesso;
    }
}
