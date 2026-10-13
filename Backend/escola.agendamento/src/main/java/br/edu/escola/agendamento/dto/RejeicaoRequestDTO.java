package br.edu.escola.agendamento.dto;

import jakarta.validation.constraints.NotBlank;

public class RejeicaoRequestDTO {

    @NotBlank(message = "A justificativa da rejeição é obrigatória.")
    private String justificativa;

    public RejeicaoRequestDTO() {
    }

    public RejeicaoRequestDTO(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
