package br.com.artesanatoestoque.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteFabricacaoResponseDto {

    private UUID id;
    private UUID produtoId;
    private String produtoNome;
    private String codigoLote;
    private Integer quantidadeFabricada;
    private Integer quantidadeDisponivel;
    private LocalDate dataFabricacao;
    private LocalDate dataValidade;
    private String observacao;
    private UUID usuarioId;
    private String usuarioNome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}