package br.com.pedidos.api.adapter.entrada.rest;

import br.com.pedidos.api.application.PedidoSemItensException;
import br.com.pedidos.api.application.PedidoNaoEncontradoException;
import br.com.pedidos.api.domain.ItemInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PedidoExceptionHandler {

    @ExceptionHandler({ItemInvalidoException.class, PedidoSemItensException.class})
    public ResponseEntity<ErroResponse> handleDomainError(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErroResponse(exception.getMessage()));
    }

    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNotFound(PedidoNaoEncontradoException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(exception.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> handleClosedOrder(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(exception.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErroResponse> handleBadRequest(Exception exception) {
        return ResponseEntity.badRequest().body(new ErroResponse("requisição inválida"));
    }

    public record ErroResponse(String mensagem) {
    }
}
