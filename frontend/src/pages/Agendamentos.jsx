import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import CalendarioDisponibilidade from "../components/CalendarioDisponibilidade";

export default function Agendamentos() {
  const { usuario, logout } = useAuth();
  return (
    <div style={{ maxWidth: 420, margin: "0 auto", textAlign: "center" }}>
      <h1>Agendamentos</h1>
      <p>Olá, {usuario?.nomeCompleto}</p>
      <p><Link to="/agendamentos/meus">Meus agendamentos</Link></p>
      <p>Calendário</p>
      <CalendarioDisponibilidade mes={9} ano={2026} />
      <button onClick={logout}>Sair</button>
    </div>
  );
}
