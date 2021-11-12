package br.com.backend.cervejaria.service;

import br.com.backend.cervejaria.builder.CervejaDTOBuilder;
import br.com.backend.cervejaria.dto.CervejaDTO;
import br.com.backend.cervejaria.entity.Cerveja;
import br.com.backend.cervejaria.exception.CervejaEstoqueExcedidoException;
import br.com.backend.cervejaria.exception.CervejaJaCadastradaException;
import br.com.backend.cervejaria.exception.CervejaNaoEncontradaException;
import br.com.backend.cervejaria.mapper.CervejaMapper;
import br.com.backend.cervejaria.repository.CervejaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CervejaServiceTest {

    private static final long ID_INVALIDO_CERVEJA = 1L;

    @Mock
    private CervejaRepository cervejaRepository;

    private CervejaMapper cervejaMapper = CervejaMapper.INSTANCE;

    @InjectMocks
    private CervejaService cervejaService;


    @Test
    void quandoCervejaInformadaEntaoIssoDeveSerCriado() throws CervejaJaCadastradaException {
        CervejaDTO cervejaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperadaSalva = cervejaMapper.toModel(cervejaEsperadaDTO);

        when(cervejaRepository.encontrarPorNome(cervejaEsperadaDTO.getNome())).thenReturn(Optional.empty());
        when(cervejaRepository.save(cervejaEsperadaSalva)).thenReturn(cervejaEsperadaSalva);

        CervejaDTO cervejaCriadaDTO = cervejaService.criarCerveja(cervejaEsperadaDTO);

        assertThat(cervejaCriadaDTO.getId(), is(equalTo(cervejaEsperadaDTO.getId())));
        assertThat(cervejaCriadaDTO.getNome(), is(equalTo(cervejaEsperadaDTO.getNome())));
        assertThat(cervejaCriadaDTO.getQuantidade(), is(equalTo(cervejaEsperadaDTO.getQuantidade())));
    }

    @Test
    void quandoCervejaJaRegistradaEntaoExcecaoDeveSerLancada() {
        CervejaDTO cervejaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaDuplicada = cervejaMapper.toModel(cervejaEsperadaDTO);

        when(cervejaRepository.encontrarPorNome(cervejaEsperadaDTO.getNome())).thenReturn(Optional.of(cervejaDuplicada));

        assertThrows(CervejaJaCadastradaException.class, () -> cervejaService.criarCerveja(cervejaEsperadaDTO));
    }

    @Test
    void quandoNomeValidoCervejaFornecidoEntaoDevolvaCerveja() throws CervejaNaoEncontradaException {
        CervejaDTO cervejaEsperadaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperadaEncontrada = cervejaMapper.toModel(cervejaEsperadaEncontradaDTO);

        when(cervejaRepository.encontrarPorNome(cervejaEsperadaEncontrada.getNome())).thenReturn(Optional.of(cervejaEsperadaEncontrada));

        CervejaDTO cervejaEncontradaDTO = cervejaService.encontrarPorNome(cervejaEsperadaEncontradaDTO.getNome());

        assertThat(cervejaEncontradaDTO, is(equalTo(cervejaEsperadaEncontrada)));
    }

    @Test
    void quandoNomeCervejaFornecidoNaoRegistradoEntaoLancarExcecao() {
        CervejaDTO cervejaEsperadaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaRepository.encontrarPorNome(cervejaEsperadaEncontradaDTO.getNome())).thenReturn(Optional.empty());

        assertThrows(CervejaNaoEncontradaException.class, () -> cervejaService.encontrarPorNome(cervejaEsperadaEncontradaDTO.getNome()));
    }

    @Test
    void quandoListaCervejaEchamadaEntaoRetorneUmalistaCervejas() {
        CervejaDTO cervejaEsperadaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperadaEncontrada = cervejaMapper.toModel(cervejaEsperadaEncontradaDTO);

        when(cervejaRepository.findAll()).thenReturn(Collections.singletonList(cervejaEsperadaEncontrada));

        List<CervejaDTO> encontrarListaCervejasDTO = cervejaService.listarTudo();

        assertThat(encontrarListaCervejasDTO, is(not(empty())));
        assertThat(encontrarListaCervejasDTO.get(0), is(equalTo(cervejaEsperadaEncontradaDTO)));
    }

    @Test
    void quandoListaCervejaChamadaEntaoRetornelistaCervejasVazia() {
        when(cervejaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<CervejaDTO> encontrarListaCervejasDTO = cervejaService.listarTudo();

        assertThat(encontrarListaCervejasDTO, is(empty()));
    }

    @Test
    void quandoExclusaoChamadoComIdValidoEntaoCervejaDeveSerDeletada() throws CervejaNaoEncontradaException{
        CervejaDTO cervejaDeletadaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaDeletadaEsperada = cervejaMapper.toModel(cervejaDeletadaEsperadaDTO);

        when(cervejaRepository.findById(cervejaDeletadaEsperadaDTO.getId())).thenReturn(Optional.of(cervejaDeletadaEsperada));
        doNothing().when(cervejaRepository).deleteById(cervejaDeletadaEsperadaDTO.getId());

        cervejaService.deletarPorId(cervejaDeletadaEsperadaDTO.getId());

        verify(cervejaRepository, times(1)).findById(cervejaDeletadaEsperadaDTO.getId());
        verify(cervejaRepository, times(1)).deleteById(cervejaDeletadaEsperadaDTO.getId());
    }

    @Test
    void quandoIncrementarChamadoEntaoIncrementarEstoqueCerveja() throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        CervejaDTO cervejaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaEsperadaDTO);

        when(cervejaRepository.findById(cervejaEsperadaDTO.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);

        int quantidadeAincrementar = 10;
        int quantidadeEsperadaAposIncremento = cervejaEsperadaDTO.getQuantidade() + quantidadeAincrementar;

        CervejaDTO incrementedCervejaDTO = cervejaService.incrementar(cervejaEsperadaDTO.getId(), quantidadeAincrementar);

        assertThat(quantidadeEsperadaAposIncremento, equalTo(incrementedCervejaDTO.getQuantidade()));
        assertThat(quantidadeEsperadaAposIncremento, lessThan(cervejaEsperadaDTO.getMaximo()));
    }

    @Test
    void quandoIncrementoMaiorQueMaximoLancarExcecao() {
        CervejaDTO cervejaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaEsperadaDTO);

        when(cervejaRepository.findById(cervejaEsperadaDTO.getId())).thenReturn(Optional.of(cervejaEsperada));

        int quantidadeAincrementar = 80;
        assertThrows(CervejaEstoqueExcedidoException.class, () -> cervejaService.incrementar(cervejaEsperadaDTO.getId(), quantidadeAincrementar));
    }

    @Test
    void quandoIncrementoAposSomaMaiorQueMaximoEntaoLancarExcecao() {

        CervejaDTO cervejaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaEsperadaDTO);

        when(cervejaRepository.findById(cervejaEsperadaDTO.getId())).thenReturn(Optional.of(cervejaEsperada));

        int quantidadeAincrementar = 45;
        assertThrows(CervejaEstoqueExcedidoException.class, () -> cervejaService.incrementar(cervejaEsperadaDTO.getId(), quantidadeAincrementar));
    }

    @Test
    void quandoIncrementoChamadoComIdInvalidoEntaoLancarExcecao() {
        int quantidadeAincrementar = 10;

        when(cervejaRepository.findById(ID_INVALIDO_CERVEJA)).thenReturn(Optional.empty());

        assertThrows(CervejaNaoEncontradaException.class, () -> cervejaService.incrementar(ID_INVALIDO_CERVEJA, quantidadeAincrementar));
    }



//Decrementar

    @Test
    void quandoDecrementoChamadoEntaoDecrementarCervejaEstoque() throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        CervejaDTO cervejaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaEsperadaDTO);

        when(cervejaRepository.findById(cervejaEsperadaDTO.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);

        int quantidadeDecrementar = 5;
        int quantidadeEsperadaAposDecremento = cervejaEsperadaDTO.getQuantidade() - quantidadeDecrementar;
        CervejaDTO cervejaDecrementadaDTO = cervejaService.decrementar(cervejaEsperadaDTO.getId(), quantidadeDecrementar);

        assertThat(quantidadeEsperadaAposDecremento, equalTo(cervejaDecrementadaDTO.getQuantidade()));
        assertThat(quantidadeEsperadaAposDecremento, greaterThan(0));
    }

    @Test
    void quandoDecrementoChamadoParaEstoqueCervejaVazio() throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        CervejaDTO cervejaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaEsperadaDTO);

        when(cervejaRepository.findById(cervejaEsperadaDTO.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);

        int quantidadeDecrementar = 10;
        int quantidadeEsperadaAposDecremento = cervejaEsperadaDTO.getQuantidade() - quantidadeDecrementar;
        CervejaDTO cervejaDecrementadaDTO = cervejaService.decrementar(cervejaEsperadaDTO.getId(), quantidadeDecrementar);

        assertThat(quantidadeEsperadaAposDecremento, equalTo(0));
        assertThat(quantidadeEsperadaAposDecremento, equalTo(cervejaDecrementadaDTO.getQuantidade()));
    }

    @Test
    void quandoDecrementarMenorQueZeroEntaoLancarExcecao() {
        CervejaDTO cervejaEsperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaEsperadaDTO);

        when(cervejaRepository.findById(cervejaEsperadaDTO.getId())).thenReturn(Optional.of(cervejaEsperada));

        int quantidadeDecrementar = 80;
        assertThrows(CervejaEstoqueExcedidoException.class, () -> cervejaService.decrementar(cervejaEsperadaDTO.getId(), quantidadeDecrementar));
    }

    @Test
    void quandoDecrementarChamadoComIdInvalidoEntaoLancarExcecao() {
        int quantidadeDecrementar = 10;

        when(cervejaRepository.findById(ID_INVALIDO_CERVEJA)).thenReturn(Optional.empty());

        assertThrows(CervejaNaoEncontradaException.class, () -> cervejaService.decrementar(ID_INVALIDO_CERVEJA, quantidadeDecrementar));
    }

}
