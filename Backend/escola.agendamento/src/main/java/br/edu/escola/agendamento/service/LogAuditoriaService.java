package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.entity.LogAuditoria;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.repository.LogAuditoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class LogAuditoriaService {

    private final LogAuditoriaRepository logAuditoriaRepository;

    public LogAuditoriaService(LogAuditoriaRepository logAuditoriaRepository) {
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    public void registrarEvento(
            Usuario usuario,
            String acao,
            String entidadeAfetada,
            String detalhes) {

        LogAuditoria logAuditoria = new LogAuditoria();

        logAuditoria.setUsuario(usuario);
        logAuditoria.setAcao(acao);
        logAuditoria.setEntidadeAfetada(entidadeAfetada);
        logAuditoria.setDetalhes(detalhes);

        logAuditoriaRepository.save(logAuditoria);
    }
}
