import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import FormularioAgendamento from "../components/FormularioAgendamento";

export default function Agendamentos() {
  const { usuario, logout } = useAuth();
  return (
    <div style={{ maxWidth: 420, margin: "0 auto", textAlign: "center" }}>
      <h1>Agendamentos</h1>
      <p>Olá, {usuario?.nomeCompleto}</p>
      <p><Link to="/agendamentos/meus">Meus agendamentos</Link></p>
      <FormularioAgendamento />
      <button onClick={logout}>Sair</button>
    </div>
  );
}
