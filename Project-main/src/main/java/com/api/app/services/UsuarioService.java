package com.api.app.services;

import com.api.app.dtos.UsuarioDTO;
import com.api.app.models.UsuarioModel;
import com.api.app.repositories.UsuarioRepository;
import com.api.app.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;



    // Método para autenticar o usuário com o email e senha
    public Optional<UsuarioModel> autenticar(String email, String senha) {
        Optional<UsuarioModel> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent() && usuario.get().getSenha().equals(senha)) {
            return usuario;
        }
        return Optional.empty();
    }

    // Método para criar um novo usuário
    public UsuarioModel criarUsuario(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    // Método para listar todos os usuários
    public List<UsuarioDTO> listarTodos() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();

        // Converte a lista de UsuarioModel para UsuarioDTO
        return usuarios.stream()
                .map(usuario -> new UsuarioDTO(
                        usuario.getId(),
                        usuario.getEmail(),
                        usuario.isAtivo(),
                        usuario.getTipoAcesso().toString(),
                        usuario.getSenha()

                ))
                .collect(Collectors.toList());
    }

    // Método para deletar um usuário pelo ID
    public void deletarUsuario(Long id) {
        Optional<UsuarioModel> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuarioRepository.delete(usuario.get());
        } else {
            throw new RuntimeException("Usuário não encontrado!");
        }
    }

    // Método para alterar um usuário
    public UsuarioModel alterarUsuario(Long id, UsuarioModel usuarioAtualizado) {
        Optional<UsuarioModel> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            UsuarioModel usuario = usuarioExistente.get();
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setSenha(usuarioAtualizado.getSenha());
            usuario.setAtivo(usuarioAtualizado.isAtivo());
            usuario.setTipoAcesso(usuarioAtualizado.getTipoAcesso());

            // Atualize outros campos conforme necessário
            return usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Usuário não encontrado!");
        }
    }


    public UsuarioModel getUsuarioLogado(HttpServletRequest request) {
        String token = request.getHeader("Authorization");  // Obtém o token da requisição
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // Remove o prefixo "Bearer "
        }

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token não encontrado ou inválido.");
        }

        // Extrai o email do token JWT
        String email = jwtUtil.extractUsername(token);  // Método que você tem no JwtUtil para extrair o username (email)

        // Busca o usuário no banco pelo email extraído
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }





}
