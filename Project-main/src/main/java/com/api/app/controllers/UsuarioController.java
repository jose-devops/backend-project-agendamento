package com.api.app.controllers;

import com.api.app.dtos.LoginRequest;
import com.api.app.dtos.LoginResponse;
import com.api.app.dtos.UsuarioDTO;
import com.api.app.models.enums.TipoAcesso;
import com.api.app.models.UsuarioModel;
import com.api.app.repositories.UsuarioRepository;
import com.api.app.services.UsuarioService;
import com.api.app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest login) {
        Optional<UsuarioModel> user = usuarioRepository.findByEmail(login.getEmail());
        if (user.isPresent() && user.get().getSenha().equals(login.getSenha())) {
            // Gera o token com base no email e no tipo de acesso (role)
            String token = jwtUtil.generateToken(user.get().getEmail(), user.get().getTipoAcesso().name());
            return new LoginResponse(token);
        }
        throw new RuntimeException("Credenciais inválidas");
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> criarUsuario(@RequestBody UsuarioModel usuario) {
        Optional<UsuarioModel> existingUser = usuarioRepository.findByEmail(usuario.getEmail());
        if(existingUser.isPresent()){
            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }
        usuario.setAtivo(true);
        if (usuario.getTipoAcesso() == null) {
            usuario.setTipoAcesso(TipoAcesso.PROPRIETARIO);
        }
        UsuarioModel savedUser = usuarioRepository.save(usuario);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuariosDTO = usuarioService.listarTodos();
        return new ResponseEntity<>(usuariosDTO, HttpStatus.OK);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.ok("Usuário deletado com sucesso!");
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<Object> alterarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioModel usuarioAtualizado,
            @RequestHeader("Authorization") String tokenHeader) {

        // Log inicial para depuração
        System.out.println("=== Entrou no endpoint /usuario/alterar/" + id + " ===");
        System.out.println("Body recebido: " + usuarioAtualizado);

        // Extrai o token removendo o prefixo "Bearer " se presente
        String token = tokenHeader.startsWith("Bearer ") ? tokenHeader.substring(7) : tokenHeader;
        System.out.println("Token extraído: " + token);

        if (!jwtUtil.validateToken(token)) {
            System.out.println("Token inválido ou expirado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido ou expirado");
        }

        // Extrai o email e o role do token
        String emailFromToken = jwtUtil.extractUsername(token);
        String roleFromToken = jwtUtil.extractRole(token);
        System.out.println("RoleFromToken = " + roleFromToken + ", EmailFromToken = " + emailFromToken);

        // Busca o usuário a ser alterado
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findById(id);
        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuário com id " + id + " não encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado!");
        }
        UsuarioModel usuario = usuarioOpt.get();
        System.out.println("Usuário encontrado: " + usuario);

        // Se o usuário logado for INQUILINO, só pode alterar seu próprio cadastro.
        // Se for PROPRIETARIO, permite editar qualquer usuário, sem verificar o email.
        if ("ROLE_INQUILINO".equals(roleFromToken) && !usuario.getEmail().equals(emailFromToken)) {
            System.out.println("Bloqueado: Inquilino tentando editar outro usuário. Email do cadastro: "
                    + usuario.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Inquilino não tem permissão para editar outro usuário!");
        }

        System.out.println("Verificações de permissão concluídas. Prosseguindo com a alteração.");

        // Atualiza os dados do usuário
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setSenha(usuarioAtualizado.getSenha());
        usuario.setAtivo(usuarioAtualizado.isAtivo());
        usuario.setTipoAcesso(usuarioAtualizado.getTipoAcesso());

        System.out.println("Dados atualizados: " + usuario);

        usuarioRepository.save(usuario);
        System.out.println("Usuário salvo com sucesso.");

        return ResponseEntity.ok(usuario);
    }











}
