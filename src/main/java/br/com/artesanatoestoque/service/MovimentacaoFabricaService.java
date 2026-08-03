package br.com.artesanatoestoque.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.artesanatoestoque.dto.MovimentacaoFabricaRequestDto;
import br.com.artesanatoestoque.dto.MovimentacaoFabricaResponseDto;
import br.com.artesanatoestoque.dto.MovimentacaoLojaRequestDto;
import br.com.artesanatoestoque.entities.EstoqueFabrica;
import br.com.artesanatoestoque.entities.LoteFabricacao;
import br.com.artesanatoestoque.entities.MovimentacaoFabrica;
import br.com.artesanatoestoque.entities.Produto;
import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;

import br.com.artesanatoestoque.repository.EstoqueFabricaRepository;
import br.com.artesanatoestoque.repository.LoteFabricacaoRepository;
import br.com.artesanatoestoque.repository.MovimentacaoFabricaRepository;
import br.com.artesanatoestoque.repository.ProdutoRepository;


@Service
public class MovimentacaoFabricaService {

    @Autowired
    private MovimentacaoFabricaRepository movimentacaoRepository;

    @Autowired
    private EstoqueFabricaRepository estoqueFabricaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private LoteFabricacaoRepository loteFabricacaoRepository;

    @Autowired
    private EstoqueFabricaService estoqueFabricaService;

    @Autowired
    private MovimentacaoLojaService movimentacaoLojaService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Converte a entidade para o DTO de resposta, incluindo campos derivados
     * de relacionamentos (produtoNome, unidadeMedida, codigoLote).
     */
    private MovimentacaoFabricaResponseDto toResponseDto(MovimentacaoFabrica movimentacao) {
        MovimentacaoFabricaResponseDto dto = modelMapper.map(movimentacao, MovimentacaoFabricaResponseDto.class);
        dto.setProdutoNome(movimentacao.getProduto().getProdutoNome());
        dto.setUnidadeMedida(movimentacao.getProduto().getUnidadeMedida());
        if (movimentacao.getLoteFabricacao() != null) {
            dto.setCodigoLote(movimentacao.getLoteFabricacao().getCodigoLote());
        }
        return dto;
    }

    /**
     * Registra uma movimentação na fábrica
     */
    @Transactional
    public MovimentacaoFabricaResponseDto registrarMovimentacao(MovimentacaoFabricaRequestDto request) {
        // Valida produto
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        // Busca estoque da fábrica
        EstoqueFabrica estoqueFabrica = estoqueFabricaRepository.findByProdutoId(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estoque não encontrado para este produto na fábrica. Crie o estoque primeiro."
                ));

        // Valida quantidade
        if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        // Busca lote se informado
        LoteFabricacao lote = null;
        if (request.getLoteFabricacaoId() != null) {
            lote = loteFabricacaoRepository.findById(request.getLoteFabricacaoId())
                    .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado."));
        }

        // Processa a movimentação baseado no tipo
        processarMovimentacao(estoqueFabrica, request, lote, produto);

        // Cria a movimentação
        MovimentacaoFabrica movimentacao = new MovimentacaoFabrica();
        movimentacao.setProduto(produto);
        movimentacao.setEstoqueFabrica(estoqueFabrica);
        movimentacao.setQuantidade(request.getQuantidade());
        movimentacao.setTipoMovimentacao(request.getTipoMovimentacao());
        movimentacao.setLoteFabricacao(lote);
        movimentacao.setObservacao(request.getObservacao());
        movimentacao.setUsuarioId(request.getUsuarioId());
        movimentacao.setUsuarioNome(request.getUsuarioNome());
        movimentacao.setDataMovimentacao(LocalDateTime.now());
        movimentacaoRepository.save(movimentacao);
        return toResponseDto(movimentacao);
    }

    /**
     * Processa a movimentação e atualiza o estoque
     */
    private void processarMovimentacao(EstoqueFabrica estoqueFabrica,
                                       MovimentacaoFabricaRequestDto request,
                                       LoteFabricacao lote,
                                       Produto produto) {
        switch (request.getTipoMovimentacao()) {
            case ENTRADA:
            case PRODUCAO:
                estoqueFabricaService.adicionarQuantidade(request.getProdutoId(), request.getQuantidade());

                if (lote != null) {
                    lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() + request.getQuantidade());
                    loteFabricacaoRepository.save(lote);
                }
                break;

            case SAIDA:
                if (estoqueFabrica.getQuantidadeDisponivel() < request.getQuantidade()) {
                    throw new IllegalArgumentException(
                            "Quantidade insuficiente em estoque. Disponível: " + estoqueFabrica.getQuantidadeDisponivel()
                    );
                }
                estoqueFabricaService.removerQuantidade(request.getProdutoId(), request.getQuantidade());

                if (lote != null) {
                    if (lote.getQuantidadeDisponivel() < request.getQuantidade()) {
                        throw new IllegalArgumentException(
                                "Quantidade insuficiente no lote. Disponível: " + lote.getQuantidadeDisponivel()
                        );
                    }
                    lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() - request.getQuantidade());
                    loteFabricacaoRepository.save(lote);
                }
                break;

            case TRANSFERENCIA_ENVIADA:
                if (estoqueFabrica.getQuantidadeDisponivel() < request.getQuantidade()) {
                    throw new IllegalArgumentException(
                            "Quantidade insuficiente em estoque. Disponível: " + estoqueFabrica.getQuantidadeDisponivel()
                    );
                }
                estoqueFabricaService.removerQuantidade(request.getProdutoId(), request.getQuantidade());

                if (lote != null) {
                    if (lote.getQuantidadeDisponivel() < request.getQuantidade()) {
                        throw new IllegalArgumentException(
                                "Quantidade insuficiente no lote. Disponível: " + lote.getQuantidadeDisponivel()
                        );
                    }
                    lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() - request.getQuantidade());
                    loteFabricacaoRepository.save(lote);
                }

                MovimentacaoLojaRequestDto transferenciaLoja = MovimentacaoLojaRequestDto.builder()
                        .produtoId(request.getProdutoId())
                        .loteFabricacaoId(lote != null ? lote.getId() : null)  // NOVO
                        .tipoMovimentacao(TipoMovimentacaoEnum.TRANSFERENCIA_RECEBIDA)
                        .unidadeMedida(produto.getUnidadeMedida())
                        .quantidade(request.getQuantidade())
                        .observacao("Transferência recebida da fábrica" +
                                (lote != null ? " — Lote: " + lote.getCodigoLote() : ""))
                        .usuarioId(request.getUsuarioId())
                        .usuarioNome(request.getUsuarioNome())
                        .build();

                movimentacaoLojaService.registrarMovimentacao(transferenciaLoja);
                break;

            case AJUSTE_INVENTARIO:
                int quantidadeAtual = estoqueFabrica.getQuantidadeDisponivel();
                int novaQuantidade = quantidadeAtual + request.getQuantidade();

                if (novaQuantidade < 0) {
                    throw new IllegalArgumentException(
                            "Ajuste resultaria em quantidade negativa. Atual: " + quantidadeAtual
                    );
                }

                estoqueFabrica.setQuantidadeDisponivel(novaQuantidade);
                estoqueFabricaRepository.save(estoqueFabrica);
                break;

            default:
                throw new IllegalArgumentException("Tipo de movimentação inválido: " + request.getTipoMovimentacao());
        }
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoFabricaResponseDto> listarTodas() {
        return movimentacaoRepository.findAll()
                .stream()
                .sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao()))
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovimentacaoFabricaResponseDto buscarPorId(UUID id) {
        MovimentacaoFabrica movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movimentação não encontrada."));

        return toResponseDto(movimentacao);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoFabricaResponseDto> buscarHistoricoPorProduto(UUID produtoId) {
        if (!produtoRepository.existsById(produtoId)) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }

        return movimentacaoRepository.findHistoricoPorProduto(produtoId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoFabricaResponseDto> buscarPorLote(UUID loteId) {
        if (!loteFabricacaoRepository.existsById(loteId)) {
            throw new IllegalArgumentException("Lote não encontrado.");
        }

        return movimentacaoRepository.findByLoteFabricacaoId(loteId)
                .stream()
                .sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao()))
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoFabricaResponseDto> buscarPorTipo(TipoMovimentacaoEnum tipo) {
        return movimentacaoRepository.findByTipoMovimentacao(tipo)
                .stream()
                .sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao()))
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoFabricaResponseDto> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data de início deve ser anterior à data fim.");
        }

        return movimentacaoRepository.findByPeriodo(inicio, fim)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoFabricaResponseDto> buscarMovimentacoesHoje() {
        LocalDateTime inicioHoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fimHoje = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        return buscarPorPeriodo(inicioHoje, fimHoje);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoFabricaResponseDto> buscarUltimas(int quantidade) {
        return movimentacaoRepository.findAll()
                .stream()
                .sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao()))
                .limit(quantidade)
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}