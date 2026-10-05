package br.edu.escola.agendamento.dto;

public class RejeicaoRequestDTO {

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
