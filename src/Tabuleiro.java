import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;

import java.util.Random;

public class Tabuleiro 
{
    protected ArrayList<Jogador> jogadores = new ArrayList<Jogador>();
    ArrayList<Jogador> presos = new ArrayList<Jogador>(); 

    protected static boolean continuarJogo = true;
    protected static int ordemDoJogo = 1;

    public static final String RESET = "\u001B[0m", RED = "\u001B[31m", GREEN = "\u001B[32m",
    YELLOW = "\u001B[33m",BLUE = "\u001B[34m", PURPLE = "\u001B[35m", CYAN = "\u001B[36m", WHITE_BOLD = "\033[1;97m",
    RED_BACKGROUND = "\u001B[41m", GREEN_BACKGROUND = "\u001B[42m", YELLOW_BACKGROUND = "\u001B[43m",
    BLUE_BACKGROUND = "\u001B[44m", PURPLE_BACKGROUND = "\u001B[45m",CYAN_BACKGROUND = "\u001B[46m",WHITE_BACKGROUND = "\u001B[47m";

    Scanner scan = new Scanner(System.in);
    Random rng = new Random();

    public void iniciarJogo()
    {
        adicionarJogadores();
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Pressione qualquer botao para iniciar o jogo!");
        scan.nextLine();
        clearScreen();

        while(continuarJogo)
        {
            iniciarRodada();
        }

        printTabuleiro();
        System.out.println("----------------------------------------------------------------------");
        System.out.print("\n" + CYAN + "O Jogo Terminou!\n" + RESET);
        System.out.print("\n" + GREEN + "Parabens " + jogadores.get(jogadores.size() - 1).nomeJogador + "!\nVoce foi o primeiro a chegar!\n" + RESET);
        System.out.print("\nCheque o podio abaixo:\n");
        printColocacoes();
       
    }

    public boolean adicionarJogadores()
    {
        System.out.println("Quantas pessoas vao jogar? Escolha um numero de 2 a 6:");
        String qtdJogadores = scanValido("Numero");
        ordemDoJogo = Integer.parseInt(qtdJogadores);

        boolean azarados = false, sortudos = false, normais = false;
        int i = 1;
        while(i < ordemDoJogo + 1)
        {
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Note que apenas a primeira letra do seu nome ira contar no tabuleiro!");
            System.out.println("Escolha o nome do " + i + "º jogador:");
            String nome = scanValido("Nome");
            
            boolean continuar = true;
            while(continuar)
            {
                continuar = false;
                for (Jogador jogador : jogadores) 
                {
                    if (nome.charAt(0) == jogador.nomeJogador.charAt(0))
                    {
                        System.out.println(RED + "Esse nome(ou icone) ja foi escolhido! Escolha outro:" + RESET);
                        nome = scanValido("Nome");
                        continuar = true;
                    }
                }
            }

            System.out.println("Agora escolha se voce eh (1)Sortudo (2)Azarado (3)Normal:");
            String sorte = scanValido("Sorte");

            switch (sorte) 
            {
                case "1":
                    Jogador sortudo = new JogadorSortudo(nome, i);
                    jogadores.add(sortudo);
                    sortudos = true;
                    break;
                case "2":
                    Jogador azarado = new JogadorAzarado(nome, i);
                    jogadores.add(azarado);
                    azarados = true;
                    break;            
                default:
                    Jogador normal = new JogadorNormal(nome, i);
                    jogadores.add(normal);
                    normais = true;
                    break;
            }

            i++;
        }

        boolean continuar = (azarados && normais) || (azarados && sortudos) || (sortudos && normais);
        while(!continuar)
        {
            System.out.println("----------------------------------------------------------------------");
            System.out.println(RED + "Voce deve escolher pelo menos um tipo de sorte diferente!" + RESET);
            System.out.println("Digite uma nova sorte:");
            String sorteNova = scanValido("Sorte");
            int sorte = Integer.parseInt(sorteNova);

            switch (sorte) 
            {
                case 1:
                    Jogador sortudo = new JogadorSortudo(jogadores.get(i-2).nomeJogador, jogadores.get(i-2).vezDeJogar);
                    jogadores.add(sortudo);
                    sortudos = true;
                    break;
                case 2:
                    Jogador azarado = new JogadorAzarado(jogadores.get(i-2).nomeJogador, jogadores.get(i-2).vezDeJogar);
                    jogadores.add(azarado);
                    azarados = true;
                    break;            
                default:
                    Jogador normal = new JogadorNormal(jogadores.get(i-2).nomeJogador, jogadores.get(i-2).vezDeJogar);
                    jogadores.add(normal);
                    normais = true;
                    break;
            }
            jogadores.remove(i-2);
            continuar = (azarados && normais) || (azarados && sortudos) || (sortudos && normais);
        }

        return true;
    }

    public void iniciarRodada()
    {
        int turno = 1;
        while (turno < ordemDoJogo+1 && continuarJogo) // Inicia UM turno de cada jogador
        {
            for (Jogador jogador : jogadores) // procura o jogador da vez(turno)
            {
                if(jogador.vezDeJogar == turno && continuarJogo)
                {
                    do
                    {
                        jogador.dadosIguais = false;
                        ordenar();
                        clearScreen();
                        printTabuleiro();
                        printColocacoes();
                        System.out.println(jogador.nomeJogador + ", é sua vez");
                        System.out.println("Pressione qualquer botao para jogar!");
                        scan.nextLine();
                        clearScreen();
                        
                        iniciarTurnoDO(jogador); // 

                        ordenar();
                    } while(jogador.dadosIguais && continuarJogo);  
                    
                    break;
                }
            }

            turno++;
        }
    }

    public boolean iniciarTurnoDO(Jogador jogador)
    {
        if(casaBloqueio(jogador))
        {
            return false;
        }

        // inicio do turno de verdade
        jogador.rodadas += 1;
        int soma = jogador.rolarDados(); // rolar os dados
        jogador.casaAtual += soma; // andar as casas
        ordenar(); // ordenar do jogador mais perto do inicio para o mais distante

        System.out.print(CYAN + "\n" + jogador.nomeJogador + ", voce vai andar " + soma + " casas!\n" + RESET);
        if(jogador.dadosIguais)
        {
            System.out.println(GREEN + "Eita voce tirou dados iguais! Ao fim da sua rodada sera voce de novo." + RESET);
        }

        casaSurpresa(jogador);
        casaSorte(jogador);
        casaRotorneAoInicio(jogador);
        casaMagica(jogador);
        ordenar();

        printTabuleiro();
        
        if(jogador.casaAtual >= 40)
        {
            jogador.casaAtual = 40;
            continuarJogo = false;
        }
        printColocacoes();
        
        System.out.println("Fim da sua rodada, " + jogador.nomeJogador + "!");
        System.out.println("Pressione qualquer botao para passar sua vez!");
        scan.nextLine();
        clearScreen();
        return true;
    }

    public boolean casaBloqueio(Jogador jogador)
    {
        if(jogador.casaAtual == 10 || jogador.casaAtual == 25 || jogador.casaAtual == 38) // CASA BLOQUEIO
        {
            if(presos.contains(jogador) == false) // checar se ja perdeu ou nao um turno
            {
                printTabuleiro();
                printColocacoes();

                presos.add(jogador); // colocado na prisao

                System.out.println(RED + "Esta na prisao. nao jogara esse turno... ;(" + RESET);
                System.out.println("Fim da sua rodada " + jogador.nomeJogador + "!");
                System.out.println("Pressione qualquer botao para passar sua vez!");
                scan.nextLine();
                clearScreen();                
                return true; // retorna -> turno do jogador foi pulado (esta preso) 
            }

            presos.remove(jogador); // removido da prisao por ja ter perdido um turno
        }

        return false; // retorna -> turno do jogador acontecera normalmente (ja perdeu um turno anteriormente)
    }

    public boolean casaSurpresa(Jogador jogador) 
    {
        if(jogador.casaAtual == 13)
        {
            System.out.println(PURPLE + "Voce caiu na casa surpresa!" + RESET);
            int novaSorte = rng.nextInt(3) - 1;
            
            if(novaSorte == -1)
            {
                Jogador j = new JogadorAzarado(jogador.nomeJogador, jogador.vezDeJogar, jogador.casaAtual, jogador.rodadas);
                jogadores.remove(jogador);
                jogadores.add(j);

                System.out.println(RED + "Sua nova sorte eh: Azarado!" + RESET);
                return true;
            }
            if(novaSorte == 1)
            {
                Jogador j = new JogadorSortudo(jogador.nomeJogador, jogador.vezDeJogar, jogador.casaAtual, jogador.rodadas);
                jogadores.remove(jogador);
                jogadores.add(j);

                System.out.println(GREEN + "Sua nova sorte eh: Sortudo!" + RESET);
                return true;
            }

            Jogador j = new JogadorNormal(jogador.nomeJogador, jogador.vezDeJogar, jogador.casaAtual, jogador.rodadas);
            jogadores.remove(jogador);
            jogadores.add(j);

            System.out.println(YELLOW + "Sua nova sorte eh: Normal" + RESET);
            return true;
        }

        return false;
    }

    public boolean casaSorte(Jogador jogador)
    {
        if((jogador.casaAtual == 5 || jogador.casaAtual == 15 || jogador.casaAtual == 30))
        {      
            System.out.println(PURPLE + "Voce caiu na casa da Sorte! Ande mais 3 casas!" + RESET);
            if(jogador instanceof JogadorAzarado)
            {
                System.out.println(RED + "Ah, esquece voce é azarado... nao avance nenhuma." + RESET);
            }
            else
            {
                jogador.casaAtual += 3;
            }
        }

        return false;
    }

    public boolean casaRotorneAoInicio(Jogador jogador)
    {
        if(jogador.casaAtual != 17 && jogador.casaAtual != 27)
        {
            return false;
        }

        printTabuleiro();
        printColocacoes();
        System.out.println(PURPLE + jogador.nomeJogador + ", escolha um jogador(letra) para voltar ao inicio: " + RESET);
        String jogadorEscolhido = scan.next();

        for (Jogador p : jogadores) 
        {
            if(p.nomeJogador.charAt(0) == jogadorEscolhido.charAt(0) && p.nomeJogador.charAt(0) != jogador.nomeJogador.charAt(0))
            {
                p.casaAtual = 0;
                return true;
            }
        }
        
        System.out.println(RED + "Letra nao encontrada... Perdeu sua chance..." + RESET);
        return false;
    }

    public boolean casaMagica(Jogador jogador)
    {
        ordenar();
        if(jogador.casaAtual != 20 && jogador.casaAtual != 35)
        {
            return false;
        }
        System.out.println(PURPLE + "Voce caiu na casa magia..." + RESET + RED + "Troque de posicao com o ultimo lugar..." + RESET);
        int temporareo = jogador.casaAtual;
        jogador.casaAtual = jogadores.get(0).casaAtual;
        jogadores.get(0).casaAtual = temporareo;
        ordenar();

        return false;
    }

    public void ordenar()
    {
        for (int i = 0; i < jogadores.size(); i++) 
        {
            for (int j = i+1; j < jogadores.size(); j++) 
            {
                if(jogadores.get(i).casaAtual > jogadores.get(j).casaAtual)
                {
                    Jogador temporareo = jogadores.get(i);
                    jogadores.set(i, jogadores.get(j));
                    jogadores.set(j, temporareo);
                }
            }
        }
    }

    public void printColocacoes()
    {
        ordenar();
        System.out.println("--------------------------------------------------------------");
        System.out.println(CYAN + "Estes sao os jogadores na ordem de primeiro a ultimo:" +RESET);
        System.out.print("\n");
        for (int i = jogadores.size()-1; i != -1; i--) 
        {
            System.out.println(jogadores.size()-i + "º - " + jogadores.get(i).toString());
        }
        System.out.println("--------------------------------------------------------------");
    }

    public void printTabuleiro()
    {
        int casa = 0, inversor = 3;
        System.out.println("--------------------------------------------------------------");
        System.out.println("-----LARGADA-----"); 
        System.out.println("| " + jogadorNaCasa(0) + " |");
        casa++;
        for (int row = 0; row < 8; row++)
        {
            for (int column = 0; column < 4; column++)
            {
                if(row %2 != 0 && casa != 10 && casa != 20 && casa != 30 && casa != 40)
                {
                    System.out.print("| " + jogadorNaCasa(casa+inversor) + " ");
                    inversor -= 2;
                }
                else
                {
                    System.out.print("| " + jogadorNaCasa(casa) + " ");
                }
                casa++;
            } 
            inversor = 3;
            System.out.print("|");

            System.out.println("");
            if (row %2 == 0)
            {
                System.out.println("            | " + jogadorNaCasa(casa) + " |");
            }                
            else if(casa == 40)
            {
                System.out.println("|" + jogadorNaCasa(casa) + "|");
            }
            else
            {
                System.out.println("| " + jogadorNaCasa(casa) + " |" + "            ");
            }   
            casa++;
        }
        System.out.print("-----CHEGADA-----\n"); 
    }

    public String jogadorNaCasa(int i)
    {
        for (Jogador jogador : jogadores) 
        {
            if(i == jogador.casaAtual)
            {
                String color = WHITE_BOLD;
                switch (jogador.vezDeJogar) 
                {
                    case 1:
                        color = GREEN;
                        break;
                    case 2:
                        color = RED;
                        break;
                    case 3:
                        color = CYAN;
                        break;
                    case 4:
                        color = BLUE;
                        break;
                    case 5:
                        color = WHITE_BOLD;
                        break;
                    default:
                        color = YELLOW;
                        break;
                }
                return color + jogador.nomeJogador.charAt(0) + RESET;
            }
        }

        switch (i) {
            case 0:
                return PURPLE_BACKGROUND + WHITE_BOLD + ">" + RESET;
            case 20, 35:
                return PURPLE_BACKGROUND + WHITE_BOLD + "<" + RESET;
            case 17, 27:
                return PURPLE_BACKGROUND + WHITE_BOLD  + "0" + RESET;
            case 5, 15, 30:
                return PURPLE_BACKGROUND + WHITE_BOLD  + "+" + RESET;
            case 13:
                return PURPLE_BACKGROUND + WHITE_BOLD  + "?" + RESET;
            case 10, 25, 38:
                return PURPLE_BACKGROUND + WHITE_BOLD  + "Ø" + RESET;
            case 40:
                return PURPLE_BACKGROUND + WHITE_BOLD  + "FIM" + RESET;
            default:
                return WHITE_BACKGROUND + "-" + RESET;
        }
    }

    public String scanValido(String situacao)
    {
        String s = scan.nextLine();

        if(situacao.equals("Numero"))
        {
            while(!(s.equals("2") || s.equals("3") || s.equals("4") || s.equals("5") || s.equals("6")))
            {
                System.out.println(RED + "Digite um numero entre 2 e 6" + RESET);
                s = scan.nextLine();
            }
            return s;
        }
        else if(situacao.equals("Sorte"))
        {
            while(!(s.equals("1") || s.equals("2") || s.equals("3")))
            {
                System.out.println(RED + "Digite um numero entre 1 e 3" + RESET);
                s = scan.nextLine();
            }
            return s;
        }
        else if(situacao.equals("Nome"))
        {            
            while(s.equals(""))
            {
                System.out.println(RED + "Digite um caractere valido" + RESET);
                s = scan.nextLine();
            }
            return s;
        }

        return s;
    }

    public void clearScreen()
    {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
