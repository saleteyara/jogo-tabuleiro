

public class JogadorNormal extends Jogador
{   
    protected JogadorNormal(String nomeJogador, int vezDeJogar)
    {
        super(nomeJogador, vezDeJogar);
        this.dadosIguais = false;
        this.rodadas = 0;
        this.casaAtual = 0;
    }  

    protected JogadorNormal(String nomeJogador, int vezDeJogar, int casaAtual)
    {
        super(nomeJogador, vezDeJogar);
        this.dadosIguais = false;
        this.rodadas = 0;
        this.casaAtual = casaAtual;
    } 

    protected JogadorNormal(String nomeJogador, int vezDeJogar, int casaAtual, int rodadas)
    {
        super(nomeJogador, vezDeJogar);
        this.dadosIguais = false;
        this.rodadas = rodadas;
        this.casaAtual = casaAtual;
    } 

    public int rolarDados()
    {
        int dado1 = rng.nextInt(6) + 1;     
        int dado2 = rng.nextInt(6) + 1;

        this.dadosIguais = (dado1 == dado2);
        return (dado1 + dado2); 
    }

    @Override
    public String toString()
    {
        return "Jogador = " + nomeJogador.charAt(0) + "|" + "Casa Atual = " + casaAtual + "|" + "Sorte = " + "Normal" + "|" + "Rodadas = " + rodadas;
    }

}
