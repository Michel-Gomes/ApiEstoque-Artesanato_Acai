package br.com.artesanatoestoque.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.artesanatoestoque.entities.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    boolean existsByprodutoNomeIgnoreCase(String produtoNome);

    boolean existsByCodigoBarras(String codigoBarras);
}