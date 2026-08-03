package br.com.artesanatoestoque.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class LoteFabricacaoRequestDto {

    @NotNull(message = "Produto é obrigatório")
    private UUID produtoId;

    @NotBlank(message = "Código do lote é obrigatório")
    @Size(max = 50)
    private String codigoLote;

    @NotNull(message = "Quantidade fabricada é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidadeFabricada;

    @NotNull(message = "Data de fabricação é obrigatória")
    private LocalDate dataFabricacao;

    private LocalDate dataValidade;

    @Size(max = 500)
    private String observacao;

    @NotNull(message = "Usuario ID é obrigatório")
    private UUID usuarioId;

    @NotNull(message = "Nome do usuário é obrigatório")
    private String usuarioNome;
}