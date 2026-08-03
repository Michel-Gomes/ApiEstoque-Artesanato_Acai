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

import br.com.artesanatoestoque.dto.EstoqueLojaRequestDto;
import br.com.artesanatoestoque.dto.EstoqueLojaResponseDto;
import br.com.artesanatoestoque.service.EstoqueLojaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/estoque-loja")
@Tag(name = "Estoque Loja", description = "Endpoints para gerenciamento do estoque da loja")
@RequiredArgsConstructor
public class EstoqueLojaController {

    @Autowired
    private EstoqueLojaService service;

   
     //Criar estoque inicial para um produto na loja
   
    @Operation(summary = "Criar estoque inicial para um produto na loja",
    		description = "Cria um novo estoque para um produto na loja com as configurações iniciais fornecidas")
    @PostMapping
    public ResponseEntity<EstoqueLojaResponseDto> criar(@Valid @RequestBody EstoqueLojaRequestDto request) {
        EstoqueLojaResponseDto response = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

  
     //Listar todos os estoques da loja
    
    @Operation(summary = "Listar todos os estoques da loja",
    		description = "Retorna uma lista de todos os estoques disponíveis na loja")
    @GetMapping
    public ResponseEntity<List<EstoqueLojaResponseDto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }


     //Buscar estoque por ID
     
    @Operation(summary ="Buscar estoque por ID",
    		description = "Retorna os detalhes do estoque correspondente ao ID")
    @GetMapping("/{id}")
    public ResponseEntity<EstoqueLojaResponseDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    
     //Buscar estoque por ID do produto
    
    @Operation(summary = "Buscar estoque por ID do produto",
    		description = "Retorna os detalhes do estoque correspondente ao ID do produto")
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<EstoqueLojaResponseDto> buscarPorProdutoId(@PathVariable UUID produtoId) {
        return ResponseEntity.ok(service.buscarPorProdutoId(produtoId));
    }

    
    //Atualizar configurações do estoque (localização, min, max)
     
    @Operation(summary = "Atualizar estoque da loja",
    		description = "Atualiza as configurações do estoque, como localização, quantidade mínima e máxima")
    @PutMapping("/{id}")
    public ResponseEntity<EstoqueLojaResponseDto> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody EstoqueLojaRequestDto request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    
     // Deletar estoque (apenas se quantidade = 0)
     
    @Operation(summary = "Deletar estoque da loja",
    		description = "Deleta o estoque se a quantidade atual for igual a zero")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    
     //Listar estoques abaixo do mínimo (alerta)
    
    @Operation(summary = "Listar estoques abaixo do mínimo",
    		description = "Lista todos os estoques que estão abaixo da quantidade mínima definida")
    @GetMapping("/alerta/minimo")
    public ResponseEntity<List<EstoqueLojaResponseDto>> buscarAbaixoMinimo() {
        return ResponseEntity.ok(service.buscarAbaixoDoMinimo());
    }
}