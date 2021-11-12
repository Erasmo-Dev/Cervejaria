package br.com.backend.cervejaria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CervejaJaCadastradaException extends Exception{

    public CervejaJaCadastradaException(String cervejaNome) {
        super(String.format("Cerveja com o nome %s já registrada no sistema.", cervejaNome));
    }
}
