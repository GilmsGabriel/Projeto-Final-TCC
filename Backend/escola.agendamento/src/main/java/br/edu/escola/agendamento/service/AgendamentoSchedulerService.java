package br.edu.escola.agendamento.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AgendamentoSchedulerService {

    private final AgendamentoService agendamentoService;

    public AgendamentoSchedulerService(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void executarExpiracaoDiaria() {
        agendamentoService.expirarAgendamentosVencidos();
    }
}
