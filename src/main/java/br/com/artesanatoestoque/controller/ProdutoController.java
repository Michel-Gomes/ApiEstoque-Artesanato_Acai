package br.com.artesanatoestoque.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.artesanatoestoque.dto.ProdutoRequestDto;
import br.com.artesanatoestoque.dto.ProdutoResponseDto;
import br.com.artesanatoestoque.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produto", description = "Endpoints para gerenciamento de produtos")
@RequiredArgsConstructor
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

	@Operation(summary = "Criar um novo produto",
			description = "Endpoint para criar um novo produto no sistema")
    @PostMapping
    public ResponseEntity<ProdutoResponseDto> criar(@RequestBody ProdutoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(request));
    }

    @Operation(
    		summary = "Buscar produto por ID",
    		description = "Endpoint para buscar um produto específico pelo seu ID"
    		)
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

	@Operation(summary = "Listar todos os produtos",
			description = "Endpoint para listar todos os produtos cadastrados no sistema"
			)
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDto>> listar() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

	@Operation(summary = "Atualizar um produto",
			description = "Endpoint para atualizar os dados de um produto existente"
			)
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> atualizar(
            @PathVariable UUID id,
            @RequestBody ProdutoRequestDto request) {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

	@Operation(summary = "Deletar um produto",
			description = "Endpoint para deletar um produto do sistema pelo seu ID"
			)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
