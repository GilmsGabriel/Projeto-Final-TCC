import { useEffect, useState } from "react";
import api from "../api/api";
import CardSolicitacaoPendente from "../components/CardSolicitacaoPendente";
import "./DashboardAdminPage.css";

export default function DashboardAdminPage() {
  const [solicitacoes, setSolicitacoes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    let cancelado = false;

    async function carregarPendentes() {
      setCarregando(true);
      try {
        const { data } = await api.get("/api/admin/agendamentos/pendentes");
        if (!cancelado) setSolicitacoes(data);
      } catch {
        if (!cancelado) setErro("Não foi possível carregar as solicitações.");
      } finally {
        if (!cancelado) setCarregando(false);
      }
    }

    carregarPendentes();

    return () => {
      cancelado = true;
    };
  }, []);

  async function handleAprovar(id) {
    await api.put(`/api/admin/agendamentos/${id}/aprovar`);
    setSolicitacoes((atual) => atual.filter((s) => s.id !== id));
  }

  async function handleRejeitar(id, justificativa) {
    await api.put(`/api/admin/agendamentos/${id}/rejeitar`, { justificativa });
    setSolicitacoes((atual) => atual.filter((s) => s.id !== id));
  }

  if (carregando) return <p>Carregando solicitações...</p>;
  if (erro) return <p>{erro}</p>;

  return (
    <div className="dashboard-admin">
      <h1>Solicitações pendentes</h1>

      {solicitacoes.length === 0 ? (
        <p>Nenhuma solicitação pendente no momento.</p>
      ) : (
        <div className="lista-solicitacoes">
          {solicitacoes.map((solicitacao) => (
            <CardSolicitacaoPendente
              key={solicitacao.id}
              agendamento={solicitacao}
              onAprovar={handleAprovar}
              onRejeitar={handleRejeitar}
            />
          ))}
        </div>
      )}
    </div>
  );
}
