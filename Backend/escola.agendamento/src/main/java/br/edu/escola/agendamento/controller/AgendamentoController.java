
package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.dto.AgendamentoResponseDTO;
import br.edu.escola.agendamento.dto.ApiResponse;
import br.edu.escola.agendamento.dto.DataOcupadaDTO;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.enums.PerfilUsuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.exception.AcessoNegadoException;
import br.edu.escola.agendamento.exception.RecursoNaoEncontradoException;
import br.edu.escola.agendamento.exception.RegraNegocioException;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.service.AgendamentoService;
import br.edu.escola.agendamento.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoRepository agendamentoRepository;
    private final AgendamentoService agendamentoService;
    private final UsuarioService usuarioService;

    public AgendamentoController(
            AgendamentoRepository agendamentoRepository,
            AgendamentoService agendamentoService,
            UsuarioService usuarioService
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.agendamentoService = agendamentoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/calendario")
    public ResponseEntity<ApiResponse<List<DataOcupadaDTO>>> listarCalendarioPublico(
            @RequestParam int mes,
            @RequestParam int ano
    ) {
        if (mes < 1 || mes > 12) {
            throw new RegraNegocioException(
                    "Mês inválido.",
                    "MES_INVALIDO",
                    "O mês deve estar entre 1 e 12."
            );
        }

        YearMonth anoMes = YearMonth.of(ano, mes);

        LocalDate inicio = anoMes.atDay(1);
        LocalDate fim = anoMes.atEndOfMonth();

        List<DataOcupadaDTO> datasOcupadas =
                agendamentoRepository
                        .findByDataEventoBetweenAndStatusNot(
                                inicio,
                                fim,
                                StatusAgendamento.REJEITADO
                        )
                        .stream()
                        .map(agendamento -> new DataOcupadaDTO(
                                agendamento.getDataEvento(),
                                agendamento.getStatus()
                        ))
                        .toList();

        ApiResponse<List<DataOcupadaDTO>> response = ApiResponse.ok(
                datasOcupadas,
                "Calendário consultado com sucesso."
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/{id}/anexo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AgendamentoResponseDTO>> anexarArquivo(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication auth
    ) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Agendamento não encontrado.",
                        "AGENDAMENTO_NAO_ENCONTRADO"
                ));

        UsuarioResponseDTO usuarioLogado =
                usuarioService.buscarPerfilLogado(auth.getName());

        boolean administrador =
                usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR;

        boolean donoDoAgendamento =
                agendamento.getUsuario() != null
                        && Objects.equals(
                        agendamento.getUsuario().getId(),
                        usuarioLogado.getId()
                );

        if (!administrador && !donoDoAgendamento) {
            throw new AcessoNegadoException(
                    "Você não tem permissão para anexar arquivos neste agendamento.",
                    "ACESSO_NEGADO"
            );
        }

        AgendamentoResponseDTO resultado =
                agendamentoService.anexarArquivo(id, arquivo);

        ApiResponse<AgendamentoResponseDTO> response = ApiResponse.ok(
                resultado,
                "Arquivo anexado com sucesso."
        );

        return ResponseEntity.ok(response);
    }
}
