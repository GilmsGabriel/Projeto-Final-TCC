package br.edu.escola.agendamento.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = FileStorageConfig.class)
@EnableConfigurationProperties(FileStorageConfig.class)
@TestPropertySource(properties = "file.upload-dir=./uploads/agendamentos")
class FileStorageConfigTest {

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Test
    void deveInjetarDiretorioDeUpload() {
        assertEquals(
                "./uploads/agendamentos",
                fileStorageConfig.getUploadDir()
        );
    }
}