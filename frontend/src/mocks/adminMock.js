let pendentesMock = [
  {
    id: 10,
    tituloEvento: "Festa junina da turma 5A",
    dataEvento: "2026-09-26",
    status: "PENDENTE",
    solicitante: { id: 1, nomeCompleto: "Maria Silva" },
  },
  {
    id: 11,
    tituloEvento: "Reunião de pais",
    dataEvento: "2026-10-03",
    status: "PENDENTE",
    solicitante: { id: 2, nomeCompleto: "João Souza" },
  },
];

// GET /api/admin/agendamentos/pendentes
export function pendentesFake() {
  return pendentesMock.filter((a) => a.status === "PENDENTE");
}

// PUT /api/admin/agendamentos/{id}/aprovar
export function aprovarFake(id) {
  const agendamento = pendentesMock.find((a) => a.id === Number(id));
  if (!agendamento) throw new Error("Agendamento não encontrado");
  agendamento.status = "APROVADO";
  return { ...agendamento };
}

// PUT /api/admin/agendamentos/{id}/rejeitar
export function rejeitarFake(id, justificativa) {
  const agendamento = pendentesMock.find((a) => a.id === Number(id));
  if (!agendamento) throw new Error("Agendamento não encontrado");
  if (!justificativa || !justificativa.trim()) {
    throw new Error("Justificativa é obrigatória");
  }
  agendamento.status = "REJEITADO";
  agendamento.justificativa = justificativa;
  return { ...agendamento };
}
