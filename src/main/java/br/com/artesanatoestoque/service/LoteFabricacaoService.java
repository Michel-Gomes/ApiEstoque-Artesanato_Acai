package br.com.artesanatoestoque.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.artesanatoestoque.dto.LoteFabricacaoRequestDto;
import br.com.artesanatoestoque.dto.LoteFabricacaoResponseDto;
import br.com.artesanatoestoque.entities.LoteFabricacao;
import br.com.artesanatoestoque.entities.Produto;
import br.com.artesanatoestoque.repository.LoteFabricacaoRepository;
import br.com.artesanatoestoque.repository.ProdutoRepository;

@Service
public class LoteFabricacaoService {

    @Autowired
    private LoteFabricacaoRepository loteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Converte a entidade para o DTO de resposta, incluindo o nome do produto
     * (que não existe diretamente na entidade LoteFabricacao, só via relacionamento).
     */
    private LoteFabricacaoResponseDto toResponseDto(LoteFabricacao lote) {
        LoteFabricacaoResponseDto dto = modelMapper.map(lote, LoteFabricacaoResponseDto.class);
        dto.setProdutoId(lote.getProduto().getId());
        dto.setProdutoNome(lote.getProduto().getProdutoNome());
        return dto;
    }

    @Transactional
    public LoteFabricacaoResponseDto criar(LoteFabricacaoRequestDto request) {
        // Valida se o produto existe
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        // Valida se o código do lote já existe
        if (loteRepository.existsByCodigoLote(request.getCodigoLote())) {
            throw new IllegalArgumentException("Código de lote já cadastrado: " + request.getCodigoLote());
        }

        // Cria o lote
        LoteFabricacao lote = modelMapper.map(request, LoteFabricacao.class);
        lote.setId(null); // garante que é sempre um INSERT, nunca um UPDATE acidental
        lote.setProduto(produto);

        LoteFabricacao salvo = loteRepository.save(lote);
        return toResponseDto(salvo);
    }

    /**
     * Atualiza informações do lote (código, datas, observação)
     * NÃO atualiza quantidadeFabricada/quantidadeDisponivel diretamente -
     * isso é feito por movimentações/consumo
     */
    @Transactional
    public LoteFabricacaoResponseDto atualizar(UUID id, LoteFabricacaoRequestDto request) {
        LoteFabricacao lote = loteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado."));

        // Se o código do lote mudou, valida que o novo código não está em uso por outro lote
        if (!lote.getCodigoLote().equals(request.getCodigoLote())
                && loteRepository.existsByCodigoLote(request.getCodigoLote())) {
            throw new IllegalArgumentException("Código de lote já cadastrado: " + request.getCodigoLote());
        }

        lote.setCodigoLote(request.getCodigoLote());
        lote.setDataFabricacao(request.getDataFabricacao());
        lote.setDataValidade(request.getDataValidade());
        lote.setObservacao(request.getObservacao());
        // quantidadeFabricada e quantidadeDisponivel NÃO são alterados aqui de propósito

        LoteFabricacao atualizado = loteRepository.save(lote);
        return toResponseDto(atualizado);
    }

    @Transactional(readOnly = true)
    public List<LoteFabricacaoResponseDto> listarTodos() {
        return loteRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoteFabricacaoResponseDto buscarPorId(UUID id) {
        LoteFabricacao lote = loteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado."));

        return toResponseDto(lote);
    }

    @Transactional(readOnly = true)
    public List<LoteFabricacaoResponseDto> buscarPorProduto(UUID produtoId) {
        return loteRepository.findByProdutoId(produtoId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoteFabricacaoResponseDto> buscarLotesProximosVencimento(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);

        return loteRepository.findLotesProximosVencimento(dataLimite)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Consome quantidade de um lote específico (usado nas movimentações)
     */
    @Transactional
    public void consumirLote(UUID loteId, Integer quantidade) {
        LoteFabricacao lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado."));

        if (lote.getQuantidadeDisponivel() < quantidade) {
            throw new IllegalArgumentException("Quantidade insuficiente no lote. Disponível: "
                    + lote.getQuantidadeDisponivel());
        }

        lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() - quantidade);
        loteRepository.save(lote);
    }
}