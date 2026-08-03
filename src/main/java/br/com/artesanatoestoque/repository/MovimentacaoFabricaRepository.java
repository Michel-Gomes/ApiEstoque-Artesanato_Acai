package br.com.artesanatoestoque.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.artesanatoestoque.entities.MovimentacaoFabrica;
import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;

public interface MovimentacaoFabricaRepository extends JpaRepository<MovimentacaoFabrica, UUID> {

    List<MovimentacaoFabrica> findByProdutoId(UUID produtoId);

    List<MovimentacaoFabrica> findByTipoMovimentacao(TipoMovimentacaoEnum tipo);

    List<MovimentacaoFabrica> findByLoteFabricacaoId(UUID loteId);

    @Query("SELECT m FROM MovimentacaoFabrica m WHERE m.dataMovimentacao BETWEEN :inicio AND :fim ORDER BY m.dataMovimentacao DESC")
    List<MovimentacaoFabrica> findByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT m FROM MovimentacaoFabrica m WHERE m.produto.id = :produtoId ORDER BY m.dataMovimentacao DESC")
    List<MovimentacaoFabrica> findHistoricoPorProduto(@Param("produtoId") UUID produtoId);
}