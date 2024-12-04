package br.com.carros.TabelaFipe.principal;

import java.util.Comparator;
import java.util.Scanner;

import br.com.carros.TabelaFipe.model.Dados;
import br.com.carros.TabelaFipe.model.Modelos;
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
		
		System.out.println("Modelos dessa marca: ");
		modeloLista.modelos().stream()
			.sorted(Comparator.comparing(Dados::codigo))
			.forEach(System.out::println);

		System.out.println("Digite um trecho do nome do veículo para consulta:");

	}
}
