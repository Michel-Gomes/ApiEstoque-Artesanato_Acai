package br.com.artesanatoestoque.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.artesanatoestoque.enums.CategoriaEnum;
import br.com.artesanatoestoque.enums.UnidadeMedidaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoResponseDto {

    private UUID id;
    private String produtoNome;
    private String descricao;
    private String codigoBarras;
    private BigDecimal precoCusto;
    private Integer quantidadeEstoque;
    private CategoriaEnum categoria;
    private UnidadeMedidaEnum unidadeMedida;
    private Boolean perecivel;
    private LocalDate dataValidade;
    private String fornecedor;
    private UUID usuarioId;
    private String usuarioNome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}