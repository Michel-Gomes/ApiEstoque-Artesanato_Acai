package br.com.artesanatoestoque.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.artesanatoestoque.entities.EstoqueFabrica;

public interface EstoqueFabricaRepository extends JpaRepository<EstoqueFabrica, UUID> {

	Optional<EstoqueFabrica> findByProdutoId(UUID produtoId);

    boolean existsByProdutoId(UUID produtoId);
}
