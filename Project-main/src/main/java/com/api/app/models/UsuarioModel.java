package com.api.app.models;

import com.api.app.models.enums.TipoAcesso;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    private String email;
    private String senha;

    @Column(nullable = false)
    private boolean ativo = true;



    @Enumerated(EnumType.STRING)
    private TipoAcesso tipoAcesso;  // Enum com valores, por exemplo, PROPRIETARIO e INQUILINO

    // Relacionamento OneToOne com o perfil Inquilino

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private MoradorModel morador;

    // Relacionamento OneToOne com o perfil Proprietário
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private ProprietarioModel proprietarip;



}
