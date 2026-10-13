package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.dto.*;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.LogAuditoria;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.repository.LogAuditoriaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.edu.escola.agendamento.service.AgendamentoService;
import br.edu.escola.agendamento.service.UsuarioService;
import org.springframework.security.core.Authentication;
import br.edu.escola.agendamento.dto.RejeicaoRequestDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AgendamentoRepository agendamentoRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;
    private final AgendamentoService agendamentoService;
    private final UsuarioService usuarioService;

    public AdminController(
            AgendamentoRepository agendamentoRepository,
            LogAuditoriaRepository logAuditoriaRepository,
            AgendamentoService agendamentoService,
            UsuarioService usuarioService) {

        this.agendamentoRepository = agendamentoRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
        this.agendamentoService = agendamentoService;
        this.usuarioService = usuarioService;
    }

    @PutMapping("/agendamentos/{id}/aprovar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AgendamentoResponseDTO>> aprovar(
            @PathVariable Long id,
            Authentication auth) {

        UsuarioResponseDTO administrador =
                usuarioService.buscarPerfilLogado(auth.getName());

        AgendamentoResponseDTO resultado =
                agendamentoService.aprovarAgendamento(
                        id,
                        administrador.getId()
                );

        ApiResponse<AgendamentoResponseDTO> response = ApiResponse.ok(
                resultado,
                "Agendamento aprovado com sucesso."
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/agendamentos/{id}/rejeitar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AgendamentoResponseDTO>> rejeitar(
            @PathVariable Long id,
            @RequestBody @Valid RejeicaoRequestDTO dto,
            Authentication auth) {

        UsuarioResponseDTO administrador =
                usuarioService.buscarPerfilLogado(auth.getName());

        AgendamentoResponseDTO resultado =
                agendamentoService.rejeitarAgendamento(
                        id,
                        administrador.getId(),
                        dto
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        resultado,
                        "Agendamento rejeitado com sucesso."
                )
        );
    }

    @GetMapping("/agendamentos/pendentes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<AgendamentoResponseDTO>>>
    listarPendentes() {

        List<AgendamentoResponseDTO> agendamentosPendentes =
                agendamentoRepository
                        .findByStatus(StatusAgendamento.PENDENTE)
                        .stream()
                        .map(this::converterParaAgendamentoResponseDTO)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        agendamentosPendentes,
                        "Agendamentos pendentes consultados com sucesso."
                )
        );
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<LogAuditoriaResponseDTO>>>
    listarLogs() {

        List<LogAuditoriaResponseDTO> logs =
                logAuditoriaRepository
                        .findAllByOrderByDataHoraDesc()
                        .stream()
                        .map(this::converterParaLogAuditoriaResponseDTO)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        logs,
                        "Logs de auditoria consultados com sucesso."
                )
        );
    }

    private AgendamentoResponseDTO converterParaAgendamentoResponseDTO(
            Agendamento agendamento
    ) {
        AgendamentoResponseDTO response =
                new AgendamentoResponseDTO();

        response.setId(agendamento.getId());

        Usuario usuario = agendamento.getUsuario();

        response.setUsuario(
                usuario == null ? null : new UsuarioResumoDTO(
                        usuario.getId(),
                        usuario.getNomeCompleto(),
                        usuario.getEmail()
                )
        );

        response.setDataEvento(agendamento.getDataEvento());

        if (agendamento.getDataEvento() != null) {
            response.setDiaSemana(
                    agendamento.getDataEvento()
                            .getDayOfWeek()
                            .toString()
            );
        }

        response.setDescricaoEvento(
                agendamento.getDescricaoEvento()
        );

        response.setAnexoUrl(agendamento.getAnexoUrl());
        response.setStatus(agendamento.getStatus());

        response.setJustificativaRejeicao(
                agendamento.getJustificativaRejeicao()
        );

        response.setDataCriacao(agendamento.getDataCriacao());
        response.setDataAtualizacao(agendamento.getDataAtualizacao());

        return response;
    }

    private LogAuditoriaResponseDTO converterParaLogAuditoriaResponseDTO(
            LogAuditoria log
    ) {
        LogAuditoriaResponseDTO response =
                new LogAuditoriaResponseDTO();

        response.setId(log.getId());

        Usuario usuario = log.getUsuario();

        response.setUsuario(
                usuario == null ? null : new UsuarioResumoDTO(
                        usuario.getId(),
                        usuario.getNomeCompleto(),
                        usuario.getEmail()
                )
        );

        response.setAcao(log.getAcao());
        response.setEntidadeAfetada(log.getEntidadeAfetada());
        response.setDetalhes(log.getDetalhes());
        response.setDataHora(log.getDataHora());

        return response;
    }
}