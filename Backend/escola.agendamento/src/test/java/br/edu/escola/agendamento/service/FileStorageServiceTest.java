package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.config.FileStorageConfig;
import br.edu.escola.agendamento.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageServiceTest {

    @TempDir
    Path diretorioTemporario;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {

        FileStorageConfig config =
                new FileStorageConfig();

        config.setUploadDir(
                diretorioTemporario.toString()
        );

        config.setMaxFileSize(
                DataSize.ofMegabytes(5)
        );

        fileStorageService =
                new FileStorageService(config);
    }

    @Test
    void deveArmazenarPdfValido() {

        MockMultipartFile arquivo =
                new MockMultipartFile(
                        "arquivo",
                        "planta.pdf",
                        "application/pdf",
                        "conteudo pdf".getBytes()
                );

        fileStorageService.validarArquivo(arquivo);

        String anexoUrl =
                fileStorageService.armazenarArquivo(
                        arquivo,
                        10L
                );

        Path arquivoSalvo =
                diretorioTemporario
                        .resolve("10")
                        .resolve("planta.pdf");

        assertTrue(Files.exists(arquivoSalvo));

        assertEquals(
                "/arquivos/agendamentos/10/planta.pdf",
                anexoUrl
        );
    }

    @Test
    void deveRejeitarArquivoDocx() {

        MockMultipartFile arquivo =
                new MockMultipartFile(
                        "arquivo",
                        "documento.docx",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "conteudo".getBytes()
                );

        RegraNegocioException exception =
                assertThrows(
                        RegraNegocioException.class,
                        () -> fileStorageService.validarArquivo(arquivo)
                );

        assertEquals(
                "ARQUIVO_TIPO_INVALIDO",
                exception.getCodigoErro()
        );
    }

    @Test
    void deveRejeitarArquivoAcimaDoTamanhoMaximo() {

        FileStorageConfig config =
                new FileStorageConfig();

        config.setUploadDir(
                diretorioTemporario.toString()
        );

        config.setMaxFileSize(
                DataSize.ofBytes(10)
        );

        FileStorageService service =
                new FileStorageService(config);

        MockMultipartFile arquivo =
                new MockMultipartFile(
                        "arquivo",
                        "grande.pdf",
                        "application/pdf",
                        new byte[11]
                );

        RegraNegocioException exception =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.validarArquivo(arquivo)
                );

        assertEquals(
                "ARQUIVO_TAMANHO_INVALIDO",
                exception.getCodigoErro()
        );
    }
}