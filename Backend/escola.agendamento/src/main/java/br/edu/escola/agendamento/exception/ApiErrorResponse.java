package br.edu.escola.agendamento.exception;

import java.time.Instant;
import java.util.List;

public class ApiErrorResponse {

    private boolean sucesso;
    private String mensagem;
    private String codigoErro;
    private List<String> detalhes;
    private Instant timestamp;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(
            boolean sucesso,
            String mensagem,
            String codigoErro,
            List<String> detalhes,
            Instant timestamp
    ) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.codigoErro = codigoErro;
        this.detalhes = detalhes;
        this.timestamp = timestamp;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCodigoErro() {
        return codigoErro;
    }

    public void setCodigoErro(String codigoErro) {
        this.codigoErro = codigoErro;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<String> detalhes) {
        this.detalhes = detalhes;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
