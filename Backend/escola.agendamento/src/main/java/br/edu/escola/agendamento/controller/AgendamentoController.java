
package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.dto.ApiResponse;
import br.edu.escola.agendamento.dto.DataOcupadaDTO;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.exception.RegraNegocioException;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoController(
            AgendamentoRepository agendamentoRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
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
}
