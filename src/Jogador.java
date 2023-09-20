

import java.util.Random;
import java.util.Scanner;

public abstract class Jogador 
{
    protected int casaAtual;
    protected String nomeJogador;
    protected int rodadas;
    protected boolean dadosIguais;
    protected int vezDeJogar;

    Random rng = new Random();
    Scanner scan = new Scanner(System.in);

    protected Jogador(String nomeJogador, int vezDeJogar)
    {
        this.nomeJogador = nomeJogador;
        this.vezDeJogar = vezDeJogar;
    }    

    
    public abstract String toString();

    public abstract int rolarDados();
}
