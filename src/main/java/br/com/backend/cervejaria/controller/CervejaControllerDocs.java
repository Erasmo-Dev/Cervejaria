package br.com.backend.cervejaria.controller;

import br.com.backend.cervejaria.dto.CervejaDTO;
import br.com.backend.cervejaria.exception.CervejaJaCadastradaException;
import br.com.backend.cervejaria.exception.CervejaNaoEncontradaException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Gerenciar estoque de cerveja")
public interface CervejaControllerDocs {

    @ApiOperation(value = "Operacão de criação da cerveja")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Sucesso cerveja criada"),
            @ApiResponse(code = 400, message = "Campos obrigatórios ausentes ou valor de intervalo de campo incorreto.")
    })
    CervejaDTO criarCerveja(CervejaDTO cervejaDTO) throws CervejaJaCadastradaException;

    @ApiOperation(value = "Retorna cerveja encontrada por um determinado nome")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cerveja de sucesso encontrada no sistema"),
            @ApiResponse(code = 404, message = "Cerveja com o nome fornecido não encontrada.")
    })
    CervejaDTO encontrarPorNome(@PathVariable String name) throws CervejaNaoEncontradaException;

    @ApiOperation(value = "Retorna uma lista de todas as cervejas cadastradas no sistema")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de todas as cervejas cadastradas no sistema"),
    })
    List<CervejaDTO> listarCervejas();

    @ApiOperation(value = "Exclua uma cerveja encontrada por um determinado ID válido")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Cerveja excluída do sistema com sucesso"),
            @ApiResponse(code = 404, message = "Beer with given id not found.")
    })
    void deletarPorId(@PathVariable Long id) throws CervejaNaoEncontradaException;

}
