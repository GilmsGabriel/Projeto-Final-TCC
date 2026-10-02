import { useEffect, useState } from "react";
import api from "../api/api";
import "./CalendarioDisponibilidade.css";

const DIAS_SEMANA = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];

export default function CalendarioDisponibilidade({ mes, ano }) {
  const [registros, setRegistros] = useState([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let cancelado = false;

    async function carregarCalendario() {
      setCarregando(true);
      const { data } = await api.get("/api/agendamentos/calendario", {
        params: { mes, ano },
      });
      if (!cancelado) {
        setRegistros(data);
        setCarregando(false);
      }
    }

    carregarCalendario();

    return () => {
      cancelado = true;
    };
  }, [mes, ano]);

  function getStatusDoDia(diaFormatado) {
    const registro = registros.find((r) => r.data === diaFormatado);
    return registro ? registro.status : null; // null = livre
  }

  function formatarData(dia) {
    const mesStr = String(mes).padStart(2, "0");
    const diaStr = String(dia).padStart(2, "0");
    return `${ano}-${mesStr}-${diaStr}`;
  }

  const diasNoMes = new Date(ano, mes, 0).getDate();
  const dias = Array.from({ length: diasNoMes }, (_, i) => i + 1);

  if (carregando) return <p>Carregando calendário...</p>;

  return (
    <div className="calendario">
      <div className="calendario-cabecalho">
        {DIAS_SEMANA.map((d) => (
          <span key={d}>{d}</span>
        ))}
      </div>

      <div className="calendario-grade">
        {Array.from({ length: new Date(ano, mes - 1, 1).getDay() }).map((_, i) => (
          <div key={`vazio-${i}`} className="dia dia-vazio" />
        ))}

        {dias.map((dia) => {
          const dataFormatada = formatarData(dia);
          const diaSemana = new Date(ano, mes - 1, dia).getDay();
          const ehFimDeSemana = diaSemana === 0 || diaSemana === 6;
          const status = getStatusDoDia(dataFormatada);

          let classe = "dia";
          if (!ehFimDeSemana) {
            classe += " dia-desabilitado";
          } else if (status === "APROVADO") {
            classe += " dia-aprovado";
          } else if (status === "PENDENTE") {
            classe += " dia-pendente";
          } else {
            classe += " dia-livre";
          }

          return (
            <button
              key={dataFormatada}
              className={classe}
              disabled={!ehFimDeSemana || status === "APROVADO" || status === "PENDENTE"}
              title={status ? `${dataFormatada} - ${status}` : dataFormatada}
            >
              {dia}
            </button>
          );
        })}
      </div>
    </div>
  );
}
