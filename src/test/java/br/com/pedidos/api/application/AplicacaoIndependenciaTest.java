package br.com.pedidos.api.application;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

class AplicacaoIndependenciaTest {

    @Test
    void aplicacaoNaoImportaFrameworksOuAdapters() throws Exception {
        var diretorio = Path.of("src/main/java/br/com/pedidos/api/application");

        try (Stream<Path> arquivos = Files.list(diretorio)) {
            arquivos.filter(arquivo -> arquivo.toString().endsWith(".java"))
                    .forEach(arquivo -> {
                        try {
                            var fonte = Files.readString(arquivo);
                            assertFalse(fonte.contains("org.springframework"), arquivo.toString());
                            assertFalse(fonte.contains("jakarta.persistence"), arquivo.toString());
                            assertFalse(fonte.contains("javax.persistence"), arquivo.toString());
                            assertFalse(fonte.contains("org.springframework.web"), arquivo.toString());
                            assertFalse(fonte.contains("adapter"), arquivo.toString());
                        } catch (Exception erro) {
                            throw new RuntimeException(erro);
                        }
                    });
        }
    }
}
