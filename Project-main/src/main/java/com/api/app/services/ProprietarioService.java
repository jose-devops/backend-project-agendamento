package com.api.app.services;

import com.api.app.dtos.ProprietarioDTO;
import com.api.app.dtos.ProprietarioResponseDTO;
import com.api.app.models.ProprietarioModel;
import com.api.app.models.enums.TipoProprietario;
import com.api.app.models.UsuarioModel;
import com.api.app.repositories.ProprietarioRepository;
import com.api.app.repositories.UsuarioRepository;
import com.api.app.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProprietarioService {

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Criar um novo proprietário
    public ProprietarioResponseDTO criarProprietario(ProprietarioModel proprietario) {
        ProprietarioModel salvo = proprietarioRepository.save(proprietario);

        ProprietarioResponseDTO dto = new ProprietarioResponseDTO();
        dto.setNome(salvo.getNome());
        dto.setRazaoSocial(salvo.getRazaoSocial());
        dto.setTipoPessoa(salvo.getTipo().name());
        dto.setTelefonePrincipal(salvo.getTelefonePrincipal());
        dto.setTelefoneSecundario(salvo.getTelefoneSecundario());
        dto.setEmail(salvo.getUsuario().getEmail());

        return dto;
    }


    @Transactional
    public void deletarProprioCadastro(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ProprietarioModel proprietario = proprietarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

        proprietarioRepository.delete(proprietario);
        usuarioRepository.delete(usuario);
    }


    @Transactional
    public void inativarProprioCadastro(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));

        UsuarioModel usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou já inativo"));

        ProprietarioModel proprietario = proprietarioRepository.findByUsuarioAndAtivoTrue(usuario)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado ou já inativo"));

        // Inativando os registros
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        proprietario.setAtivo(false);
        proprietarioRepository.save(proprietario);
    }









    @Transactional
    public void editar(ProprietarioDTO dto, String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha()); // use BCrypt futuramente
        usuarioRepository.save(usuario);

        ProprietarioModel proprietario = proprietarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

        proprietario.setNome(dto.getNome());
        proprietario.setRazaoSocial(dto.getRazaoSocial());
        proprietario.setTipo(TipoProprietario.valueOf(dto.getTipoPessoa()));
        proprietario.setTelefonePrincipal(dto.getTelefonePrincipal());
        proprietario.setTelefoneSecundario(dto.getTelefoneSecundario());
        proprietario.setEmail(dto.getEmail());
        proprietario.setObservacao(dto.getObservacao());
        proprietario.setCpfCnpj(dto.getCpfCnpj());

        proprietarioRepository.save(proprietario);
    }


    public ProprietarioResponseDTO buscarPerfilProprietario(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ProprietarioModel proprietario = proprietarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

        ProprietarioResponseDTO dto = new ProprietarioResponseDTO();
        dto.setId(proprietario.getId());
        dto.setNome(proprietario.getNome());
        dto.setRazaoSocial(proprietario.getRazaoSocial());
        dto.setTipoPessoa(proprietario.getTipo().name());
        dto.setTelefonePrincipal(proprietario.getTelefonePrincipal());
        dto.setTelefoneSecundario(proprietario.getTelefoneSecundario());
        dto.setEmail(usuario.getEmail());
        dto.setCpfCnpj(proprietario.getCpfCnpj());
        dto.setObservacao(proprietario.getObservacao());

        return dto;
    }

}
