package com.ufcg.psoft.mercadofacil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class VolatilLoteRepositoryTest {

	@Autowired
	VolatilLoteRepository driver;

	Lote lote;
	Lote resultado;
	Produto produto;
	Lote loteAlternativo;

	@BeforeEach
	void setup() {
		produto = Produto.builder()
				.id(1L)
				.nome("Produto Base")
				.codigoBarra("123456789")
				.fabricante("Fabricante Base")
				.preco(125.36)
				.build();
		lote = Lote.builder()
				.id(1L)
				.numeroDeItens(100)
				.produto(produto)
				.build();
		loteAlternativo = Lote.builder()
				.id(1L)
				.numeroDeItens(200)
				.produto(produto)
				.build();
		driver = new VolatilLoteRepository();
	}

	@AfterEach
	void tearDown() {
		produto = null;
		driver.deleteAll();
	}

	@Test
	@DisplayName("Adicionar o primeiro Lote no repositorio de dados")
	void salvarPrimeiroLote() {
		resultado = driver.save(lote);

		assertEquals(driver.findAll().size(), 1);
		assertEquals(resultado.getId().longValue(), lote.getId().longValue());
		assertEquals(resultado.getProduto(), produto);
	}

	@Test
	@DisplayName("Adicionar o segundo Lote (ou posterior) no repositorio de dados")
	void salvarSegundoLoteOuPosterior() {
		Produto produtoExtra = Produto.builder()
				.id(2L)
				.nome("Produto Extra")
				.codigoBarra("987654321")
				.fabricante("Fabricante Extra")
				.preco(125.36)
				.build();
		Lote loteExtra = Lote.builder()
				.id(2L)
				.numeroDeItens(100)
				.produto(produtoExtra)
				.build();
		driver.save(lote);

		resultado = driver.save(loteExtra);

		assertEquals(driver.findAll().size(), 2);
		assertEquals(resultado.getId().longValue(), loteExtra.getId().longValue());
		assertEquals(resultado.getProduto(), produtoExtra);
	}

	@Test
	@DisplayName("Adcionar lote com mesmo identificador")
	void testeLoteComMesmoIdentificador() {
		Produto produtoAlternativo = Produto.builder()
				.id(2L)
				.nome("Produto Alternativo")
				.codigoBarra("987654321")
				.fabricante("Fabricante Extra")
				.preco(125.36)
				.build();

		Lote resultado_x = driver.save(lote);
		Lote resultado_y = driver.save(loteAlternativo);
		
		// Para fins de identificação que deveria ser pelo ID não deveria aceitar
		// lote com o mesmo ID. Algo que está acontecendo.
		assertEquals(driver.findAll().size(), 2);
	}
	
	@Test
	@DisplayName("Teste de procurar o Lote")
	void testFindRetornoDeLote() {
		resultado = driver.save(lote);
		assertEquals(driver.findAll().size(), 1);
		/*
		 * Forma que esse teste, em específico, poderia funcionar:
		 * int pos = 0;
		 * Long n = Long.valueOf(pos);
		 * assertEquals(driver.find(n), resultado);
		*/
		// Esse teste da erro, pois a coleção usada para armazenamento
		// não posusui como chave o ID ou o parseInt("" + id) como chave.
		assertEquals(driver.find(resultado.getId().longValue()), resultado);
	}
	
	@Test
	@DisplayName("Teste o retorno de todos os Lotes armazenados")
	void testFindAll() {
		driver.save(lote);
		driver.save(loteAlternativo);
		
		List lista = new ArrayList<Lote>();
		lista.add(lote);
		lista.add(loteAlternativo);
		assertEquals(driver.findAll(), lista);
	}
	
	@Test
	@DisplayName("Teste: Atualizar um lote, com lista de lotes tamanho um.")
	void testUpdate() {
		driver.save(lote);
		driver.update(loteAlternativo);
		int pos = 0;
		Long n = Long.valueOf(pos);
		assertEquals(driver.findAll().size(), 1);
		assertEquals(driver.find(n), loteAlternativo);
	}
	
	@Test
	@DisplayName("Teste: Atualizar um lote, com lista de lotes tamanho dois ou mais.")
	void testUpdateLotesTamanhoMaiorQueDois() {
		driver.save(lote);
		driver.save(loteAlternativo);
		driver.update(loteAlternativo);
		// O método de atualizar está apagando todos os lotes e adicionando
		// apenas o lote recebido.
		assertEquals(driver.findAll().size(), 2);
	}
	
	@Test
	@DisplayName("Teste: Deletar um lote da coleção de lotes.")
	void testDelete() {
		driver.save(lote);
		driver.save(loteAlternativo);
		driver.delete(lote);
		// O método deveria apagar apenas o lote passado.
		assertEquals(1, driver.findAll().size());
	}
	
	@Test
	@DisplayName("Teste: Deletar todos os lotes na coleção")
	void testDeleteAll() {
		driver.save(lote);
		driver.save(loteAlternativo);
		driver.deleteAll();
		assertEquals(0, driver.findAll().size());
	}
}
