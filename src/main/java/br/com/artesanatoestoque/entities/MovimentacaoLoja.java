package br.com.artesanatoestoque.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "movimentacao_loja")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoLoja {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "estoque_loja_id", nullable = false)
    private EstoqueLoja estoqueLoja;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "lote_fabricacao_id")
    private LoteFabricacao loteFabricacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 50)
    private TipoMovimentacaoEnum tipoMovimentacao;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime dataMovimentacao;

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

        if (dataMovimentacao == null) {
            dataMovimentacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}