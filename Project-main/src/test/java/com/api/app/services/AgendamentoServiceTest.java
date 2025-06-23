package com.api.app.services;

import com.api.app.dtos.AgendamentoDTO;
import com.api.app.models.AgendamentoModel;
import com.api.app.models.UsuarioModel;
import com.api.app.models.enums.StatusAgendamento;
import com.api.app.models.enums.TipoAcesso;
import com.api.app.repositories.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository repository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private UsuarioModel usuario;

    @BeforeEach
    public void setup() {
        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setTipoAcesso(TipoAcesso.PROPRIETARIO);
    }

    @Test
    public void deveCriarAgendamentoComSucesso() {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setDescricao("Teste");
        dto.setTipoAgendamento(null);  // pode ajustar se tiver enum
        dto.setLocal("Sala");
        dto.setDataInicio(LocalDateTime.now());
        dto.setDataFim(LocalDateTime.now().plusHours(1));
        dto.setStatus(StatusAgendamento.AGENDADO);
        dto.setObservacao("Obs");

        AgendamentoModel model = agendamentoService.fromDto(dto);

        assertEquals(dto.getDescricao(), model.getDescricao());
        assertEquals(dto.getLocal(), model.getLocal());
        assertEquals(dto.getStatus(), model.getStatus());
    }

    @Test
    public void deveGerarErroAoBuscarAgendamentoInexistente() {
        Long idInexistente = 999L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<AgendamentoModel> resultado = agendamentoService.buscarPorId(idInexistente);

        assertTrue(resultado.isEmpty(), "Deveria retornar Optional.empty() quando não encontrado");
    }
}
