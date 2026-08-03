package br.com.artesanatoestoque.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.artesanatoestoque.entities.LoteFabricacao;

public interface LoteFabricacaoRepository extends JpaRepository<LoteFabricacao, UUID> {
    
    Optional<LoteFabricacao> findByCodigoLote(String codigoLote);
    
    boolean existsByCodigoLote(String codigoLote);
    
    List<LoteFabricacao> findByProdutoId(UUID produtoId);
    
    // Busca lotes próximos ao vencimento
    @Query("SELECT l FROM LoteFabricacao l WHERE l.dataValidade <= :dataLimite AND l.quantidadeDisponivel > 0 ORDER BY l.dataValidade")
    List<LoteFabricacao> findLotesProximosVencimento(@Param("dataLimite") LocalDate dataLimite);
    
    // Busca lotes disponíveis de um produto (FIFO - First In, First Out)
    @Query("SELECT l FROM LoteFabricacao l WHERE l.produto.id = :produtoId AND l.quantidadeDisponivel > 0 ORDER BY l.dataFabricacao")
    List<LoteFabricacao> findLotesDisponiveisPorProduto(@Param("produtoId") UUID produtoId);
}