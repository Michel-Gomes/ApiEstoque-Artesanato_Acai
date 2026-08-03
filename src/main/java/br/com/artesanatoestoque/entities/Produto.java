package br.com.artesanatoestoque.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.artesanatoestoque.enums.CategoriaEnum;
import br.com.artesanatoestoque.enums.UnidadeMedidaEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "produto_nome", nullable = false, length = 100)
	@NotBlank(message = "O nome do produto é obrigatório")
	@Size(min = 2, max = 100)
	private String produtoNome;

	@Size(max = 500)
	@Column(length = 500)
	private String descricao;

	@NotBlank(message = "Código de barras é obrigatório")
	@Column(name = "codigo_barras", unique = true, length = 50)
	private String codigoBarras;

	@NotNull(message = " O preço de custo é obrigatório")
	@DecimalMin(value = "0.01")
	@Column(name = "preco_custo", nullable = false, precision = 10, scale = 2)
	private BigDecimal precoCusto;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "categoria", nullable = false, columnDefinition = "VARCHAR(50)")
	private CategoriaEnum categoria;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "unidadeMedida", nullable = false, columnDefinition = "VARCHAR(50)")
	private UnidadeMedidaEnum unidadeMedida;

	@Column(name = "perecivel", nullable = false)
	private Boolean perecivel;

	@Column(name = "data_validade")
	private LocalDate dataValidade;

	@Column(name = "fornecedor", length = 200)
	private String fornecedor;

	@Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "usuario_nome", length = 200)
    private String usuarioNome;

	@Column(name = "data_criacao", updatable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_atualizacao")
	private LocalDateTime dataAtualizacao;



	@PrePersist
	protected void onCreate() {
		dataCriacao = LocalDateTime.now();
		dataAtualizacao = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		dataAtualizacao = LocalDateTime.now();
	}

}
