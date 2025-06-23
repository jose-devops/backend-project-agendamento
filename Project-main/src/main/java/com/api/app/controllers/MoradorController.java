package com.api.app.controllers;

import com.api.app.dtos.MoradorDTO;
import com.api.app.dtos.MoradorResponseDTO;
import com.api.app.models.MoradorModel;
import com.api.app.models.UsuarioModel;
import com.api.app.services.MoradorService;
import com.api.app.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/morador")
public class MoradorController {

    @Autowired
    private MoradorService moradorService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar/me")
    public ResponseEntity<MoradorResponseDTO> listarMoradorLogado(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(moradorService.listarMoradorLogado(token));
    }


    @GetMapping("/listar")
    public ResponseEntity<List<MoradorResponseDTO>> listarMoradores(HttpServletRequest request) {
        UsuarioModel usuario = usuarioService.getUsuarioLogado(request); // Obtém o usuário logado via token
        List<MoradorResponseDTO> moradores = moradorService.listarMoradoresProprietario(usuario);
        return ResponseEntity.ok(moradores);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<MoradorResponseDTO> buscarPorId(@PathVariable Long id, HttpServletRequest request) {
        UsuarioModel usuario = usuarioService.getUsuarioLogado(request); // Obtém o usuário logado via token
        MoradorModel morador = moradorService.buscarPorId(id, usuario);
        MoradorResponseDTO responseDTO = moradorService.converterModelParaResponseDTO(morador);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<MoradorModel> atualizarMorador(@PathVariable Long id, @RequestBody MoradorDTO dto, HttpServletRequest request) {
        UsuarioModel usuario = usuarioService.getUsuarioLogado(request); // Obtém o usuário logado via token
        MoradorModel moradorAtualizado = moradorService.atualizar(id, dto, usuario);
        return ResponseEntity.ok(moradorAtualizado);
    }

    @DeleteMapping("/deletar/{id}")
    @PreAuthorize("hasRole('ROLE_PROPRIETARIO')")
    public ResponseEntity<String> deletarMorador(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        moradorService.deletarMoradorPorId(id, token);
        return ResponseEntity.ok("Morador deletado com sucesso!");
    }

    @PostMapping("/cadastrar-por-proprietario")
    public ResponseEntity<?> cadastrarPorProprietario(@RequestBody MoradorDTO dto,
                                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        moradorService.cadastrarPorProprietario(dto, token);
        return ResponseEntity.ok("Morador cadastrado com sucesso pelo proprietário.");
    }



    @DeleteMapping("/inativar/{id}")

    public ResponseEntity<String> inativarMorador(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        moradorService.inativarMoradorPorId(id, token);
        return ResponseEntity.ok("Morador inativado com sucesso!");
    }


}
