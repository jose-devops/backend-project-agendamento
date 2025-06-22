package com.api.app.dtos;
import com.api.app.models.enums.Especialidade;
import lombok.Data;


@Data
public class PrestadorResponseDTO {

    private Long id;

    private String razao;

    private String cpfCnpj;

    private String telefonePrincipal;

    private String telefoneSecundario;

    private String linkWhatsapp;

    private Especialidade especialidade;

    private String observacao;


    public PrestadorResponseDTO(Long id, String razao, String cpfCnpj, String telefonePrincipal, String telefoneSecundario,
                                String linkWhatsapp, Especialidade especialidade, String observacao) {
        this.id = id;
        this.razao = razao;
        this.cpfCnpj = cpfCnpj;
        this.telefonePrincipal = telefonePrincipal;
        this.telefoneSecundario = telefoneSecundario;
        this.linkWhatsapp = linkWhatsapp;
        this.especialidade = especialidade;
        this.observacao = observacao;

    }
}