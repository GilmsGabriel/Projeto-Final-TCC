import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function Agendamentos() {
  const { usuario, logout } = useAuth();
  return (
    <div>
      <h1>Agendamentos</h1>
      <p>Olá, {usuario?.nomeCompleto}</p>
      <p><Link to="/agendamentos/meus">Meus agendamentos</Link></p>
      <button onClick={logout}>Sair</button>
    </div>
  );
}
