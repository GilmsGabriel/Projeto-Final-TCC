package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.dto.AgendamentoResponseDTO;
import br.edu.escola.agendamento.dto.ApiResponse;
import br.edu.escola.agendamento.dto.UsuarioResumoDTO;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
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

    public AdminController(
            AgendamentoRepository agendamentoRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @GetMapping("/agendamentos/pendentes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<AgendamentoResponseDTO>>>
    listarPendentes() {

        List<AgendamentoResponseDTO> agendamentosPendentes =
                agendamentoRepository
                        .findByStatus(StatusAgendamento.PENDENTE)
                        .stream()
                        .map(this::converterParaResponseDTO)
                        .toList();

        ApiResponse<List<AgendamentoResponseDTO>> response =
                ApiResponse.ok(
                        agendamentosPendentes,
                        "Agendamentos pendentes consultados com sucesso."
                );

        return ResponseEntity.ok(response);
    }

    private AgendamentoResponseDTO converterParaResponseDTO(
            Agendamento agendamento
    ) {
        AgendamentoResponseDTO response =
                new AgendamentoResponseDTO();

        response.setId(agendamento.getId());

        Usuario usuario = agendamento.getUsuario();

        response.setUsuario(
                new UsuarioResumoDTO(
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
}