export const logsAuditoriaMock = [
  {
    id: 55,
    usuario: { id: 2, nomeCompleto: "Admin Geral", email: "admin@escola.edu.br" },
    acao: "APROVACAO_AGENDAMENTO",
    entidadeAfetada: "Agendamento#10",
    detalhes: "Aprovado o agendamento de 2026-09-26.",
    dataHora: "2026-09-19T12:00:00Z",
  },
  {
    id: 57,
    usuario: { id: 2, nomeCompleto: "Admin Geral", email: "admin@escola.edu.br" },
    acao: "REJEICAO_AGENDAMENTO",
    entidadeAfetada: "Agendamento#12",
    detalhes: "Rejeitado o agendamento de 2026-10-10.",
    dataHora: "2026-09-21T16:30:00Z",
  },
  {
    id: 56,
    usuario: { id: 1, nomeCompleto: "Maria Silva", email: "maria@escola.edu.br" },
    acao: "CRIACAO_AGENDAMENTO",
    entidadeAfetada: "Agendamento#11",
    detalhes: "Solicitado o agendamento de 2026-10-03.",
    dataHora: "2026-09-20T08:45:00Z",
  },
];

// GET /api/admin/logs
export function logsAuditoriaFake() {
  return [...logsAuditoriaMock];
}
