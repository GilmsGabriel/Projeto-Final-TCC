package br.edu.escola.agendamento.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    private final String codigoErro;

    public RecursoNaoEncontradoException(String mensagem, String codigoErro) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }

    public String getCodigoErro() {
        return codigoErro;
    }
}
