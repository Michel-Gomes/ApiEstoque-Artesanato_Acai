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

import br.com.artesanatoestoque.dto.MovimentacaoFabricaRequestDto;
import br.com.artesanatoestoque.dto.MovimentacaoFabricaResponseDto;
import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;
import br.com.artesanatoestoque.service.MovimentacaoFabricaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movimentacao-fabrica")
@Tag(name = "Movimentação Fábrica", description = "Endpoints para gerenciar movimentações na fábrica")
@RequiredArgsConstructor
public class MovimentacaoFabricaController {

    @Autowired
    private MovimentacaoFabricaService service;

    /**
     * Registrar movimentação na fábrica
     * POST /api/v1/movimentacao-fabrica
     */
    
    //Registrar movimentação na fábrica
    @Operation(summary = "Registrar movimentação na fábrica",
    		description = "Registra uma nova movimentação de produto na fábrica")
    @PostMapping
    public ResponseEntity<MovimentacaoFabricaResponseDto> registrar(
            @Valid @RequestBody MovimentacaoFabricaRequestDto request) {
        MovimentacaoFabricaResponseDto response = service.registrarMovimentacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

     //Listar todas as movimentações
     
     
    @Operation(summary = "Listar todas as movimentações",
    		description = "Lista todas as movimentações registradas na fábrica, ordenadas por data DESC")
    @GetMapping
    public ResponseEntity<List<MovimentacaoFabricaResponseDto>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    
     //Buscar movimentação por ID
    
    @Operation(summary = "Buscar movimentação por ID",
    		description = "Busca uma movimentação específica na fábrica pelo seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoFabricaResponseDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    
     // Buscar histórico de movimentações de um produto
    
    @Operation(summary = "Buscar histórico de movimentações de um produto",
    		description = "Busca o histórico completo de movimentações para um produto específico na fábrica")
    @GetMapping("/produto/{produtoId}/historico")
    public ResponseEntity<List<MovimentacaoFabricaResponseDto>> buscarHistoricoPorProduto(
            @PathVariable UUID produtoId) {
        return ResponseEntity.ok(service.buscarHistoricoPorProduto(produtoId));
    }

     // Buscar movimentações de um lote
     
    @Operation(summary = "Buscar movimentações de um lote",
    		description = "Busca todas as movimentações associadas a um lote específico na fábrica")
    @GetMapping("/lote/{loteId}")
    public ResponseEntity<List<MovimentacaoFabricaResponseDto>> buscarPorLote(@PathVariable UUID loteId) {
        return ResponseEntity.ok(service.buscarPorLote(loteId));
    }

     //Buscar movimentações por tipo
     
    @Operation(summary = "Buscar movimentações por tipo",
    		description = "Busca todas as movimentações na fábrica de um tipo específico (entrada ou saída)")
    @GetMapping("/tipo")
    public ResponseEntity<List<MovimentacaoFabricaResponseDto>> buscarPorTipo(
            @RequestParam TipoMovimentacaoEnum tipo) {
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }

   
    // Buscar movimentações por período
    
    @Operation(summary = "Buscar movimentações por período",
    		description = "Busca todas as movimentações na fábrica dentro de um intervalo de datas especificado")
    @GetMapping("/periodo")
    public ResponseEntity<List<MovimentacaoFabricaResponseDto>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(service.buscarPorPeriodo(inicio, fim));
    }

     // Buscar movimentações de hoje
     
    @Operation(summary = "Buscar movimentações de hoje",
    		description = "Busca todas as movimentações registradas na fábrica no dia atual")
    @GetMapping("/hoje")
    public ResponseEntity<List<MovimentacaoFabricaResponseDto>> buscarMovimentacoesHoje() {
        return ResponseEntity.ok(service.buscarMovimentacoesHoje());
    }

    
     // Buscar últimas movimentações
     
    @Operation(summary = "Buscar últimas movimentações",
    		description = "Busca as últimas N movimentações registradas na fábrica, ordenadas por data DESC")
    @GetMapping("/ultimas")
    public ResponseEntity<List<MovimentacaoFabricaResponseDto>> buscarUltimas(
            @RequestParam(defaultValue = "10") int quantidade) {
        return ResponseEntity.ok(service.buscarUltimas(quantidade));
    }
}