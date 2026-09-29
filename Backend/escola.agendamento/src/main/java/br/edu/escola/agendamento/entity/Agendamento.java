package br.edu.escola.agendamento.entity;

import br.edu.escola.agendamento.enums.StatusAgendamento;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate dataEvento;

    @Column(columnDefinition = "TEXT")
    private String descricaoEvento;

    private String anexoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status = StatusAgendamento.PENDENTE;

    @Column(columnDefinition = "TEXT")
    private String justificativaRejeicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliado_por_id")
    private Usuario avaliadoPor;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    public Agendamento(Long id, Usuario usuario, LocalDate dataEvento, String descricaoEvento, String anexoUrl,
                       StatusAgendamento status, String justificativaRejeicao, Usuario avaliadoPor, LocalDateTime dataCriacao,
                       LocalDateTime dataAtualizacao) {
        this.id = id;
        this.usuario = usuario;
        this.dataEvento = dataEvento;
        this.descricaoEvento = descricaoEvento;
        this.anexoUrl = anexoUrl;
        this.status = status;
        this.justificativaRejeicao = justificativaRejeicao;
        this.avaliadoPor = avaliadoPor;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    //Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public String getAnexoUrl() {
        return anexoUrl;
    }

    public void setAnexoUrl(String anexoUrl) {
        this.anexoUrl = anexoUrl;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public String getJustificativaRejeicao() {
        return justificativaRejeicao;
    }

    public void setJustificativaRejeicao(String justificativaRejeicao) {
        this.justificativaRejeicao = justificativaRejeicao;
    }

    public Usuario getAvaliadoPor() {
        return avaliadoPor;
    }

    public void setAvaliadoPor(Usuario avaliadoPor) {
        this.avaliadoPor = avaliadoPor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
