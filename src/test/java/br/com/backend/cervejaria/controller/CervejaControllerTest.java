package br.com.backend.cervejaria.controller;

import br.com.backend.cervejaria.builder.CervejaDTOBuilder;
import br.com.backend.cervejaria.dto.CervejaDTO;
import br.com.backend.cervejaria.dto.QuantidadeDTO;
import br.com.backend.cervejaria.exception.CervejaEstoqueExcedidoException;
import br.com.backend.cervejaria.exception.CervejaNaoEncontradaException;
import br.com.backend.cervejaria.service.CervejaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static br.com.backend.cervejaria.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CervejaControllerTest {

    private static final String CERVEJA_API_URL_CAMINHO = "/api/v1/cervejas";
    private static final long ID_VALIDO_CERVEJA = 1L;
    private static final long ID_INVALIDO_CERVEJA = 2l;
    private static final String CERVEJA_API_URL_SUBCAMINHO_INCREMENTO_URL = "/incremento";
    private static final String CERVEJA_API_URL_SUBCAMINHO_DECREMENTO_URL = "/decremento";

    private MockMvc mockMvc;

    @Mock
    private CervejaService cervejaService;

    @InjectMocks
    private CervejaController cervejaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cervejaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void quandoPOSTChamadoCervejaCriada() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.criarCerveja(cervejaDTO)).thenReturn(cervejaDTO);

        mockMvc.perform(post(CERVEJA_API_URL_CAMINHO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(cervejaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoPOSTChamadoSemCampoRequisitadoEntaoRetornaErro() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        cervejaDTO.setMarca(null);

        mockMvc.perform(post(CERVEJA_API_URL_CAMINHO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(cervejaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quandoGETChamadoComNomeValidoEntaoOKStatusRetornado() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.encontrarPorNome(cervejaDTO.getNome())).thenReturn(cervejaDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(CERVEJA_API_URL_CAMINHO + "/" + cervejaDTO.getNome())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoGETChamadoSemNomeRegistradoEntaoNotFoundStatusRetornado() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.encontrarPorNome(cervejaDTO.getNome())).thenThrow(CervejaNaoEncontradaException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(CERVEJA_API_URL_CAMINHO + "/" + cervejaDTO.getNome())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void quandoGETListaComCervejasChamadoEntaoOKStatusRetornado() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.listarTudo()).thenReturn(Collections.singletonList(cervejaDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(CERVEJA_API_URL_CAMINHO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$[0].marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$[0].tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoGETListaSemCervejasChamadoEntaoOKStatusRetornado() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.listarTudo()).thenReturn(Collections.singletonList(cervejaDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(CERVEJA_API_URL_CAMINHO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void quandoDELETEChamdoComIdValidoEntaoNoContentStatusRetornado() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        doNothing().when(cervejaService).deletarPorId(cervejaDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete(CERVEJA_API_URL_CAMINHO + "/" + cervejaDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void quandoDELETEChamadoComIdInvalidoEntaoNotFoundStatusRetornado() throws Exception {
        doThrow(CervejaNaoEncontradaException.class).when(cervejaService).deletarPorId(ID_INVALIDO_CERVEJA);

        mockMvc.perform(MockMvcRequestBuilders.delete(CERVEJA_API_URL_CAMINHO + "/" + ID_INVALIDO_CERVEJA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void quandotoPATCHChamadoParaIncrementoDiscontoEntaoOKStatusRetornado() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(10)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        cervejaDTO.setQuantidade(cervejaDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.incrementar(ID_VALIDO_CERVEJA, quantidadeDTO.getQuantidade())).thenReturn(cervejaDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(CERVEJA_API_URL_CAMINHO + "/" + ID_VALIDO_CERVEJA + CERVEJA_API_URL_SUBCAMINHO_INCREMENTO_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantidadeDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())))
                .andExpect(jsonPath("$.quantidade", is(cervejaDTO.getQuantidade())));
    }

    @Test
    void quandoPATCHChamadoIncrementoMaiorQueMaximoEentaoBadRequestStatusRetornado() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(30)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        cervejaDTO.setQuantidade(cervejaDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.incrementar(ID_VALIDO_CERVEJA, quantidadeDTO.getQuantidade())).thenThrow(CervejaEstoqueExcedidoException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(CERVEJA_API_URL_CAMINHO + "/" + ID_VALIDO_CERVEJA + CERVEJA_API_URL_SUBCAMINHO_INCREMENTO_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoPATCChamdoComIdCervejainvalidoParaIncrementarEntaoNotFoundStatusRetornado() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(30)
                .build();

        when(cervejaService.incrementar(ID_VALIDO_CERVEJA, quantidadeDTO.getQuantidade())).thenThrow(CervejaNaoEncontradaException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(CERVEJA_API_URL_CAMINHO + "/" + ID_VALIDO_CERVEJA + CERVEJA_API_URL_SUBCAMINHO_INCREMENTO_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantidadeDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoPATCHChamadoParaDerementarDiscontoEntaoOKstausRetornado() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(5)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        cervejaDTO.setQuantidade(cervejaDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.decrementar(ID_VALIDO_CERVEJA, quantidadeDTO.getQuantidade())).thenReturn(cervejaDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(CERVEJA_API_URL_CAMINHO + "/" + ID_VALIDO_CERVEJA + CERVEJA_API_URL_SUBCAMINHO_DECREMENTO_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantidadeDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())))
                .andExpect(jsonPath("$.quantidade", is(cervejaDTO.getQuantidade())));
    }

    @Test
    void quandoPATCHChamadoParaDecrementarMenorQueZeroENtaoBadRequestStatusRetornado() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(60)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        cervejaDTO.setQuantidade(cervejaDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.decrementar(ID_VALIDO_CERVEJA, quantidadeDTO.getQuantidade())).thenThrow(CervejaEstoqueExcedidoException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(CERVEJA_API_URL_CAMINHO + "/" + ID_VALIDO_CERVEJA + CERVEJA_API_URL_SUBCAMINHO_DECREMENTO_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoPATCHChamadoComIdCervejainvalidoParaDecrementarEntaoNotFoundStatusRetornado() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(5)
                .build();

        when(cervejaService.decrementar(ID_VALIDO_CERVEJA, quantidadeDTO.getQuantidade())).thenThrow(CervejaNaoEncontradaException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(CERVEJA_API_URL_CAMINHO + "/" + ID_VALIDO_CERVEJA + CERVEJA_API_URL_SUBCAMINHO_DECREMENTO_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantidadeDTO)))
                .andExpect(status().isNotFound());
    }

}
