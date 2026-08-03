package br.com.artesanatoestoque.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;
import br.com.artesanatoestoque.enums.UnidadeMedidaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoLojaResponseDto {

    private UUID id;
    private UUID estoqueLojaId;
    private UUID produtoId;
    private String produtoNome;
    private TipoMovimentacaoEnum tipoMovimentacao;
    private UnidadeMedidaEnum unidadeMedida;
    private Integer quantidade;
    private String observacao;
    private LocalDateTime dataMovimentacao;
    private UUID usuarioId;
    private String usuarioNome;
    private LocalDateTime dataCriacao;

    private UUID loteFabricacaoId;
    private String codigoLote;
    private LocalDate dataValidadeLote;
}