package br.com.artesanatoestoque.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.artesanatoestoque.entities.EstoqueFabrica;
import br.com.artesanatoestoque.entities.EstoqueLoja;
import br.com.artesanatoestoque.repository.EstoqueFabricaRepository;
import br.com.artesanatoestoque.repository.EstoqueLojaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.artesanatoestoque.dto.ProdutoRequestDto;
import br.com.artesanatoestoque.dto.ProdutoResponseDto;
import br.com.artesanatoestoque.entities.Produto;
import br.com.artesanatoestoque.exception.RecursoNaoEncontradoException;
import br.com.artesanatoestoque.repository.ProdutoRepository;


@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private EstoqueFabricaRepository estoqueFabricaRepository;

	@Autowired
	private EstoqueLojaRepository estoqueLojaRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Transactional
	public ProdutoResponseDto criar(ProdutoRequestDto request) {

		// Valida se o código de barras já existe
        if (produtoRepository.existsByCodigoBarras(request.getCodigoBarras())) {
            throw new IllegalArgumentException("Código de barras '" + request.getCodigoBarras() + "' já cadastrado!"
            );
        }

		Produto produto = modelMapper.map(request, Produto.class);
		Produto salvo = produtoRepository.save(produto);
		ProdutoResponseDto response = modelMapper.map(salvo, ProdutoResponseDto.class);
		response.setQuantidadeEstoque(calcularQuantidadeTotal(salvo.getId()));
		return response;
	}

	@Transactional(readOnly = true)
    public List<ProdutoResponseDto> listarTodos() {
        return produtoRepository.findAll()
            .stream()
            .map(produto -> {
				ProdutoResponseDto response = modelMapper.map(produto, ProdutoResponseDto.class);
				response.setQuantidadeEstoque(calcularQuantidadeTotal(produto.getId()));
				return response;
			})
			.collect(Collectors.toList());
    }

	@Transactional(readOnly = true)
	public ProdutoResponseDto buscarPorId(UUID id) {
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
		ProdutoResponseDto response = modelMapper.map(produto, ProdutoResponseDto.class);
		response.setQuantidadeEstoque(calcularQuantidadeTotal(produto.getId()));
		return response;
	}


	@Transactional
	public ProdutoResponseDto atualizar(UUID id, ProdutoRequestDto request) {
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

		// Valida código de barras duplicado (exceto o próprio produto)
        if (!produto.getCodigoBarras().equals(request.getCodigoBarras())
            && produtoRepository.existsByCodigoBarras(request.getCodigoBarras())) {
            throw new IllegalArgumentException("Código de barras '" + request.getCodigoBarras() + "' já cadastrado!"
            );
        }
		modelMapper.map(request, produto);
		 Produto atualizado = produtoRepository.save(produto);

		ProdutoResponseDto response = modelMapper.map(atualizado, ProdutoResponseDto.class);
		response.setQuantidadeEstoque(calcularQuantidadeTotal(atualizado.getId()));
		return response;
	}

	@Transactional
	public void deletar(UUID id) {
		if (!produtoRepository.existsById(id)) {
			throw new RecursoNaoEncontradoException("Produto não encontrado para exclusão" + id);
		}
		produtoRepository.deleteById(id);
	}

	private Integer calcularQuantidadeTotal(UUID produtoId) {
		int quantidadeFabrica = estoqueFabricaRepository.findByProdutoId(produtoId)
				.map(EstoqueFabrica::getQuantidadeDisponivel)
				.orElse(0);

		int quantidadeLoja = estoqueLojaRepository.findByProdutoId(produtoId)
				.map(EstoqueLoja::getQuantidadeDisponivel)
				.orElse(0);

		return quantidadeFabrica + quantidadeLoja;
	}
}