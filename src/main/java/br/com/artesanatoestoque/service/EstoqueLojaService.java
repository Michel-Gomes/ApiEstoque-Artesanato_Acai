package br.com.artesanatoestoque.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.artesanatoestoque.dto.EstoqueLojaRequestDto;
import br.com.artesanatoestoque.dto.EstoqueLojaResponseDto;
import br.com.artesanatoestoque.entities.EstoqueLoja;
import br.com.artesanatoestoque.entities.Produto;
import br.com.artesanatoestoque.repository.EstoqueLojaRepository;
import br.com.artesanatoestoque.repository.ProdutoRepository;


@Service
public class EstoqueLojaService {

    @Autowired
    private EstoqueLojaRepository estoqueLojaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Converte a entidade para o DTO de resposta, incluindo o nome do produto
     * (que não existe diretamente na entidade EstoqueLoja, só via relacionamento).
     */
    private EstoqueLojaResponseDto toResponseDto(EstoqueLoja estoque) {
        EstoqueLojaResponseDto dto = modelMapper.map(estoque, EstoqueLojaResponseDto.class);
        dto.setProdutoId(estoque.getProduto().getId());
        dto.setProdutoNome(estoque.getProduto().getProdutoNome());
        return dto;
    }

    /**
     * Cria estoque inicial para um produto na loja
     */
    @Transactional
    public EstoqueLojaResponseDto criar(EstoqueLojaRequestDto request) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        if (estoqueLojaRepository.existsByProdutoId(request.getProdutoId())) {
            throw new IllegalArgumentException("Já existe estoque na loja para este produto.");
        }

        EstoqueLoja estoque = modelMapper.map(request, EstoqueLoja.class);
        estoque.setId(null);
        estoque.setProduto(produto);
        EstoqueLoja salvo = estoqueLojaRepository.save(estoque);
        return toResponseDto(salvo);
    }

    /**
     * Lista todos os estoques da loja
     */
    @Transactional(readOnly = true)
    public List<EstoqueLojaResponseDto> listarTodos() {
        return estoqueLojaRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca estoque por ID
     */
    @Transactional(readOnly = true)
    public EstoqueLojaResponseDto buscarPorId(UUID id) {
        EstoqueLoja estoque = estoqueLojaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado."));
        return toResponseDto(estoque);
    }

    /**
     * Busca estoque por ID do produto
     */
    @Transactional(readOnly = true)
    public EstoqueLojaResponseDto buscarPorProdutoId(UUID produtoId) {
        EstoqueLoja estoque = estoqueLojaRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para este produto."));
        return toResponseDto(estoque);
    }

    /**
     * Atualiza informações do estoque (localização, min, max)
     * NÃO atualiza quantidade diretamente - isso é feito por movimentações
     */
    @Transactional
    public EstoqueLojaResponseDto atualizar(UUID id, EstoqueLojaRequestDto request) {
        EstoqueLoja estoque = estoqueLojaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado."));

        estoque.setQuantidadeMinima(request.getQuantidadeMinima());
        estoque.setQuantidadeMaxima(request.getQuantidadeMaxima());
        estoque.setLocalizacao(request.getLocalizacao());

        EstoqueLoja atualizado = estoqueLojaRepository.save(estoque);
        return toResponseDto(atualizado);
    }

    /**
     * Deleta estoque (apenas se quantidade for zero)
     */
    @Transactional
    public void deletar(UUID id) {
        EstoqueLoja estoque = estoqueLojaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado."));

        if (estoque.getQuantidadeDisponivel() > 0) {
            throw new IllegalArgumentException(
                    "Não é possível deletar estoque com quantidade disponível. Atual: "
                            + estoque.getQuantidadeDisponivel()
            );
        }

        estoqueLojaRepository.deleteById(id);
    }

    /**
     * Busca estoques abaixo do mínimo (para alertas)
     */
    @Transactional(readOnly = true)
    public List<EstoqueLojaResponseDto> buscarAbaixoDoMinimo() {
        return estoqueLojaRepository.findAll()
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
        EstoqueLoja estoque = estoqueLojaRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para este produto."));

        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + quantidade);
        estoqueLojaRepository.save(estoque);
    }

    /**
     * Remove quantidade do estoque (usado pelas movimentações)
     */
    @Transactional
    public void removerQuantidade(UUID produtoId, Integer quantidade) {
        EstoqueLoja estoque = estoqueLojaRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para este produto."));

        if (estoque.getQuantidadeDisponivel() < quantidade) {
            throw new IllegalArgumentException(
                    "Quantidade insuficiente em estoque. Disponível: " + estoque.getQuantidadeDisponivel()
            );
        }

        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - quantidade);
        estoqueLojaRepository.save(estoque);
    }
}