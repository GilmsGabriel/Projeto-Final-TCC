package br.edu.escola.agendamento.dto;

public class UsuarioResumoDTO {

    private Long id;
    private String nomeCompleto;
    private String email;

    public UsuarioResumoDTO() {
    }

    public UsuarioResumoDTO(Long id, String nomeCompleto, String email) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
