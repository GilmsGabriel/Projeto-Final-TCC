package br.edu.escola.agendamento.exception;

public class AcessoNegadoException extends RuntimeException {

    private final String codigoErro;

    public AcessoNegadoException(String mensagem, String codigoErro) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }

    public String getCodigoErro() {
        return codigoErro;
    }
}
