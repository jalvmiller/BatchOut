package br.com.joaomu.view;

import java.util.Scanner;

// A aplicação parte daqui. A instãncia dessa classe é responsável pelo Menu da aplicação.
// Quando essa classe é instanciada, temos a criação de um objeto Scanner dentro da instância..
// que é "sobrescrito" pelo Scanner externo que é passado pela classe App.

// O método que exibe o Menu retorna um inteiro que será usado em um Switch.
// Esse Switch é responsável pela seleção da "operação" que será utilizada pelo usuário.
// Os outros métodos são utilitários, mas têm relação com a UI também.


public class MenuView {

    private Scanner scanner;

    public MenuView(Scanner scanner){
        this.scanner = scanner;
    }
    

    private int lerInt() {
        while (!scanner.hasNextInt()) {
            System.out.println("Informe um número inteiro: ");
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }


    public int exibirMenuPrincipal(){
        System.out.println("\n===========================================");
        System.out.println(("||             === PROTÓTIPO ===             ||"));
        System.out.println("=============================================");
        System.out.println("1 - Cadastro de questões");
        System.out.println("2 - Listagem de questões");
        System.out.println("3 - Editar questão");
        System.out.println("4 - Remover questão");
        System.out.println("5 - Buscar por matéria");
        System.out.println("6 - Buscar por dificuldade");
        System.out.println("0 - Sair");
        System.out.println("\nEscolha uma opção");

        return lerInt();
    }

    
    public String exibirMenuBusca() {
        System.out.println("\n=== Buscar Questão ===");
        System.out.println("Digite o termo para buscar: ");
        return scanner.nextLine();
    }

    public void exibirSucesso(String mensagem) {
        System.out.println("\n" + mensagem);
    }

    public void exibirErro(String mensagem) {
        System.out.println("\n" + mensagem);
    }

    public void exibirInformacao(String mensagem) {
        System.out.println("\n" + mensagem);
    }



    public void standby() {
        System.out.println("STANDBY");
        System.out.println("Pressione Enter para continuar");
        scanner.nextLine();
    }



}