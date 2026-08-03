package br.com.artesanatoestoque.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estoque_loja")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstoqueLoja {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@jakarta.persistence.Version
	private Long versao;

	@ManyToOne
	@JoinColumn(name = "produto_id", nullable = false)
	private Produto produto;

	 @Column(name = "quantidade_disponivel", nullable = false)
	 private Integer quantidadeDisponivel;

	 @Column(name = "quantidade_minima")
	 private Integer quantidadeMinima;

	 @Column(name = "quantidade_maxima")
	 private Integer quantidadeMaxima;

	 @Column(name = "localizacao", length = 200)
	 private String localizacao;

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
