package com.api.app.controllers;

import com.api.app.dtos.AgendamentoDTO;
import com.api.app.dtos.AgendamentoResponseDTO;
import com.api.app.models.AgendamentoModel;
import com.api.app.models.UsuarioModel;
import com.api.app.repositories.UsuarioRepository;
import com.api.app.security.JwtUtil;
import com.api.app.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioModel getUsuarioLogado(String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @PostMapping("/cadastrar")
    public AgendamentoResponseDTO cadastrar(@RequestBody AgendamentoDTO dto,
                                            @RequestHeader("Authorization") String token) {
        UsuarioModel usuario = getUsuarioLogado(token);
        AgendamentoModel novo = service.salvar(service.fromDto(dto), usuario);
        return service.toResponseDto(novo);
    }

    @GetMapping("/listar")
    public List<AgendamentoResponseDTO> listar(@RequestHeader("Authorization") String token) {
        UsuarioModel usuario = getUsuarioLogado(token);
        return service.listarTodos() // todos, pois morador agora pode ver os do proprietário
                .stream()
                .map(service::toResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> buscarPorId(@PathVariable Long id,
                                                              @RequestHeader("Authorization") String token) {
        UsuarioModel usuario = getUsuarioLogado(token);

        return service.buscarPorId(id)
                .filter(ag -> {
                    if (usuario.getTipoAcesso().isProprietario()) {
                        return true; // pode ver todos
                    } else if (usuario.getTipoAcesso().isMorador()) {
                        return ag.getMorador() != null &&
                                ag.getMorador().getUsuario().getId().equals(usuario.getId());
                    }
                    return false;
                })
                .map(service::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<AgendamentoResponseDTO> alterar(@PathVariable Long id,
                                                          @RequestBody AgendamentoDTO dto,
                                                          @RequestHeader("Authorization") String token) {
        UsuarioModel usuario = getUsuarioLogado(token);
        dto.setId(id);
        AgendamentoModel atualizado = service.atualizar(id, dto, usuario);
        return ResponseEntity.ok(service.toResponseDto(atualizado));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable Long id,
                                                       @RequestHeader("Authorization") String token) {
        UsuarioModel usuario = getUsuarioLogado(token);
        service.deletar(id, usuario);
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Agendamento deletado com sucesso!");
        return ResponseEntity.ok(resposta);
    }
}
