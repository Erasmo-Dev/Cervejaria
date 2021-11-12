package br.com.backend.cervejaria.builder;

import br.com.backend.cervejaria.dto.CervejaDTO;
import br.com.backend.cervejaria.enums.CervejaTipo;
import lombok.Builder;

@Builder
public class CervejaDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String nome = "Brahma";

    @Builder.Default
    private String marca = "Ambev";

    @Builder.Default
    private int maximo = 50;

    @Builder.Default
    private int quantidade = 10;

    @Builder.Default
    private CervejaTipo tipo = CervejaTipo.BOCK;

    public CervejaDTO toCervejaDTO() {
        return new CervejaDTO(id,
                nome,
                marca,
                maximo,
                quantidade,
                tipo);
    }

}
