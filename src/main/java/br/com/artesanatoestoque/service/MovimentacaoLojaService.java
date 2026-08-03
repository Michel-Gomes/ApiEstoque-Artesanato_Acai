package br.com.artesanatoestoque.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.artesanatoestoque.dto.MovimentacaoLojaRequestDto;
import br.com.artesanatoestoque.dto.MovimentacaoLojaResponseDto;
import br.com.artesanatoestoque.entities.EstoqueLoja;
import br.com.artesanatoestoque.entities.LoteFabricacao;
import br.com.artesanatoestoque.entities.MovimentacaoLoja;
import br.com.artesanatoestoque.entities.Produto;
import br.com.artesanatoestoque.enums.TipoMovimentacaoEnum;
import br.com.artesanatoestoque.repository.EstoqueLojaRepository;
import br.com.artesanatoestoque.repository.LoteFabricacaoRepository;
import br.com.artesanatoestoque.repository.MovimentacaoLojaRepository;
import br.com.artesanatoestoque.repository.ProdutoRepository;


@Service
public class MovimentacaoLojaService {

	@Autowired
	private MovimentacaoLojaRepository movimentacaoRepository;

	@Autowired
	private EstoqueLojaRepository estoqueLojaRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private LoteFabricacaoRepository loteFabricacaoRepository;

	@Autowired
	private EstoqueLojaService estoqueLojaService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Converte a entidade para o DTO de resposta, incluindo campos derivados
	 * de relacionamentos (produtoNome, dados do lote quando existir).
	 */
	private MovimentacaoLojaResponseDto toResponseDto(MovimentacaoLoja movimentacao) {
		MovimentacaoLojaResponseDto dto = modelMapper.map(movimentacao, MovimentacaoLojaResponseDto.class);
		dto.setProdutoId(movimentacao.getProduto().getId());
		dto.setProdutoNome(movimentacao.getProduto().getProdutoNome());
		dto.setUnidadeMedida(movimentacao.getProduto().getUnidadeMedida());

		if (movimentacao.getEstoqueLoja() != null) {
			dto.setEstoqueLojaId(movimentacao.getEstoqueLoja().getId());
		}

		if (movimentacao.getLoteFabricacao() != null) {
			dto.setLoteFabricacaoId(movimentacao.getLoteFabricacao().getId());
			dto.setCodigoLote(movimentacao.getLoteFabricacao().getCodigoLote());
			dto.setDataValidadeLote(movimentacao.getLoteFabricacao().getDataValidade());
		}

		return dto;
	}

	/**
	 * Registra uma movimentação na loja
	 */
	@Transactional
	public MovimentacaoLojaResponseDto registrarMovimentacao(MovimentacaoLojaRequestDto request) {
		// Valida produto
		Produto produto = produtoRepository.findById(request.getProdutoId())
				.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

		// Busca estoque da loja
		EstoqueLoja estoqueLoja = estoqueLojaRepository.findByProdutoId(request.getProdutoId())
				.orElseThrow(() -> new IllegalArgumentException(
						"Estoque não encontrado para este produto na loja. Crie o estoque primeiro."));

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
		processarMovimentacao(estoqueLoja, request);

		// Cria a movimentação
		MovimentacaoLoja movimentacao = modelMapper.map(request, MovimentacaoLoja.class);
		movimentacao.setId(null); // garante que e sempre um INSERT
		movimentacao.setProduto(produto);
		movimentacao.setEstoqueLoja(estoqueLoja);
		movimentacao.setLoteFabricacao(lote);
		movimentacao.setDataMovimentacao(LocalDateTime.now());

		MovimentacaoLoja salva = movimentacaoRepository.save(movimentacao);
		return toResponseDto(salva);
	}

	/**
	 * Processa a movimentação e atualiza o estoque
	 */
	private void processarMovimentacao(EstoqueLoja estoqueLoja, MovimentacaoLojaRequestDto request) {
		switch (request.getTipoMovimentacao()) {
			case ENTRADA:
				estoqueLojaService.adicionarQuantidade(request.getProdutoId(), request.getQuantidade());
				break;

			case TRANSFERENCIA_RECEBIDA:
				estoqueLojaService.adicionarQuantidade(request.getProdutoId(), request.getQuantidade());
				break;

			case SAIDA:
				if (estoqueLoja.getQuantidadeDisponivel() < request.getQuantidade()) {
					throw new IllegalArgumentException(
							"Quantidade insuficiente em estoque. Disponível: " + estoqueLoja.getQuantidadeDisponivel());
				}
				estoqueLojaService.removerQuantidade(request.getProdutoId(), request.getQuantidade());
				break;

			case AJUSTE_INVENTARIO:
				int quantidadeAtual = estoqueLoja.getQuantidadeDisponivel();
				int novaQuantidade = quantidadeAtual + request.getQuantidade();

				if (novaQuantidade < 0) {
					throw new IllegalArgumentException(
							"Ajuste resultaria em quantidade negativa. Atual: " + quantidadeAtual);
				}

				estoqueLoja.setQuantidadeDisponivel(novaQuantidade);
				estoqueLojaRepository.save(estoqueLoja);
				break;

			default:
				throw new IllegalArgumentException("Tipo de movimentação inválido: " + request.getTipoMovimentacao());
		}
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoLojaResponseDto> listarTodas() {
		return movimentacaoRepository.findAll().stream()
				.sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao()))
				.map(this::toResponseDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public MovimentacaoLojaResponseDto buscarPorId(UUID id) {
		MovimentacaoLoja movimentacao = movimentacaoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Movimentação não encontrada."));

		return toResponseDto(movimentacao);
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoLojaResponseDto> buscarHistoricoPorProduto(UUID produtoId) {
		if (!produtoRepository.existsById(produtoId)) {
			throw new IllegalArgumentException("Produto não encontrado.");
		}

		return movimentacaoRepository.findByProdutoId(produtoId).stream()
				.sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao()))
				.map(this::toResponseDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoLojaResponseDto> buscarPorTipo(TipoMovimentacaoEnum tipo) {
		return movimentacaoRepository.findByTipoMovimentacao(tipo).stream()
				.sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao()))
				.map(this::toResponseDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoLojaResponseDto> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
		if (inicio.isAfter(fim)) {
			throw new IllegalArgumentException("Data de início deve ser anterior à data fim.");
		}

		return movimentacaoRepository.findByDataMovimentacaoBetween(inicio, fim).stream()
				.sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao()))
				.map(this::toResponseDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoLojaResponseDto> buscarMovimentacoesHoje() {
		LocalDateTime inicioHoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		LocalDateTime fimHoje = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

		return buscarPorPeriodo(inicioHoje, fimHoje);
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoLojaResponseDto> buscarUltimas(int quantidade) {
		return movimentacaoRepository.findAll().stream()
				.sorted((m1, m2) -> m2.getDataMovimentacao().compareTo(m1.getDataMovimentacao())).limit(quantidade)
				.map(this::toResponseDto)
				.collect(Collectors.toList());
	}
}