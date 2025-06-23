package com.api.app.repositories;

import com.api.app.models.ProprietarioModel;
import com.api.app.models.UsuarioModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProprietarioRepository extends JpaRepository<ProprietarioModel, Long> {
    @EntityGraph(attributePaths = {"usuario"})
    List<ProprietarioModel> findAll();

    Optional<ProprietarioModel> findByUsuario(UsuarioModel usuario);
    Optional<ProprietarioModel> findByUsuarioAndAtivoTrue(UsuarioModel usuario);


}
