package br.com.pedidos.api.adapter.entrada.rest;

import br.com.pedidos.api.application.CriarPedido;
import br.com.pedidos.api.domain.ItemPedido;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CriarPedido criarPedido;

    public PedidoController(CriarPedido criarPedido) {
        this.criarPedido = criarPedido;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        var itens = request.itens().stream()
                .map(item -> new ItemPedido(item.sku(), item.quantidade(), item.precoUnitario()))
                .toList();
        var pedido = criarPedido.criar(request.clienteId(), itens);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.from(pedido));
    }
}
