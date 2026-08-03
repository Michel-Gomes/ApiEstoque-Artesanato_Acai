package br.com.artesanatoestoque.dto;

import java.util.UUID;

import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;
import br.com.artesanatoestoque.enums.UnidadeMedidaEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoLojaRequestDto {

    @NotNull(message = "Produto é obrigatório")
    private UUID produtoId;

    private UUID loteFabricacaoId;

    @NotNull(message = "Tipo de movimentação é obrigatório")
    private TipoMovimentacaoEnum tipoMovimentacao;

    @NotNull(message = "Unidade de medida é obrigatória")
    private UnidadeMedidaEnum unidadeMedida;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidade;

    @Size(max = 500)
    private String observacao;

    @NotNull(message = "Usuario ID é obrigatório")
    private UUID usuarioId;

    @NotNull(message = "Nome do usuário é obrigatório")
    private String usuarioNome;
}