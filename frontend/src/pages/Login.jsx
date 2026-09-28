import { useState } from "react";
import { useAuth } from "../hooks/useAuth";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    await login(email, senha);
    navigate("/agendamentos");
  }

  return (
    <form onSubmit={handleSubmit}>
      <h1>Login</h1>
      <input placeholder="E-mail" value={email} onChange={(e) => setEmail(e.target.value)} />
      <input type="password" placeholder="Senha" value={senha} onChange={(e) => setSenha(e.target.value)} />
      <button type="submit">Entrar</button>
    </form>
  );
}
