package com.api.app.controllers;

import com.api.app.dtos.ProprietarioDTO;
import com.api.app.dtos.ProprietarioResponseDTO;
import com.api.app.models.ProprietarioModel;
import com.api.app.models.UsuarioModel;
import com.api.app.repositories.ProprietarioRepository;
import com.api.app.repositories.UsuarioRepository;
import com.api.app.security.JwtUtil;
import com.api.app.services.ProprietarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proprietario")
public class ProprietarioController {

    @Autowired
    private ProprietarioService proprietarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/cadastrar")
    public ResponseEntity<ProprietarioResponseDTO> cadastrarProprietario(@RequestBody ProprietarioModel proprietario,
                                                                   @RequestHeader("Authorization") String tokenHeader) {

        // Extrair o token removendo o prefixo "Bearer " se presente
        String token = tokenHeader.startsWith("Bearer ") ? tokenHeader.substring(7) : tokenHeader;


        // Extrair o email do usuário autenticado do token
        String emailFromToken = jwtUtil.extractUsername(token);


        // Buscar o usuário autenticado no banco de dados
        UsuarioModel usuario = usuarioRepository.findByEmailAndAtivoTrue(emailFromToken)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo"));

        // Associar o usuário autenticado ao proprietário
        proprietario.setUsuario(usuario);



        // Salvar o proprietário no banco de dados
        ProprietarioResponseDTO novoProprietario = proprietarioService.criarProprietario(proprietario);


        // Retornar o novo proprietário com status CREATED
        return new ResponseEntity<>(novoProprietario, HttpStatus.CREATED);
    }






    @PutMapping("/editar")
    public ResponseEntity<?> editarProprietario(@RequestBody ProprietarioDTO dto,
                                                @RequestHeader("Authorization") String token) {
        proprietarioService.editar(dto, token);
        return ResponseEntity.ok("Proprietário atualizado com sucesso!");
    }

    @GetMapping("/me")
    public ResponseEntity<ProprietarioResponseDTO> getProprietarioLogado(
            @RequestHeader("Authorization") String token) {
        ProprietarioResponseDTO dto = proprietarioService.buscarPerfilProprietario(token);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/deletar")
    public ResponseEntity<String> deletarProprioCadastro(@RequestHeader("Authorization") String token) {
        proprietarioService.deletarProprioCadastro(token);
        return ResponseEntity.ok("Proprietário e usuário excluídos com sucesso.");
    }

    @DeleteMapping("/inativar")
    public ResponseEntity<String> inativarCadastro(@RequestHeader("Authorization") String token) {
        proprietarioService.inativarProprioCadastro(token);
        return ResponseEntity.ok("Cadastro inativado com sucesso!");
    }





}
