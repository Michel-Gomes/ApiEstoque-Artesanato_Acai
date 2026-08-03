package br.com.artesanatoestoque.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.artesanatoestoque.entities.MovimentacaoLoja;
import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;

public interface MovimentacaoLojaRepository extends JpaRepository<MovimentacaoLoja, UUID>{

	//listar todas as movimentações de uma loja pelo id do estoque da loja

	List <MovimentacaoLoja> findByEstoqueLoja_Id(UUID estoqueLojaId);

	List <MovimentacaoLoja> findByEstoqueLoja_Produto_Id(UUID produtoId);

	List <MovimentacaoLoja> findByTipoMovimentacao(TipoMovimentacaoEnum tipo);

	Boolean existsByEstoqueLoja_Id(UUID estoqueLojaId);

	List<MovimentacaoLoja> findByProdutoId(UUID produtoId);
	
	List<MovimentacaoLoja> findByDataMovimentacaoBetween(LocalDateTime inicio, LocalDateTime fim);


}
