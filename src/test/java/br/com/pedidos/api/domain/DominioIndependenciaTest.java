package br.com.pedidos.api.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class DominioIndependenciaTest {

    @Test
    void fontesDoDominioNaoImportamFrameworksOuLombok() throws Exception {
        var diretorio = Path.of("src/main/java/br/com/pedidos/api/domain");

        try (Stream<Path> arquivos = Files.list(diretorio)) {
            arquivos.filter(arquivo -> arquivo.toString().endsWith(".java"))
                    .forEach(arquivo -> {
                        try {
                            var fonte = Files.readString(arquivo);
                            assertFalse(fonte.contains("org.springframework"), arquivo.toString());
                            assertFalse(fonte.contains("jakarta.persistence"), arquivo.toString());
                            assertFalse(fonte.contains("lombok"), arquivo.toString());
                        } catch (Exception erro) {
                            throw new RuntimeException(erro);
                        }
                    });
        }
    }
}
