package br.com.backend.cervejaria.controller;

import br.com.backend.cervejaria.dto.CervejaDTO;
import br.com.backend.cervejaria.dto.QuantidadeDTO;
import br.com.backend.cervejaria.exception.CervejaEstoqueExcedidoException;
import br.com.backend.cervejaria.exception.CervejaJaCadastradaException;
import br.com.backend.cervejaria.exception.CervejaNaoEncontradaException;
import br.com.backend.cervejaria.service.CervejaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cervejas")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaController implements CervejaControllerDocs {

    private final CervejaService cervejaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CervejaDTO criarCerveja(@RequestBody @Valid CervejaDTO cervejaDTO) throws CervejaJaCadastradaException {
        return cervejaService.criarCerveja(cervejaDTO);
    }

    @GetMapping("/{nome}")
    public CervejaDTO encontrarPorNome(@PathVariable String nome) throws CervejaNaoEncontradaException {
        return cervejaService.encontrarPorNome(nome);
    }

    @GetMapping
    public List<CervejaDTO> listarCervejas() {
        return cervejaService.listarTudo();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarPorId(@PathVariable Long id) throws CervejaNaoEncontradaException {
        cervejaService.deletarPorId(id);
    }

    @PatchMapping("/{id}/incremento")
    public CervejaDTO incremento(@PathVariable Long id, @RequestBody @Valid QuantidadeDTO quantidadeDTO) throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        return cervejaService.incrementar(id, quantidadeDTO.getQuantidade());
    }

    @PatchMapping("/{id}/decremento")
    public CervejaDTO decremento(@PathVariable Long id, @RequestBody @Valid QuantidadeDTO quantidadeDTO) throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        return cervejaService.decrementar(id, quantidadeDTO.getQuantidade());
    }

}
