package com.api.app.repositories;

import com.api.app.models.MoradorModel;
import com.api.app.models.ProprietarioModel;
import com.api.app.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoradorRepository extends JpaRepository<MoradorModel, Long> {
    Optional<MoradorModel> findByIdAndAtivoTrue(Long id);
    Optional<MoradorModel> findByUsuarioAndAtivoTrue(UsuarioModel usuario);
    Optional<MoradorModel> findByEmail(String email);
    public List<MoradorModel> findByProprietarioId(Long proprietarioId);
    List<MoradorModel> findByProprietarioAndAtivoTrue(ProprietarioModel proprietario);
    Optional<MoradorModel> findByUsuario(UsuarioModel usuario);








}
