package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.config.FileStorageConfig;
import br.edu.escola.agendamento.exception.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

@Service
public class FileStorageService {

    private static final Set<String> EXTENSOES_PERMITIDAS =
            Set.of("pdf", "jpg", "png");

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of(
                    "application/pdf",
                    "image/jpeg",
                    "image/png"
            );

    private final FileStorageConfig fileStorageConfig;

    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.fileStorageConfig = fileStorageConfig;
    }

    public void validarArquivo(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException(
                    "Arquivo inválido.",
                    "ARQUIVO_INVALIDO",
                    "O arquivo não pode ser vazio."
            );
        }

        String nomeArquivo = arquivo.getOriginalFilename();
        String contentType = arquivo.getContentType();

        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new RegraNegocioException(
                    "Arquivo inválido.",
                    "ARQUIVO_INVALIDO",
                    "Não foi possível identificar o nome do arquivo."
            );
        }

        String extensao = obterExtensao(nomeArquivo);

        boolean extensaoValida =
                EXTENSOES_PERMITIDAS.contains(extensao);

        boolean contentTypeValido =
                contentType != null
                        && TIPOS_PERMITIDOS.contains(
                        contentType.toLowerCase(Locale.ROOT)
                );

        if (!extensaoValida || !contentTypeValido) {
            throw new RegraNegocioException(
                    "Tipo de arquivo não permitido.",
                    "ARQUIVO_TIPO_INVALIDO",
                    "Apenas arquivos PDF, JPG ou PNG são permitidos."
            );
        }

        if (arquivo.getSize()
                > fileStorageConfig.getMaxFileSize().toBytes()) {

            throw new RegraNegocioException(
                    "Arquivo excede o tamanho máximo permitido.",
                    "ARQUIVO_TAMANHO_INVALIDO",
                    "O arquivo ultrapassa o limite configurado de "
                            + fileStorageConfig.getMaxFileSize()
                            .toMegabytes()
                            + " MB."
            );
        }
    }

    public String armazenarArquivo(
            MultipartFile arquivo,
            Long agendamentoId
    ) {

        String nomeArquivo =
                sanitizarNomeArquivo(arquivo.getOriginalFilename());

        try {
            Path diretorioBase = Paths
                    .get(fileStorageConfig.getUploadDir())
                    .toAbsolutePath()
                    .normalize();

            Path diretorioAgendamento = diretorioBase
                    .resolve(String.valueOf(agendamentoId))
                    .normalize();

            Files.createDirectories(diretorioAgendamento);

            Path arquivoDestino = diretorioAgendamento
                    .resolve(nomeArquivo)
                    .normalize();

            if (!arquivoDestino.startsWith(diretorioAgendamento)) {
                throw new RegraNegocioException(
                        "Arquivo inválido.",
                        "ARQUIVO_INVALIDO",
                        "O nome do arquivo não é permitido."
                );
            }

            try (InputStream inputStream = arquivo.getInputStream()) {
                Files.copy(
                        inputStream,
                        arquivoDestino,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            return "/arquivos/agendamentos/"
                    + agendamentoId
                    + "/"
                    + nomeArquivo;

        } catch (IOException exception) {
            throw new RegraNegocioException(
                    "Não foi possível armazenar o arquivo.",
                    "ARQUIVO_ERRO_ARMAZENAMENTO",
                    "Ocorreu um erro ao salvar o arquivo."
            );
        }
    }

    private String obterExtensao(String nomeArquivo) {

        int ultimoPonto = nomeArquivo.lastIndexOf('.');

        if (ultimoPonto == -1
                || ultimoPonto == nomeArquivo.length() - 1) {
            return "";
        }

        return nomeArquivo
                .substring(ultimoPonto + 1)
                .toLowerCase(Locale.ROOT);
    }

    private String sanitizarNomeArquivo(String nomeArquivo) {

        String nomeSeguro = Paths
                .get(nomeArquivo)
                .getFileName()
                .toString();

        return nomeSeguro.replaceAll(
                "[^a-zA-Z0-9._-]",
                "_"
        );
    }
}