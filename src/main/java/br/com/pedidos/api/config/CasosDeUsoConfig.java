package br.com.pedidos.api.config;

import br.com.pedidos.api.application.CriarPedido;
import br.com.pedidos.api.application.CriarPedidoService;
import br.com.pedidos.api.application.Pedidos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CasosDeUsoConfig {

    @Bean
    CriarPedido criarPedido(Pedidos pedidos) {
        return new CriarPedidoService(pedidos);
    }
}
