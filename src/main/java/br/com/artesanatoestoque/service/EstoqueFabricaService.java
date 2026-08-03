package br.com.artesanatoestoque.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.artesanatoestoque.dto.EstoqueFabricaRequestDto;
import br.com.artesanatoestoque.dto.EstoqueFabricaResponseDto;
import br.com.artesanatoestoque.entities.EstoqueFabrica;
import br.com.artesanatoestoque.entities.Produto;
import br.com.artesanatoestoque.repository.EstoqueFabricaRepository;
import br.com.artesanatoestoque.repository.ProdutoRepository;


@Service
public class EstoqueFabricaService {

    @Autowired
    private  EstoqueFabricaRepository estoqueFabricaRepository;

    @Autowired
    private  ProdutoRepository produtoRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Converte a entidade para o DTO de resposta, incluindo o nome do produto
     * (que não existe diretamente na entidade EstoqueFabrica, só via relacionamento).
     */
    private EstoqueFabricaResponseDto toResponseDto(EstoqueFabrica estoque) {
        EstoqueFabricaResponseDto dto = modelMapper.map(estoque, EstoqueFabricaResponseDto.class);
        dto.setProdutoId(estoque.getProduto().getId());
        dto.setProdutoNome(estoque.getProduto().getProdutoNome());
        return dto;
    }

    /**
     * Cria estoque inicial para um produto na fábrica
     */
    @Transactional
    public EstoqueFabricaResponseDto criar(@NonNull EstoqueFabricaRequestDto request) {
        // Valida se o produto existe
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        // Valida se já existe estoque para este produto
        if (estoqueFabricaRepository.findByProdutoId(request.getProdutoId()).isPresent()) {
            throw new IllegalArgumentException("Já existe estoque na fábrica para este produto.");
        }

        // Cria o estoque
        EstoqueFabrica estoque = new EstoqueFabrica();
        estoque.setProduto(produto);
        estoque.setQuantidadeDisponivel(request.getQuantidadeDisponivel());
        estoque.setQuantidadeMinima(request.getQuantidadeMinima());
        estoque.setQuantidadeMaxima(request.getQuantidadeMaxima());
        EstoqueFabrica salvo = estoqueFabricaRepository.save(estoque);
        return toResponseDto(salvo);
    }

    /**
     * Lista todos os estoques da fábrica
     */
    @Transactional(readOnly = true)
    public List<EstoqueFabricaResponseDto> listarTodos() {
        return estoqueFabricaRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca estoque por ID
     */
    @Transactional(readOnly = true)
    public EstoqueFabricaResponseDto buscarPorId(UUID id) {
        EstoqueFabrica estoque = estoqueFabricaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado."));

        return toResponseDto(estoque);
    }

    /**
     * Busca estoque por ID do produto
     */
    @Transactional(readOnly = true)
    public EstoqueFabricaResponseDto buscarPorProdutoId(UUID produtoId) {
        EstoqueFabrica estoque = estoqueFabricaRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para este produto."));

        return toResponseDto(estoque);
    }

    /**
     * Atualiza informações do estoque (localização, min, max)
     */
    @Transactional
    public EstoqueFabricaResponseDto atualizar(UUID id, EstoqueFabricaRequestDto request) {
        EstoqueFabrica estoque = estoqueFabricaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado."));

        // Atualiza apenas campos permitidos (não muda quantidade diretamente)
        estoque.setQuantidadeMinima(request.getQuantidadeMinima());
        estoque.setQuantidadeMaxima(request.getQuantidadeMaxima());
        estoque.setLocalizacao(request.getLocalizacao());

        EstoqueFabrica atualizado = estoqueFabricaRepository.save(estoque);
        return toResponseDto(atualizado);
    }

    /**
     * Deleta estoque (apenas se quantidade for zero)
     */
    @Transactional
    public void deletar(UUID id) {
        EstoqueFabrica estoque = estoqueFabricaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado."));

        if (estoque.getQuantidadeDisponivel() > 0) {
            throw new IllegalArgumentException("Não é possível deletar estoque com quantidade disponível.");
        }

        estoqueFabricaRepository.deleteById(id);
    }

    /**
     * Busca estoques abaixo do mínimo (para alertas)
     */
    @Transactional(readOnly = true)
    public List<EstoqueFabricaResponseDto> buscarAbaixoDoMinimo() {
        return estoqueFabricaRepository.findAll()
                .stream()
                .filter(e -> e.getQuantidadeMinima() != null
                        && e.getQuantidadeDisponivel() < e.getQuantidadeMinima())
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Adiciona quantidade ao estoque (usado pelas movimentações)
     */
    @Transactional
    public void adicionarQuantidade(UUID produtoId, Integer quantidade) {
        EstoqueFabrica estoque = estoqueFabricaRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para este produto."));

        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + quantidade);
        estoqueFabricaRepository.save(estoque);
    }

    /**
     * Remove quantidade do estoque (usado pelas movimentações)
     */
    @Transactional
    public void removerQuantidade(UUID produtoId, Integer quantidade) {
        EstoqueFabrica estoque = estoqueFabricaRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para este produto."));

        if (estoque.getQuantidadeDisponivel() < quantidade) {
            throw new IllegalArgumentException("Quantidade insuficiente em estoque. Disponível: "
                    + estoque.getQuantidadeDisponivel());
        }

        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - quantidade);
        estoqueFabricaRepository.save(estoque);
    }
}