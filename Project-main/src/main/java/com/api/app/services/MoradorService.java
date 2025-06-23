package com.api.app.services;

import com.api.app.dtos.MoradorDTO;
import com.api.app.dtos.MoradorResponseDTO;
import com.api.app.models.*;
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
    private ImovelRepository imovelRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;




    public List<MoradorResponseDTO> listarMoradoresProprietario(UsuarioModel usuario) {
        // Verificar se o usuário logado é um proprietário
        if (!usuario.getTipoAcesso().isProprietario()) {
            throw new RuntimeException("Apenas proprietários podem listar moradores.");
        }

        // Buscar todos os moradores vinculados ao proprietário logado
        List<MoradorModel> moradores = moradorRepository.findByProprietarioId(usuario.getId());

        // Converter para DTO
        return moradores.stream()
                .map(morador -> converterModelParaResponseDTO(morador))
                .collect(Collectors.toList());
    }


    public MoradorResponseDTO listarMoradorLogado(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo"));

        if (!usuario.getTipoAcesso().isMorador()) {
            throw new RuntimeException("Apenas moradores podem acessar esse recurso.");
        }

        // Garantir apenas 1 morador por usuário
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

        // Atualizar os campos do morador com os valores do DTO
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


        // Se o imóvel foi alterado, atualizar o imóvel do morador
        if (dto.getImovelId() != null) {
            ImovelModel imovel = imovelRepository.findById(dto.getImovelId())
                    .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));
            morador.setImovel(imovel);  // Vincula o morador a um novo imóvel
        }

        // Salvar as alterações no banco de dados
        return moradorRepository.save(morador);
    }



    public MoradorModel buscarPorId(Long id, UsuarioModel usuario) {
        // Buscar o morador pelo ID
        MoradorModel morador = moradorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

        // Verificar se o morador pertence ao usuário
        if (!morador.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você não tem permissão para visualizar este morador.");
        }

        return morador;
    }







/* AJUSTAR PARA FUTURO ACESSO DE ADMINISTRADOR*/

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

        // Verificações de vínculo
        boolean possuiContratos = contratoRepository.existsByMorador(morador);
        boolean possuiAgendamentos = agendamentoRepository.existsByMorador(morador);

        if (possuiContratos || possuiAgendamentos) {
            StringBuilder mensagem = new StringBuilder("Não é possível excluir o morador. Motivo:");

            if (possuiContratos) mensagem.append(" possui contratos vinculados");
            if (possuiContratos && possuiAgendamentos) mensagem.append(" e");
            if (possuiAgendamentos) mensagem.append(" possui agendamentos vinculados");

            mensagem.append(". Remova os vínculos antes de prosseguir.");
            throw new RuntimeException(mensagem.toString());
        }

        // Desvincular o morador de imóveis (campo nullable)
        List<ImovelModel> imoveis = imovelRepository.findByMorador(morador);
        for (ImovelModel imovel : imoveis) {
            imovel.setMorador(null);
            imovelRepository.save(imovel);
        }

        // Deletar morador e usuário associado
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

        // Adicionar o email do usuário (não o objeto completo)
        if (model.getUsuario() != null) {
            dto.setUsuarioEmail(model.getUsuario().getEmail());
        }

        // Retornar apenas o ID do proprietário
        if (model.getProprietario() != null) {
            dto.setProprietarioId(model.getProprietario().getId());
        }

        return dto;
    }

    private MoradorModel converterDTOparaModel(MoradorDTO dto) {
        MoradorModel morador = new MoradorModel();
        morador.setNome(dto.getNome());
        morador.setCpf(dto.getCpf());
        morador.setDataAniversario(dto.getDataAniversario());
        morador.setTelefonePrincipal(dto.getTelefonePrincipal());
        morador.setTelefoneSecundario(dto.getTelefoneSecundario());
        morador.setProfissao(dto.getProfissao());
        morador.setRendaMensal(dto.getRendaMensal());
        morador.setObservacao(dto.getObservacao());

        if (dto.getUsuarioId() != null) {
            UsuarioModel usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            morador.setUsuario(usuario);
        }

        return morador;
    }


    @Transactional
    public void cadastrarPorProprietario(MoradorDTO dto, String token) {
        String emailProprietario = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        UsuarioModel usuarioProprietario = usuarioRepository.findByEmail(emailProprietario)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

        if (usuarioProprietario.getTipoAcesso() != TipoAcesso.PROPRIETARIO) {
            throw new RuntimeException("Apenas proprietários podem cadastrar moradores");
        }

        // Criar usuário do morador
        UsuarioModel usuarioMorador = new UsuarioModel();
        usuarioMorador.setEmail(dto.getEmail());
        usuarioMorador.setSenha(dto.getSenha()); // Use BCrypt depois
        usuarioMorador.setTipoAcesso(TipoAcesso.MORADOR);
        usuarioMorador.setAtivo(true);
        usuarioRepository.save(usuarioMorador);

        // Obter o proprietário
        ProprietarioModel proprietario = proprietarioRepository.findByUsuario(usuarioProprietario)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado para este usuário"));

        // Criar morador
        MoradorModel morador = new MoradorModel();


        morador.setNome(dto.getNome());
        morador.setCpf(dto.getCpf());
        morador.setDataAniversario(dto.getDataAniversario());
        morador.setTelefonePrincipal(dto.getTelefonePrincipal());
        morador.setTelefoneSecundario(dto.getTelefoneSecundario());
        morador.setRendaMensal(dto.getRendaMensal());
        morador.setProfissao(dto.getProfissao());
        morador.setObservacao(dto.getObservacao());

        // vinculos
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
