package br.com.pedidos.api.config;

import br.com.pedidos.api.application.CriarPedido;
import br.com.pedidos.api.application.CriarPedidoService;
import br.com.pedidos.api.application.AdicionarItem;
import br.com.pedidos.api.application.AdicionarItemService;
import br.com.pedidos.api.application.Pedidos;
import br.com.pedidos.api.application.ConsultarPedido;
import br.com.pedidos.api.application.ConsultarPedidoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CasosDeUsoConfig {

    @Bean
    CriarPedido criarPedido(Pedidos pedidos) {
        return new CriarPedidoService(pedidos);
    }

    @Bean
    AdicionarItem adicionarItem(Pedidos pedidos) {
        return new AdicionarItemService(pedidos);
    }

    @Bean
    ConsultarPedido consultarPedido(Pedidos pedidos) {
        return new ConsultarPedidoService(pedidos);
    }
}
