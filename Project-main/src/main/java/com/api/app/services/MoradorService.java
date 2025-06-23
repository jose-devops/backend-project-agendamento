package com.api.app.services;

import com.api.app.dtos.MoradorDTO;
import com.api.app.dtos.MoradorResponseDTO;
import com.api.app.models.MoradorModel;
import com.api.app.models.ProprietarioModel;
import com.api.app.models.UsuarioModel;
import com.api.app.models.enums.TipoAcesso;
import com.api.app.repositories.*;
import com.api.app.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoradorService {

    @Autowired
    private MoradorRepository moradorRepository;
    @Autowired
    private ProprietarioRepository proprietarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    public List<MoradorResponseDTO> listarMoradoresProprietario(UsuarioModel usuario) {
        if (!usuario.getTipoAcesso().isProprietario()) {
            throw new RuntimeException("Apenas proprietários podem listar moradores.");
        }

        List<MoradorModel> moradores = moradorRepository.findByProprietarioId(usuario.getId());

        return moradores.stream()
                .map(this::converterModelParaResponseDTO)
                .collect(Collectors.toList());
    }

    public MoradorResponseDTO listarMoradorLogado(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo"));

        if (!usuario.getTipoAcesso().isMorador()) {
            throw new RuntimeException("Apenas moradores podem acessar esse recurso.");
        }

        MoradorModel morador = moradorRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado para o usuário logado"));

        return converterModelParaResponseDTO(morador);
    }

    @Transactional
    public MoradorModel atualizar(Long id, MoradorDTO dto, UsuarioModel usuario) {
        MoradorModel morador = moradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

        boolean ehProprietarioDoMorador = morador.getProprietario() != null
                && morador.getProprietario().getId().equals(usuario.getId());

        boolean ehOMorador = morador.getUsuario() != null
                && morador.getUsuario().getId().equals(usuario.getId());

        if (!ehProprietarioDoMorador && !ehOMorador) {
            throw new RuntimeException("Você não tem permissão para editar este morador.");
        }

        morador.setNome(dto.getNome());
        morador.setEmail(dto.getEmail());
        morador.setTelefonePrincipal(dto.getTelefonePrincipal());
        morador.setTelefoneSecundario(dto.getTelefoneSecundario());
        morador.setCpf(dto.getCpf());
        morador.setRendaMensal(dto.getRendaMensal());
        morador.setDataAniversario(dto.getDataAniversario());
        morador.setProfissao(dto.getProfissao());
        morador.setObservacao(dto.getObservacao());

        if (morador.getUsuario() != null) {
            morador.getUsuario().setEmail(dto.getEmail());
            if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
                morador.getUsuario().setSenha(dto.getSenha());
            }
            usuarioRepository.save(morador.getUsuario());
        }

        // Removido: atualização do imóvel

        return moradorRepository.save(morador);
    }

    public MoradorModel buscarPorId(Long id, UsuarioModel usuario) {
        MoradorModel morador = moradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

        if (!morador.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você não tem permissão para visualizar este morador.");
        }

        return morador;
    }

    @Transactional
    public void deletarMoradorPorId(Long id, String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo"));

        MoradorModel morador = moradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

        if (!usuario.getTipoAcesso().equals(TipoAcesso.PROPRIETARIO)) {
            throw new RuntimeException("Apenas proprietários podem excluir moradores");
        }

        // Removido: verificação de contratos

        boolean possuiAgendamentos = agendamentoRepository.existsByMorador(morador);

        if (possuiAgendamentos) {
            throw new RuntimeException("Não é possível excluir o morador pois possui agendamentos vinculados.");
        }

        // Removido: desvincular imóveis

        moradorRepository.delete(morador);
        usuarioRepository.delete(morador.getUsuario());
    }

    public MoradorResponseDTO converterModelParaResponseDTO(MoradorModel model) {
        MoradorResponseDTO dto = new MoradorResponseDTO();
        dto.setId(model.getId());
        dto.setNome(model.getNome());
        dto.setCpf(model.getCpf());
        dto.setDataAniversario(model.getDataAniversario());
        dto.setTelefonePrincipal(model.getTelefonePrincipal());
        dto.setTelefoneSecundario(model.getTelefoneSecundario());
        dto.setProfissao(model.getProfissao());
        dto.setRendaMensal(model.getRendaMensal());
        dto.setObservacao(model.getObservacao());

        if (model.getUsuario() != null) {
            dto.setUsuarioEmail(model.getUsuario().getEmail());
        }

        if (model.getProprietario() != null) {
            dto.setProprietarioId(model.getProprietario().getId());
        }

        return dto;
    }

    @Transactional
    public void cadastrarPorProprietario(MoradorDTO dto, String token) {
        String emailProprietario = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuarioProprietario = usuarioRepository.findByEmail(emailProprietario)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

        if (usuarioProprietario.getTipoAcesso() != TipoAcesso.PROPRIETARIO) {
            throw new RuntimeException("Apenas proprietários podem cadastrar moradores");
        }

        UsuarioModel usuarioMorador = new UsuarioModel();
        usuarioMorador.setEmail(dto.getEmail());
        usuarioMorador.setSenha(dto.getSenha());
        usuarioMorador.setTipoAcesso(TipoAcesso.MORADOR);
        usuarioMorador.setAtivo(true);
        usuarioRepository.save(usuarioMorador);

        ProprietarioModel proprietario = proprietarioRepository.findByUsuario(usuarioProprietario)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado para este usuário"));

        MoradorModel morador = new MoradorModel();
        morador.setNome(dto.getNome());
        morador.setCpf(dto.getCpf());
        morador.setDataAniversario(dto.getDataAniversario());
        morador.setTelefonePrincipal(dto.getTelefonePrincipal());
        morador.setTelefoneSecundario(dto.getTelefoneSecundario());
        morador.setRendaMensal(dto.getRendaMensal());
        morador.setProfissao(dto.getProfissao());
        morador.setObservacao(dto.getObservacao());

        morador.setUsuario(usuarioMorador);
        morador.setProprietario(proprietario);
        morador.setEmail(usuarioMorador.getEmail());

        moradorRepository.save(morador);
    }

    @Transactional
    public void inativarMoradorPorId(Long id, String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo"));

        MoradorModel morador = moradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

        if (usuario.getTipoAcesso().equals(TipoAcesso.PROPRIETARIO)) {
            ProprietarioModel proprietario = proprietarioRepository.findByUsuarioAndAtivoTrue(usuario)
                    .orElseThrow(() -> new RuntimeException("Proprietário não encontrado ou inativo"));

            if (!morador.getProprietario().getId().equals(proprietario.getId())) {
                throw new RuntimeException("Este morador não pertence ao proprietário autenticado");
            }

        } else if (usuario.getTipoAcesso().equals(TipoAcesso.MORADOR)) {
            if (!morador.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Você não tem permissão para inativar outro morador");
            }

        } else {
            throw new RuntimeException("Tipo de usuário não autorizado");
        }

        morador.setAtivo(false);
        morador.getUsuario().setAtivo(false);
    }

    @Transactional
    public void inativarProprioCadastro(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou já inativo"));

        MoradorModel morador = moradorRepository.findByUsuarioAndAtivoTrue(usuario)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado ou já inativo"));

        morador.setAtivo(false);
        usuario.setAtivo(false);

        moradorRepository.save(morador);
        usuarioRepository.save(usuario);
    }
}
