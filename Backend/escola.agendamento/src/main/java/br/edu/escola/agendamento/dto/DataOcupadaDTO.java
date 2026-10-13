
package br.edu.escola.agendamento.dto;

import br.edu.escola.agendamento.enums.StatusAgendamento;

import java.time.LocalDate;

public class DataOcupadaDTO {

    private LocalDate data;
    private StatusAgendamento status;

    public DataOcupadaDTO() {
    }

    public DataOcupadaDTO(LocalDate data, StatusAgendamento status) {
        this.data = data;
        this.status = status;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }
}
