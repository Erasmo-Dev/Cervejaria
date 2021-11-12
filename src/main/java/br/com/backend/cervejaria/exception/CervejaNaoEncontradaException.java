package br.com.backend.cervejaria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CervejaNaoEncontradaException extends Exception {

    public CervejaNaoEncontradaException(String cervejaNome) {
        super(String.format("Cerveja com o nome %s não encontrada no sistema.", cervejaNome));
    }

    public CervejaNaoEncontradaException(Long id) {
        super(String.format("Cerveja com o id %s não encontrada no sistema.", id));
    }
}
