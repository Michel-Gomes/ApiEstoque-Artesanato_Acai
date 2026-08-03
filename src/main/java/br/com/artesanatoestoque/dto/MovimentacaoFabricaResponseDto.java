package br.com.artesanatoestoque.dto;

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
public class MovimentacaoFabricaResponseDto {

    private UUID id;
    private UUID estoqueFabricaId;
    private UUID produtoId;
    private String produtoNome;
    private UnidadeMedidaEnum unidadeMedida;
    private UUID loteFabricacaoId;
    private String codigoLote;
    private TipoMovimentacaoEnum tipoMovimentacao;
    private Integer quantidade;
    private String observacao;
    private LocalDateTime dataMovimentacao;
    private UUID usuarioId;
    private String usuarioNome;
    private LocalDateTime dataCriacao;
}