package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class AgendamentoService {

    private void validarDiaPermitido(LocalDate data) {
        DayOfWeek diaSemana = data.getDayOfWeek();

        if (diaSemana != DayOfWeek.SATURDAY
                && diaSemana != DayOfWeek.SUNDAY) {

            throw new RegraNegocioException(
                    "Não é possível agendar para este dia da semana.",
                    "REGRA_NEGOCIO_DIA_INVALIDO",
                    "Agendamentos são permitidos apenas aos sábados e domingos."
            );
        }
    }
}
