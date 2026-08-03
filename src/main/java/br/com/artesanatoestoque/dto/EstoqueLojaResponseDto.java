package br.com.artesanatoestoque.dto;

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
public class EstoqueLojaResponseDto {

	 	private UUID id;
	    private UUID produtoId;
	    private String produtoNome;
	    private Integer quantidadeDisponivel;
	    private Integer quantidadeMinima;
	    private Integer quantidadeMaxima;
	    private String localizacao;
	    private LocalDateTime dataCriacao;
	    private LocalDateTime dataAtualizacao;
}
