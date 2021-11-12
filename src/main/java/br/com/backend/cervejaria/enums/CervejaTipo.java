package br.com.backend.cervejaria.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CervejaTipo {

    PILSEN("Pilsen"),
    BOCK("Bock"),
    WITBIER("Witbier"),
    WEISSBIER("Weissbier"),
    IPA("IPA"),
    STOUT("Stout");

    private final String descricao;

}
