package br.edu.escola.agendamento.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {
    private String token;

    @Builder.Default
    private String tipo = "Bearer";

    @Builder.Default
    private long expiraEm = 3600L;

    private UsuarioResponseDTO usuario;
}