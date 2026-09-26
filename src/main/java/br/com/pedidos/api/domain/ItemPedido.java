package br.com.pedidos.api.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record ItemPedido(String produto, int quantidade, BigDecimal precoUnitario) {

    public ItemPedido {
        Objects.requireNonNull(produto, "produto não pode ser nulo");
        Objects.requireNonNull(precoUnitario, "preço unitário não pode ser nulo");

        if (produto.isBlank()) {
            throw new ItemInvalidoException("produto não pode ser vazio");
        }
        if (quantidade <= 0) {
            throw new ItemInvalidoException("quantidade deve ser positiva");
        }
        if (precoUnitario.signum() <= 0) {
            throw new ItemInvalidoException("preço unitário deve ser positivo");
        }
    }
}
