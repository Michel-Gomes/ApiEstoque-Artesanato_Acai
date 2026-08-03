package br.com.artesanatoestoque.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.artesanatoestoque.entities.EstoqueLoja;

@Repository
public interface EstoqueLojaRepository extends JpaRepository<EstoqueLoja, UUID> {

	Optional<EstoqueLoja> findByProdutoId(UUID produtoId);

    boolean existsByProdutoId(UUID produtoId);

}
