package com.api.app.repositories;

import com.api.app.models.AgendamentoModel;
import com.api.app.models.MoradorModel;
import com.api.app.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoRepository extends JpaRepository<AgendamentoModel, Long> {
    List<AgendamentoModel> findByProprietarioUsuario(UsuarioModel usuario);
    List<AgendamentoModel> findByMoradorUsuario(UsuarioModel usuario);
    boolean existsByMorador(MoradorModel morador);


}
