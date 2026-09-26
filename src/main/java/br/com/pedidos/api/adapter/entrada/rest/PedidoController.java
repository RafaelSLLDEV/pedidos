package br.com.pedidos.api.adapter.entrada.rest;

import br.com.pedidos.api.application.CriarPedido;
import br.com.pedidos.api.application.AdicionarItem;
import br.com.pedidos.api.application.ConsultarPedido;
import br.com.pedidos.api.domain.ItemPedido;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CriarPedido criarPedido;
    private final AdicionarItem adicionarItem;
    private final ConsultarPedido consultarPedido;

    public PedidoController(CriarPedido criarPedido, AdicionarItem adicionarItem, ConsultarPedido consultarPedido) {
        this.criarPedido = criarPedido;
        this.adicionarItem = adicionarItem;
        this.consultarPedido = consultarPedido;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        var itens = request.itens().stream()
                .map(item -> new ItemPedido(item.sku(), item.quantidade(), item.precoUnitario()))
                .toList();
        var pedido = criarPedido.criar(request.clienteId(), itens);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.from(pedido));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> consultar(@PathVariable String id) {
        var pedido = consultarPedido.porId(java.util.UUID.fromString(id));
        return ResponseEntity.ok(PedidoResponse.from(pedido));
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<PedidoResponse> adicionarItem(
            @PathVariable String id,
            @Valid @RequestBody AdicionarItemRequest request) {
        var pedido = adicionarItem.executar(
                java.util.UUID.fromString(id), request.sku(), request.quantidade(), request.precoUnitario());
        return ResponseEntity.ok(PedidoResponse.from(pedido));
    }
}
