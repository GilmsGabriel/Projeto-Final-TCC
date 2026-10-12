package br.edu.escola.agendamento.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean sucesso;
    private String mensagem;
    private T dados;
    private Instant timestamp;

    public static <T> ApiResponse<T> ok(T dados, String mensagem) {
        return new ApiResponse<>(true, mensagem, dados, Instant.now());
    }
}