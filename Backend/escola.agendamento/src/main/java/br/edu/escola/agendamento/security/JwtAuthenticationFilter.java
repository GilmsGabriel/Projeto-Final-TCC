package br.edu.escola.agendamento.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    // Injeção de dependências via construtor (recomendado pelo Spring)
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Extrair o token da requisição
            String token = obterTokenDaRequisicao(request);

            // 2. Validar o token
            if (StringUtils.hasText(token) && jwtTokenProvider.validarToken(token)) {

                // 3. Extrair o e-mail (username) do token
                String email = jwtTokenProvider.extrairEmail(token);

                // 4. Carregar os detalhes do usuário do banco de dados
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // 5. Criar o objeto de autenticação
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credenciais (senha) não são necessárias aqui, pois o token já foi validado
                        userDetails.getAuthorities()
                );

                // 6. Adicionar os detalhes da requisição (como IP, sessão) à autenticação
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Salvar a autenticação no contexto de segurança do Spring
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Em produção, utilize um logger (ex: log.error(...))
            System.out.println("Não foi possível definir a autenticação do usuário: " + ex.getMessage());
        }

        // 8. Passar a requisição adiante na cadeia de filtros
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extrair o token do cabeçalho Authorization
    private String obterTokenDaRequisicao(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Verifica se o cabeçalho contém texto e começa com "Bearer"
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove a palavra "Bearer " e o espaço (7 caracteres)
        }

        return null;
    }
}