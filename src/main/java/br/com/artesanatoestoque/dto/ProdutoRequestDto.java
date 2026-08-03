package br.com.artesanatoestoque.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.artesanatoestoque.enums.CategoriaEnum;
import br.com.artesanatoestoque.enums.UnidadeMedidaEnum;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
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
public class ProdutoRequestDto {

	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 2, max = 100)
	private String produtoNome;

	@Size(max = 500)
	private String descricao;

	@Column(name = "codigo_barras", unique = true, length = 50)
	private String codigoBarras;

	@NotNull(message = "Preço de custo é obrigatório")
	@DecimalMin(value = "0.01")
	private BigDecimal precoCusto;

	@NotNull(message = "Categoria é obrigatória")
	private CategoriaEnum categoria;

	@NotNull(message = "Unidade de Medida Obrigatória")
	private UnidadeMedidaEnum unidadeMedida;

	@NotNull
	private Boolean perecivel;
	private LocalDate dataValidade;
	private String fornecedor;

	@NotNull(message = "Usuario ID é obrigatório")
	private UUID usuarioId;

	@NotNull(message = "Nome do usuário é obrigatório")
	private String usuarioNome;
}