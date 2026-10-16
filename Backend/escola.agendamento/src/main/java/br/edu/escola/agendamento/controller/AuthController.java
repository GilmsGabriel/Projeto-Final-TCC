
package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.dto.ApiResponse;
import br.edu.escola.agendamento.dto.RegistroRequestDTO;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.escola.agendamento.dto.LoginRequestDTO;
import br.edu.escola.agendamento.dto.TokenResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> registrar(
            @RequestBody @Valid RegistroRequestDTO dto
    ) {
        UsuarioResponseDTO usuario = authService.registrar(dto);

        ApiResponse<UsuarioResponseDTO> response = ApiResponse.ok(
                usuario,
                "Usuário registrado com sucesso."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> login(@RequestBody @Valid LoginRequestDTO dto) {
        TokenResponseDTO token = authService.autenticar(dto);

        return ResponseEntity.ok(ApiResponse.ok(token, "Login realizado com sucesso."));
    }


}
