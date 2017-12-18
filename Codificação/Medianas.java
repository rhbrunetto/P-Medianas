import java.util.*;
import java.io.*;

/**
Classe principal
Modela a entrada em um grafo e exibe a solução
*/
class Main{
  public static Grafo GRAFO;
  private static int a;

  public static void main(String[] args){
    a = -1;
    int numeroMedianas = 0;
    Scanner scanner = new Scanner(System.in);

    /*Primeira Linha*/
    if(scanner.hasNextLine()){
      String string = scanner.nextLine();
      String[] str = string.split(" ");
      // qtdVertices = Integer.valueOf(str[0]);
      qtdMedianas = Integer.valueOf(str[1]);
    }else{
      System.out.println("ENTRADA INVÁLIDA!");
      return;
    }

    GRAFO = getInput(scanner);

    int tamanhoPopulacao = (int) (GRAFO.vertices.size() * (3/10)); /*30% da quantidade de vertices*/
    int numeroGeracoes = (int) (GRAFO.vertices.size() / numeroMedianas); /*vertices/medianas = quantidade de iterações*/
    double taxaDeMutacao;
    RunTestes rt = new RunTestes(tamanhoPopulacao, numeroGeracoes, numeroMedianas, taxaDeMutacao);
  }

  public static Grafo getInput(Scanner scanner){
    QTDVERTICES
    QTD MEDIANAS
    Grafo grafo = new Grafo();

    /*Demais Linhas*/
    while(scanner.hasNextLine()){
      String string = scanner.nextLine();
      if(string.isEmpty()) break;
      String[] str = string.split(" ");
      grafo.vertices.add(new Vertice(Integer.valueOf(str[0]), Integer.valueOf(str[1]), Integer.valueOf(str[2]), Integer.valueOf(str[3])));
    }

    return grafo;
  }

  public static int getId(){
    a = a + 1;
    return a;
  }
}

/**
Classe que roda um caso de teste
Recebe os parâmetros e submete aos métodos
*/
class RunTestes{
  int tamanhoPopulacao;
  int numeroGeracoes;
  int numeroMedianas;
  double taxaDeMutacao;

  PriorityQueue<Solucao> populacao;

  public RunTestes(int tamanhoPopulacao, int numeroGeracoes, int numeroMedianas, double taxaDeMutacao){
    this.tamanhoPopulacao = tamanhoPopulacao;
    this.numeroGeracoes = numeroGeracoes;
    this.numeroMedianas = numeroMedianas;
    this.taxaDeMutacao = taxaDeMutacao;

    this.populacao = gerarPopulacaoInicial();
  }

  public static executar(){

  }

  public static PriorityQueue<Solucao> gerarPopulacaoInicial(){
    PriorityQueue<Solucao> pop;
    int k=0;
    while(k < this.tamanhoPopulacao){
      pop.add(gerarSolucaoRandomica());
      k++;
    }
    return pop;
  }

  //TODO: MEDIANA TAMBÉM DEVE SUPRIR SUA PRÓPRIA NECESSIDADE
  public static Solucao gerarSolucaoRandomica(){
    Solucao s = new Solucao();
    int i = 0;
    while(i < this.numeroMedianas){
      int randomVal; //TODO: Gerar entre 0 e s.grafo.vertices.size()
      if(s.medianas.exists(s.grafo.vertices.get(randomVal))) continue;
      s.inserirVertice(s.grafo.vertices.get(randomVal));
      i++;
    }
    s.calcularCusto();
    return s;
  }

  public static double calcularDistancia(Vertice v1, Vertice v2){
    return (Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2)));
  }


  class Solucao{
    Grafo grafo;
    ArrayList<Vertice> medianas;
    PriorityQueue<Vertice> prioridades;
    double custo;

    public Solucao(){
      this.grafo = Main.GRAFO.copiar();
    }

    public void inserirVertice(Vertice v){
      medianas.add(v);
      v.capacidade -= v.demanda;
    }

    public void calcularCusto(){
      double menorDist = -1, secMenorDist = -1;
      for(Vertice v : grafo.vertices){
        for(Vertice med : medianas){
          if(v.equals(med)) continue;
          double dist = calcularDistancia(v, med);
          if(dist < menorDist){
            secMenorDist = menorDist;
            menorDist = dist;
            v.setCandidatoSecundario(v.medianaCandidata1, v.distanciaMed1);
            v.setCandidatoPrimario(med, menorDist);

          }else if(dist < secMenorDist){
            secMenorDist = dist;
            v.setCandidatoSecundario(med, secMenorDist);
          }
        }
        v.setDifDistancia(secMenorDist - menorDist);
        prioridades.add(v);
      }
      while(!prioridades.isEmpty()){
        Vertice v_priori = prioridades.poll();
        if(!v_priori.medianaCandidata1.isCheia())
        v_priori.associarMediana(v_priori.medianaCandidata1, v_priori.distanciaMed1);
        else if(!v_priori.medianaCandidata2.isCheia())
        v_priori.associarMediana(v_priori.medianaCandidata2, v_priori.distanciaMed2);
        else{
          Vertice v_mediana = null;
          double n_menorDist = -1;
          for(Vertice med : medianas){
            if(med.isCheia()) continue;
            double dist = calcularDistancia(v_priori, med);
            if(dist < n_menorDist){
              v_mediana = med;
              n_menorDist = dist;
            }
          }
          v_priori.associarMediana(v_mediana, n_menorDist);
        }
      }
    }
  }
}


class Vertice implements Comparable<Vertice>{
  int id, x,y, demanda, capacidade;
  Vertice medianaAssociada, medianaCandidata1, medianaCandidata2;
  double distancia, distanciaMed1, distanciaMed2, difDistancia;

  public Vertice(int x, int y, int demanda, int capacidade){
    this.id = Main.getId();
    this.x = x;
    this.y = y;
    this.demanda = demanda;
    this.capacidade = capacidade;
  }

  public void associarMediana(Vertice med, double dist){
    med.capacidade = med.capacidade - 1;
    this.medianaAssociada = med;
    this.distancia = dist;
  }

  public boolean isCheia(){
    return (this.capacidade == 0);
  }

  public void setDifDistancia(double dif){
    this.difDistancia = dif;
  }

  public void setCandidatoPrimario(Vertice v, double d1){
    this.medianaCandidata1 = v;
    this.distanciaMed1 = d1;
  }

  public void setCandidatoSecundario(Vertice v, double d2){
    this.medianaCandidata2 = v;
    this.distanciaMed2 = d2;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Vertice other = (Vertice) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(Vertice o1) {
    double dif =  this.difDistancia - o1.difDistancia;
    if(dif > 0) return -1;
    if(dif < 0) return 1;
    return 0;
  }

}

class Grafo{
  ArrayList<Vertice> vertices;

  public Grafo(){
    vertices = new ArrayList();
  }

  //TODO: Implementar
  public Grafo copiar(){
    return null;
  }
}
