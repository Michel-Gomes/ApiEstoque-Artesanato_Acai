package br.com.artesanatoestoque.entities;

import java.time.LocalDate;
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
@Table(name = "lote_fabricacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteFabricacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @jakarta.persistence.Version
    private Long versao;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "codigo_lote", nullable = false, unique = true, length = 50)
    private String codigoLote;  // Ex: "LOTE-2025-001"

    @Column(name = "quantidade_fabricada", nullable = false)
    private Integer quantidadeFabricada;

    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;  // Vai diminuindo conforme vendas

    @Column(name = "data_fabricacao", nullable = false)
    private LocalDate dataFabricacao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "observacao", length = 500)
    private String observacao;

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

        // Inicialmente, quantidade disponível = quantidade fabricada
        if (quantidadeDisponivel == null) {
            quantidadeDisponivel = quantidadeFabricada;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}