import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import "./LoginPage.css";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  async function handleSubmit(event) {
    event.preventDefault();
    if (loading) return;

    setErro("");
    setLoading(true);
    try {
      await login(email, senha);
      navigate("/agendamentos");
    } catch {
      setErro("E-mail ou senha inválidos. Confira os dados e tente novamente.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page">
      <form className="login-form" onSubmit={handleSubmit} aria-busy={loading}>
        <h1>Entrar</h1>
        {location.state?.mensagemSucesso && (
          <p className="login-success" role="status">{location.state.mensagemSucesso}</p>
        )}
        <label htmlFor="login-email">E-mail</label>
        <input
          id="login-email"
          type="email"
          autoComplete="email"
          required
          value={email}
          onChange={(event) => setEmail(event.target.value)}
        />
        <label htmlFor="login-senha">Senha</label>
        <input
          id="login-senha"
          type="password"
          autoComplete="current-password"
          required
          value={senha}
          onChange={(event) => setSenha(event.target.value)}
        />
        {erro && <p className="login-error" role="alert">{erro}</p>}
        <button type="submit" disabled={loading}>
          {loading ? "Entrando…" : "Entrar"}
        </button>
        <Link to="/registro">Criar conta</Link>
      </form>
    </main>
  );
}
