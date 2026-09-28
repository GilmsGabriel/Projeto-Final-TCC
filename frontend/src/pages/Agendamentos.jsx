import { useAuth } from "../hooks/useAuth";

export default function Agendamentos() {
  const { usuario, logout } = useAuth();
  return (
    <div>
      <h1>Agendamentos</h1>
      <p>Olá, {usuario?.nomeCompleto}</p>
      <button onClick={logout}>Sair</button>
    </div>
  );
}
