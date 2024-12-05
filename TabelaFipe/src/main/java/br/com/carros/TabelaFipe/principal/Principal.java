package br.com.carros.TabelaFipe.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.carros.TabelaFipe.model.Dados;
import br.com.carros.TabelaFipe.model.Modelos;
import br.com.carros.TabelaFipe.model.Veiculo;
import br.com.carros.TabelaFipe.services.ConsumoApi;
import br.com.carros.TabelaFipe.services.ConverteDados;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();

	private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

	public void exibeMenu() {
		var menu = """
				*** OPÇÕES ***
				Carro
				Moto
				Caminhão

				Digite uma das opções para consulta:
				""";

		System.out.println(menu);
		var opcao = leitura.nextLine();
		String endereco;

		// Verifica a opção escolhida e monta a URL correspondente
		if (opcao.toLowerCase().contains("carr")) {
			endereco = URL_BASE + "carros/marcas";
		} else if (opcao.toLowerCase().contains("mot")) {
			endereco = URL_BASE + "motos/marcas";
		} else {
			endereco = URL_BASE + "caminhoes/marcas";
		}

		// Obtém os dados a partir da API
		var json = consumo.obterDados(endereco);
		System.out.println("Dados recebidos da API: " + json);
		var marcas = conversor.obterLista(json, Dados.class);
		marcas.stream()
			.sorted(Comparator.comparing(Dados::codigo))
			.forEach(System.out::println);
			
 		System.out.println("Informe o código da marca para consulta:");
		opcao = leitura.nextLine();
		endereco += "/" + opcao + "/modelos";
		json = consumo.obterDados(endereco);
		
		var modeloLista = conversor.obterDados(json, Modelos.class);
		
		System.out.println("\nModelos dessa marca: ");
		modeloLista.modelos().stream()
			.sorted(Comparator.comparing(Dados::codigo))
			.forEach(System.out::println);

		System.out.println("Digite um trecho do nome do veículo para consulta:");
		var nomeVeiculo = leitura.nextLine();
		
		List<Dados> modelosFiltrador = modeloLista.modelos().stream()
				.filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
					.collect(Collectors.toList());
		
		System.out.println("\nModelos filtrados");
		modelosFiltrador.forEach(System.out::println);
		
		System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação: ");
		var codigoModelo = leitura.nextLine();
		
		endereco += "/" + codigoModelo + "/anos";
		json = consumo.obterDados(endereco);
		List<Dados> anos = conversor.obterLista(json, Dados.class);
		List<Veiculo> veiculos = new ArrayList<>();
		
		for (int i = 0; i < anos.size(); i++) {
			var enderecoAnos = endereco + "/" + anos.get(i).codigo();
			json = consumo.obterDados(enderecoAnos);
			Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
			veiculos.add(veiculo);
		}
		
		System.out.println("\nTodos os veiculos filtrados com avaliacao por ano:");
		veiculos.forEach(System.out::println);
	}
}
