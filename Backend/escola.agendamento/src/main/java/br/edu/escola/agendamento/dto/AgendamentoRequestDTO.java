package br.edu.escola.agendamento.dto;

import java.time.LocalDate;

public class AgendamentoRequestDTO {

    private LocalDate dataEvento;
    private String descricaoEvento;

    public AgendamentoRequestDTO() {
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(String descricaoEvento) {
        this.descricaoEvento = descricaoEvento;
    }
}
