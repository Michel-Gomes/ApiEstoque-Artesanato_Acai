package br.com.artesanatoestoque.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstoqueLojaRequestDto {

	@NotNull(message = "O ID do produto é obrigatório")
    private UUID produtoId;
	
    @NotNull(message = "A quantidade disponivel é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidadeDisponivel;

    @Min(value = 0)
    private Integer quantidadeMinima;

    @Min(value = 0)
    private Integer quantidadeMaxima;

    private String localizacao;

}
