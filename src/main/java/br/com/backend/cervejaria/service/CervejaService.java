package br.com.backend.cervejaria.service;

import br.com.backend.cervejaria.dto.CervejaDTO;
import br.com.backend.cervejaria.entity.Cerveja;
import br.com.backend.cervejaria.exception.CervejaEstoqueExcedidoException;
import br.com.backend.cervejaria.exception.CervejaJaCadastradaException;
import br.com.backend.cervejaria.exception.CervejaNaoEncontradaException;
import br.com.backend.cervejaria.mapper.CervejaMapper;
import br.com.backend.cervejaria.repository.CervejaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaService {

    private final CervejaRepository cervejaRepository;
    private final CervejaMapper cervejaMapper = CervejaMapper.INSTANCE;

    public CervejaDTO criarCerveja(CervejaDTO cervejaDTO) throws CervejaJaCadastradaException {
        verificarSeJaCadastrada(cervejaDTO.getNome());
        Cerveja cerveja = cervejaMapper.toModel(cervejaDTO);
        Cerveja cervejaSalva = cervejaRepository.save(cerveja);
        return cervejaMapper.toDTO(cervejaSalva);
    }

    public CervejaDTO encontrarPorNome(String nome) throws CervejaNaoEncontradaException {
        Cerveja cervejaEncontrar = cervejaRepository.encontrarPorNome(nome)
                .orElseThrow(() -> new CervejaNaoEncontradaException(nome));
        return cervejaMapper.toDTO(cervejaEncontrar);
    }

    public List<CervejaDTO> listarTudo() {
        return cervejaRepository.findAll()
                .stream()
                .map(cervejaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deletarPorId(Long id) throws CervejaNaoEncontradaException {
        verificarSeExiste(id);
        cervejaRepository.deleteById(id);
    }

    private void verificarSeJaCadastrada(String nome) throws CervejaJaCadastradaException {
        Optional<Cerveja> optCervejaSalva = cervejaRepository.encontrarPorNome(nome);
        if (optCervejaSalva.isPresent()) {
            throw new CervejaJaCadastradaException(nome);
        }
    }

    private Cerveja verificarSeExiste(Long id) throws CervejaNaoEncontradaException {
        return cervejaRepository.findById(id)
                .orElseThrow(() -> new CervejaNaoEncontradaException(id));
    }

    public CervejaDTO incrementar(Long id, int quantidadeIncrementar) throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        Cerveja cervejaIncrementar = verificarSeExiste(id);
        int quantidadeIncrementado = quantidadeIncrementar + cervejaIncrementar.getQuantidade();
        if (quantidadeIncrementado <= cervejaIncrementar.getMaximo()) {
            cervejaIncrementar.setQuantidade(cervejaIncrementar.getQuantidade() + quantidadeIncrementar);
            Cerveja incrementadoEstoqueCerveja = cervejaRepository.save(cervejaIncrementar);
            return cervejaMapper.toDTO(incrementadoEstoqueCerveja);
        }
        throw new CervejaEstoqueExcedidoException(id, quantidadeIncrementar);
    }

    public CervejaDTO decrementar(Long id, int quantidadeDecrementar) throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        Cerveja cervejaDecrementar = verificarSeExiste(id);
        int quantidadeDecrementado = quantidadeDecrementar - cervejaDecrementar.getQuantidade();
        if (quantidadeDecrementado <= cervejaDecrementar.getQuantidade()) {
            cervejaDecrementar.setQuantidade(cervejaDecrementar.getQuantidade() - quantidadeDecrementado);
            Cerveja decrementadoEstoqueCerveja = cervejaRepository.save(cervejaDecrementar);
            return cervejaMapper.toDTO(decrementadoEstoqueCerveja);
        }
        throw new CervejaEstoqueExcedidoException(id, quantidadeDecrementar);
    }

}
