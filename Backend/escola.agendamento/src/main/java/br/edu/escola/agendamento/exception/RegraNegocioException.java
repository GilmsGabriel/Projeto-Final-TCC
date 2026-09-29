package br.edu.escola.agendamento.exception;

public class RegraNegocioException extends RuntimeException {

    private final String codigoErro;
    private final String detalhe;

    public RegraNegocioException(String mensagem, String codigoErro, String detalhe) {
        super(mensagem);
        this.codigoErro = codigoErro;
        this.detalhe = detalhe;
    }

    public String getCodigoErro() {
        return codigoErro;
    }

    public String getDetalhe() {
        return detalhe;
    }
}
