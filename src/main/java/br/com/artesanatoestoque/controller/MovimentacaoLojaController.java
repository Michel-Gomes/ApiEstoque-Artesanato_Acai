package br.com.artesanatoestoque.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.artesanatoestoque.dto.MovimentacaoLojaRequestDto;
import br.com.artesanatoestoque.dto.MovimentacaoLojaResponseDto;
import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;
import br.com.artesanatoestoque.service.MovimentacaoLojaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movimentacao-loja")
@Tag(name = "Movimentação Loja", description = "Endpoints para gerenciar movimentações na loja")
@RequiredArgsConstructor
public class MovimentacaoLojaController {

    @Autowired
    private MovimentacaoLojaService service;

     // Registrar movimentação na loja
     
    @Operation(summary = "",
    		description = "")
    @PostMapping
    public ResponseEntity<MovimentacaoLojaResponseDto> registrar(
            @Valid @RequestBody MovimentacaoLojaRequestDto request) {
        MovimentacaoLojaResponseDto response = service.registrarMovimentacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

   
     //Listar todas as movimentações (ordenadas por data DESC)
    
    @Operation(summary = "",
    		description = "")
    @GetMapping
    public ResponseEntity<List<MovimentacaoLojaResponseDto>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    
     //Buscar movimentação por ID
    
    @Operation(summary = "",
    		description = "")
    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoLojaResponseDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    
     //Buscar histórico de movimentações de um produto
     
    @Operation(summary = "",
    		description = "")
    @GetMapping("/produto/{produtoId}/historico")
    public ResponseEntity<List<MovimentacaoLojaResponseDto>> buscarHistoricoPorProduto(
            @PathVariable UUID produtoId) {
        return ResponseEntity.ok(service.buscarHistoricoPorProduto(produtoId));
    }

    
     //Buscar movimentações por tipo
    
     @Operation(summary = "",
    		description = "")
    @GetMapping("/tipo")
    public ResponseEntity<List<MovimentacaoLojaResponseDto>> buscarPorTipo(
            @RequestParam TipoMovimentacaoEnum tipo) {
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }

   
     //Buscar movimentações por período
    
    @Operation(summary = "",
     		description = "")
    @GetMapping("/periodo")
    public ResponseEntity<List<MovimentacaoLojaResponseDto>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(service.buscarPorPeriodo(inicio, fim));
    }

    
     //Buscar movimentações de hoje
    
    @Operation(summary = "",
    		description = "")
    @GetMapping("/hoje")
    public ResponseEntity<List<MovimentacaoLojaResponseDto>> buscarMovimentacoesHoje() {
        return ResponseEntity.ok(service.buscarMovimentacoesHoje());
    }

    
     //Buscar últimas N movimentações
    
    @Operation(summary = "",
    		description = "")
    @GetMapping("/ultimas")
    public ResponseEntity<List<MovimentacaoLojaResponseDto>> buscarUltimas(
            @RequestParam(defaultValue = "10") int quantidade) {
        return ResponseEntity.ok(service.buscarUltimas(quantidade));
    }
}