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

import br.com.artesanatoestoque.dto.EstoqueFabricaRequestDto;
import br.com.artesanatoestoque.dto.EstoqueFabricaResponseDto;
import br.com.artesanatoestoque.service.EstoqueFabricaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/estoque-fabrica")
@Tag(name = "Estoque Fabrica", description = "Endpoints para gerenciamento do estoque da fábrica")
@RequiredArgsConstructor
public class EstoqueFabricaController {

    @Autowired
    private EstoqueFabricaService service;

    
     //Criar estoque inicial para um produto na fábrica

    @Operation(summary = "Criar estoque para um produto na fábrica",
    		description = "Endpoint para criar o estoque inicial de um produto na fábrica")
    @PostMapping
    public ResponseEntity<EstoqueFabricaResponseDto> criar(@Valid @RequestBody EstoqueFabricaRequestDto request) {
        EstoqueFabricaResponseDto response = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
     //Listar todos os estoques da fábrica

    @Operation(summary = "Listar todos os estoques da fábrica",
    		description = "Endpoint para listar todos os estoques cadastrados na fábrica")
    @GetMapping
    public ResponseEntity<List<EstoqueFabricaResponseDto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    
    // Buscar estoque por ID
     
    @Operation(summary = "Buscar estoque por ID da fábrica ",
    		description = "Endpoint para buscar um estoque específico pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EstoqueFabricaResponseDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }


     //Buscar estoque por ID do produto
    @Operation(summary = "Buscar estoque por ID do produto",
    		description = "Endpoint para buscar o estoque de um produto específico pelo ID do produto")
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<EstoqueFabricaResponseDto> buscarPorProdutoId(@PathVariable UUID produtoId) {
        return ResponseEntity.ok(service.buscarPorProdutoId(produtoId));
    }

     //Atualizar configurações do estoque (localização, min, max)
     
    @Operation(summary = "Atualizar estoque da fábrica",
    		description = "Endpoint para atualizar as informações do estoque da fábrica")
    @PutMapping("/{id}")
    public ResponseEntity<EstoqueFabricaResponseDto> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody EstoqueFabricaRequestDto request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

   
     // Deletar estoque (apenas se quantidade = 0)
     
    @Operation(summary = "Deletar estoque da fábrica",
    		description = "Endpoint para deletar um estoque da fábrica pelo seu ID, desde que a quantidade seja zero")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    
     //Listar estoques abaixo do mínimo (alerta)
    
    @Operation(summary = "Listar estoques abaixo do mínimo",
    		description = "Endpoint para listar todos os estoques que estão abaixo do nível mínimo definido")
    @GetMapping("/alerta/minimo")
    public ResponseEntity<List<EstoqueFabricaResponseDto>> buscarAbaixoMinimo() {
        return ResponseEntity.ok(service.buscarAbaixoDoMinimo());
    }
}