package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.dto.AgendamentoResponseDTO;
import br.edu.escola.agendamento.dto.ApiResponse;
import br.edu.escola.agendamento.dto.LogAuditoriaResponseDTO;
import br.edu.escola.agendamento.dto.UsuarioResumoDTO;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.LogAuditoria;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.repository.LogAuditoriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AgendamentoRepository agendamentoRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;

    public AdminController(
            AgendamentoRepository agendamentoRepository,
            LogAuditoriaRepository logAuditoriaRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
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