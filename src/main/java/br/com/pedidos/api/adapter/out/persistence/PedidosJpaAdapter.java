package br.com.pedidos.api.adapter.out.persistence;

import br.com.pedidos.api.application.Pedidos;
import br.com.pedidos.api.domain.Pedido;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PedidosJpaAdapter implements Pedidos {

    private final PedidoJpaRepository repository;
    private final PedidoJpaMapper mapper = new PedidoJpaMapper();

    public PedidosJpaAdapter(PedidoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Pedido salvar(Pedido pedido) {
        var entity = repository.saveAndFlush(mapper.toEntity(pedido));
        return mapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
