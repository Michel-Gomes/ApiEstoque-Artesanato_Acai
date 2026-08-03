package br.com.artesanatoestoque.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.artesanatoestoque.dto.LoteFabricacaoRequestDto;
import br.com.artesanatoestoque.dto.LoteFabricacaoResponseDto;
import br.com.artesanatoestoque.service.LoteFabricacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/lotes-fabricacao")
@Tag(name = "Lote de Fabricação", description = "Endpoints para gerenciamento de lotes de fabricação")
@RequiredArgsConstructor
public class LoteFabricacaoController {

    @Autowired
    private LoteFabricacaoService service;

    // Criar novo lote de fabricação
    @Operation(summary = "Criar novo lote de fabricação",
            description = "Cria um novo lote de fabricação com as informações fornecidas")
    @PostMapping
    public ResponseEntity<LoteFabricacaoResponseDto> criar(@Valid @RequestBody LoteFabricacaoRequestDto request) {
        LoteFabricacaoResponseDto response = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Atualizar lote de fabricação existente
    @Operation(summary = "Atualizar lote de fabricação",
            description = "Atualiza código, datas e observação de um lote existente. A quantidade não é alterada por aqui.")
    @PutMapping("/{id}")
    public ResponseEntity<LoteFabricacaoResponseDto> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody LoteFabricacaoRequestDto request) {
        LoteFabricacaoResponseDto response = service.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    // Listar todos os lotes de fabricação
    @Operation(summary = "Listar todos os lotes de fabricação",
            description = "Lista todos os lotes de fabricação cadastrados")
    @GetMapping
    public ResponseEntity<List<LoteFabricacaoResponseDto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Buscar lote de fabricação por ID
    @Operation(summary = "Buscar lote de fabricação por ID",
            description = "Busca um lote de fabricação específico pelo seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<LoteFabricacaoResponseDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Buscar lotes de fabricação por produto
    @Operation(summary = "Buscar lotes de fabricação por produto",
            description = "Busca todos os lotes de fabricação associados a um produto específico")
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<LoteFabricacaoResponseDto>> buscarPorProduto(@PathVariable UUID produtoId) {
        return ResponseEntity.ok(service.buscarPorProduto(produtoId));
    }

    // Buscar lotes de fabricação com vencimento próximo
    @Operation(summary = "Buscar lotes de fabricação com vencimento próximo",
            description = "Busca lotes de fabricação que estão com vencimento próximo dentro do número de dias especificado")
    @GetMapping("/proximos-vencimento")
    public ResponseEntity<List<LoteFabricacaoResponseDto>> buscarProximosVencimento(
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(service.buscarLotesProximosVencimento(dias));
    }
}