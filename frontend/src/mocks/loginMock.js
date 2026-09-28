export const loginMock = {
  token: "eyJhbGciOiJIUzI1NiJ9.mock.token",
  tipo: "Bearer",
  expiraEm: 3600,
  usuario: {
    id: 1,
    nomeCompleto: "Maria Silva",
    email: "maria@escola.edu.br",
    perfil: "COMUM",
  },
}

export function loginFake(email, senha) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (email && senha) resolve({ data: loginMock });
      else reject(new Error("Credenciais inválidas"));
    }, 300);
  });
}
