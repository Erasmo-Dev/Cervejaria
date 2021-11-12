package br.com.backend.cervejaria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CervejaEstoqueExcedidoException extends Exception {

    public CervejaEstoqueExcedidoException(Long id, int quantidadeIncrementar) {
        super(String.format("Cervejas com %S ID para incremento informado excede a capacidade máxima de estoque: %s", id, quantidadeIncrementar));
    }
}
