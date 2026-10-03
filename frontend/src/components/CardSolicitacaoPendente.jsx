import { useState } from "react";
import "./CardSolicitacaoPendente.css";

export default function CardSolicitacaoPendente({ agendamento, onAprovar, onRejeitar }) {
  const [modalAberto, setModalAberto] = useState(false);
  const [justificativa, setJustificativa] = useState("");
  const [processando, setProcessando] = useState(false);
  const [erro, setErro] = useState(null);

  const justificativaValida = justificativa.trim().length > 0;

  async function handleAprovar() {
    setProcessando(true);
    setErro(null);
    try {
      await onAprovar(agendamento.id);
    } catch {
      setErro("Não foi possível aprovar. Tente novamente.");
      setProcessando(false);
    }
  }

  function abrirModalRejeitar() {
    setJustificativa("");
    setErro(null);
    setModalAberto(true);
  }

  function fecharModal() {
    if (processando) return; // evita fechar no meio de uma requisição
    setModalAberto(false);
  }

  async function confirmarRejeicao() {
    if (!justificativaValida) return;
    setProcessando(true);
    setErro(null);
    try {
      await onRejeitar(agendamento.id, justificativa.trim());
      setModalAberto(false);
    } catch {
      setErro("Não foi possível rejeitar. Tente novamente.");
      setProcessando(false);
    }
  }

  return (
    <div className="card-solicitacao">
      <h2>{agendamento.tituloEvento}</h2>
      <p>Data do evento: {formatarDataBR(agendamento.dataEvento)}</p>
      {agendamento.solicitante && (
        <p>Solicitado por: {agendamento.solicitante.nomeCompleto}</p>
      )}

      {erro && <p className="card-erro">{erro}</p>}

      <div className="card-acoes">
        <button onClick={handleAprovar} disabled={processando}>
          {processando ? "Processando..." : "Aprovar"}
        </button>
        <button onClick={abrirModalRejeitar} disabled={processando}>
          Rejeitar
        </button>
      </div>

      {modalAberto && (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <div className="modal-conteudo">
            <h3>Rejeitar solicitação</h3>
            <p>Informe a justificativa da rejeição:</p>
            <textarea
              value={justificativa}
              onChange={(e) => setJustificativa(e.target.value)}
              placeholder="Ex: Conflito com manutenção programada do espaço."
              rows={4}
              autoFocus
            />
            {erro && <p className="card-erro">{erro}</p>}
            <div className="modal-acoes">
              <button onClick={fecharModal} disabled={processando}>
                Cancelar
              </button>
              <button
                onClick={confirmarRejeicao}
                disabled={!justificativaValida || processando}
              >
                {processando ? "Enviando..." : "Confirmar rejeição"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function formatarDataBR(dataISO) {
  const [ano, mes, dia] = dataISO.split("-");
  return `${dia}/${mes}/${ano}`;
}
