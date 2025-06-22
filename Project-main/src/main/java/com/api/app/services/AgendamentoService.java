package com.api.app.services;

import com.api.app.dtos.AgendamentoDTO;
import com.api.app.dtos.AgendamentoResponseDTO;
import com.api.app.models.AgendamentoModel;
import com.api.app.models.MoradorModel;
import com.api.app.models.ProprietarioModel;
import com.api.app.models.UsuarioModel;
import com.api.app.repositories.AgendamentoRepository;
import com.api.app.repositories.MoradorRepository;
import com.api.app.repositories.ProprietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository repository;

    @Autowired
    private MoradorRepository moradorRepository;

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    public AgendamentoModel salvar(AgendamentoModel agendamento, UsuarioModel usuario) {
        if (usuario.getTipoAcesso().isProprietario()) {
            ProprietarioModel prop = proprietarioRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));
            agendamento.setProprietario(prop);
        } else if (usuario.getTipoAcesso().isMorador()) {
            MoradorModel morador = moradorRepository.findByUsuarioAndAtivoTrue(usuario)
                    .orElseThrow(() -> new RuntimeException("Morador não encontrado"));
            agendamento.setMorador(morador);
        } else {
            throw new RuntimeException("Usuário não autorizado");
        }

        agendamento.setTipoAcesso(usuario.getTipoAcesso());
        return repository.save(agendamento);
    }

    public List<AgendamentoModel> listarTodos() {
        return repository.findAll();
    }

    public Optional<AgendamentoModel> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletar(Long id, UsuarioModel usuario) {
        AgendamentoModel agendamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (usuario.getTipoAcesso().isProprietario()) {
            // Pode deletar qualquer
            // Nenhuma verificação adicional
        } else if (usuario.getTipoAcesso().isMorador()) {
            if (agendamento.getMorador() == null ||
                    !agendamento.getMorador().getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Morador só pode deletar seus próprios agendamentos.");
            }
        } else {
            throw new RuntimeException("Usuário não autorizado");
        }

        repository.deleteById(id);
    }

    public AgendamentoModel atualizar(Long id, AgendamentoDTO dto, UsuarioModel usuario) {
        AgendamentoModel existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento com ID " + id + " não encontrado."));

        if (usuario.getTipoAcesso().isProprietario()) {
            // Pode editar qualquer
        } else if (usuario.getTipoAcesso().isMorador()) {
            if (existente.getMorador() == null ||
                    !existente.getMorador().getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("Morador não autorizado a editar este agendamento.");
            }
        }

        existente.setDescricao(dto.getDescricao());
        existente.setTipoAgendamento(dto.getTipoAgendamento());
        existente.setLocal(dto.getLocal());
        existente.setDataInicio(dto.getDataInicio());
        existente.setDataFim(dto.getDataFim());
        existente.setStatus(dto.getStatus());
        existente.setObservacao(dto.getObservacao());

        return repository.save(existente);
    }

    public AgendamentoModel fromDto(AgendamentoDTO dto) {
        AgendamentoModel ag = new AgendamentoModel();
        ag.setId(dto.getId());
        ag.setDescricao(dto.getDescricao());
        ag.setTipoAgendamento(dto.getTipoAgendamento());
        ag.setLocal(dto.getLocal());
        ag.setDataInicio(dto.getDataInicio());
        ag.setDataFim(dto.getDataFim());
        ag.setStatus(dto.getStatus());
        ag.setObservacao(dto.getObservacao());
        return ag;
    }

    public AgendamentoResponseDTO toResponseDto(AgendamentoModel ag) {
        AgendamentoResponseDTO response = new AgendamentoResponseDTO();
        response.setId(ag.getId());
        response.setDescricao(ag.getDescricao());
        response.setTipoAgendamento(ag.getTipoAgendamento());
        response.setTipoUsuario(ag.getTipoAcesso());
        response.setLocal(ag.getLocal());
        response.setDataInicio(ag.getDataInicio());
        response.setDataFim(ag.getDataFim());
        response.setStatus(ag.getStatus());
        response.setObservacao(ag.getObservacao());

        if (ag.getProprietario() != null) {
            response.setNomeProprietario(ag.getProprietario().getNome());
        }

        if (ag.getMorador() != null) {
            response.setNomeMorador(ag.getMorador().getNome());
        }

        return response;
    }
}
