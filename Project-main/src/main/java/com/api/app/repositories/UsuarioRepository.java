package com.api.app.repositories;

import com.api.app.models.ProprietarioModel;
import com.api.app.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {


    Optional<UsuarioModel> findByEmailAndAtivoTrue(String email);
    Optional<UsuarioModel> findByEmail(String email);
}
