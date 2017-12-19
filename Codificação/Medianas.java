import java.util.*;
import java.io.*;

/**
Classe principal
Modela a entrada em um grafo e exibe a soluÃ§Ã£o
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
      numeroMedianas = Integer.valueOf(str[1]);
    }else{
      System.out.println("ENTRADA INVALIDA!");
      return;
    }

    GRAFO = getInput(scanner);

    int tamanhoPopulacao = (int) (GRAFO.vertices.size() * (3/10)); /*30% da quantidade de vertices*/
    int numeroGeracoes = (int) (GRAFO.vertices.size() / numeroMedianas); /*vertices/medianas = quantidade de iteraÃ§Ãµes*/
    double taxaDeMutacao = 0.03; //TODO: Alterar
    double taxaDeElitismo = 0.3; //TODO: Alterar
    int quantidadeReprodutores = 10;
    RunTestes rt = new RunTestes(tamanhoPopulacao, numeroGeracoes, numeroMedianas, quantidadeReprodutores, taxaDeElitismo, taxaDeMutacao);
    rt.executar();
  }

  public static Grafo getInput(Scanner scanner){
    //int QTDVERTICES;
    //int QTDMEDIANAS;
    Grafo grafo = new Grafo();

    /*Demais Linhas*/
    while(scanner.hasNextLine()){
      String string = scanner.nextLine();
      if(string.isEmpty()) break;
      String[] str = string.split(" ");
      grafo.addVertice(new Vertice(Integer.valueOf(str[0]), Integer.valueOf(str[1]), Integer.valueOf(str[2]), Integer.valueOf(str[3])));
    }

    return grafo;
  }

  public static int getId(){
    a = a + 1;
    return a;
  }

  public static double calcularDistancia(Vertice v1, Vertice v2){
    return (Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2)));
  }

}

/**
Classe que roda um caso de teste
Recebe os parÃ¢metros e submete aos mÃ©todos
*/
class RunTestes{
  int tamanhoPopulacao;
  int numeroGeracoes;
  int numeroMedianas;
  int quantidadeReprodutores;
  double taxaDeElitismo;
  double taxaDeMutacao;

  ArrayList<Solucao> populacao;

  public RunTestes(int tamanhoPopulacao, int numeroGeracoes, int numeroMedianas, int quantidadeReprodutores, double taxaDeElitismo, double taxaDeMutacao){
    this.tamanhoPopulacao = tamanhoPopulacao;
    this.numeroGeracoes = numeroGeracoes;
    this.numeroMedianas = numeroMedianas;
    this.taxaDeElitismo = taxaDeElitismo;
    this.taxaDeMutacao = taxaDeMutacao;
    this.quantidadeReprodutores = quantidadeReprodutores;

    this.populacao = gerarPopulacaoInicial();
  }

  public double executar(){
     int k=0; double custo=0;
     while(k < this.numeroGeracoes){
       ArrayList<Solucao> reprodutores = selecionarReprodutores();
       ArrayList<Solucao> novaPopulacao = cruzar(reprodutores);
       novaPopulacao = aplicarMutacao(novaPopulacao); /*Calcula os custos (com e sem mutação)*/
       atualizarPopulacao(this.populacao, novaPopulacao);
       k++;
     }
     return custo;
  }

  /*Seleção por torneio*/
  public ArrayList<Solucao> selecionarReprodutores(){
    int i=0, k=0, randomVal;
    ArrayList<Solucao> reprodutores = new ArrayList<Solucao>();
    while(i<this.quantidadeReprodutores){
      PriorityQueue<Solucao> torneio = new PriorityQueue<Solucao>();
      while(k<this.quantidadeReprodutores){
        randomVal = 0; //TODO: implementar randomVal (entre 0 e populacao.size()-1)
        torneio.add(this.populacao.get(randomVal));
      }
      reprodutores.add(torneio.poll());
      i++;
    }
    return reprodutores;
  }

  /*Cruzamento mesclando medianas*/
  public ArrayList<Solucao> cruzar(ArrayList<Solucao> reprodutores){
    ArrayList<Solucao> novosIndividuos = new ArrayList<>();
    int randomVal = 0;
    //Cruza o melhor reprodutor com o pior
    while(reprodutores.size() > 1){
      Solucao filho = new Solucao();
      Solucao rep1 = reprodutores.get(0),
              rep2 = reprodutores.get(reprodutores.size()-1);
      for(int i=0; i<this.numeroMedianas; i++){
        Vertice m_rep1 = rep1.medianas.get(i);
        if(rep2.medianas.contains(m_rep1)) filho.inserirVertice(m_rep1);
        else{
          Vertice m_rep2 = rep2.medianas.get(i);
          //randomVal = ? TODO: Implementar randomVal
          if(randomVal % 2 == 0) filho.inserirVertice(m_rep1);
          else filho.inserirVertice(m_rep2);
        }
      }
      reprodutores.remove(rep1);
      reprodutores.remove(rep2);
      novosIndividuos.add(filho);
    }
    if(!reprodutores.isEmpty()) novosIndividuos.add(reprodutores.get(0));
    Collections.sort(novosIndividuos);
    return novosIndividuos;
  }

  /*Aplica mutação*/
  public ArrayList<Solucao> aplicarMutacao(ArrayList<Solucao> populacao){
    int quantidadeMutaveis = (int)(this.taxaDeMutacao * populacao.size()); /*Sorteia quantos serão mutados*/
    int randomVal;
    for(int i=0; i<taxaDeMutacao; i++){
      randomVal = 0; //TODO: Implementar randomVal (0 e populacao.size() -1)
      Solucao smutavel = populacao.get(randomVal);
      randomVal = 0; //TODO: Implementar randomVal (0 e medianas.size() - 1)
      int quantidadeMedianasAlteradas = randomVal;
      for(int k=0; k<quantidadeMedianasAlteradas; k++){
        randomVal = 0; //TODO: Implementar randomVal (0 e medianas.size() - 1)
        smutavel.medianas.remove(randomVal); //Remove a mediana na posicao randomVal
        randomVal = 0; //TODO: Implementar randomVal (0 e grafo.vertices.size() - 1)
        while(smutavel.medianas.contains(smutavel.grafo.vertices.get(randomVal))) randomVal = 0; //TODO: Implementar randomVal (0 e grafo.vertices.size() - 1)
        smutavel.inserirVertice(smutavel.grafo.vertices.get(randomVal)); //Adiciona o vértice como mediana
      }
    }
    for(Solucao s : populacao) s.calcularCusto();
    Collections.sort(populacao);
    return populacao;
  }

  /*Atualização da populacao*/
  public ArrayList<Solucao> atualizarPopulacao(ArrayList<Solucao> antiga, ArrayList<Solucao> nova){
    ArrayList<Solucao> pop = new ArrayList();
    Collections.sort(antiga); //Nova está ordenada
    int quantidadePreservados = (int)(antiga.size() * this.taxaDeElitismo);
    for(int i = 0; i<quantidadePreservados; i++){
      pop.add(antiga.get(i));
    }
    pop.addAll(nova);
    Collections.sort(pop);
    return pop;
  }

  public ArrayList<Solucao> gerarPopulacaoInicial(){
    ArrayList<Solucao> pop = new ArrayList<>();
    int k=0;
    while(k < this.tamanhoPopulacao){
      Solucao s = gerarSolucaoRandomica();
      while(s.custo == -1) s = gerarSolucaoRandomica();
      pop.add(s);
      k++;
    }
    Collections.sort(pop);
    return pop;
  }

  //TODO: MEDIANA TAMBÃ‰M DEVE SUPRIR SUA PRÃ“PRIA NECESSIDADE
  public Solucao gerarSolucaoRandomica(){
    Solucao s = new Solucao();
    int i = 0;
    while(i < this.numeroMedianas){
      int randomVal = 0; //TODO: Gerar entre 0 e s.grafo.vertices.size()-1
      if(s.medianas.contains(s.grafo.vertices.get(randomVal))) continue;
      s.inserirVertice(s.grafo.vertices.get(randomVal));
      i++;
    }
    s.calcularCusto();
    return s;
  }

  class Solucao implements Comparable<Solucao>{
    Grafo grafo;
    ArrayList<Vertice> medianas;
    PriorityQueue<Vertice> prioridades;
    double custo;

    public Solucao(){
      this.grafo = Main.GRAFO.copiar();
      this.custo = -1;
    }

    public void inserirVertice(Vertice v){
      medianas.add(this.grafo.vertices.get(v.id)); /*Insere o v�rtice correspondente �quele id, mas desta solu��o*/
    }

    public double calcularCusto(){
      double menorDist = -1, secMenorDist = -1;

      for(Vertice v : this.medianas){
        v.capacidade -= v.demanda;
      }

      for(Vertice v : grafo.vertices.values()){
        for(Vertice med : medianas){
          if(v.equals(med)) continue;
          double dist = Main.calcularDistancia(v, med);
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
        if(!v_priori.medianaCandidata1.isCheia(v_priori))
        v_priori.associarMediana(v_priori.medianaCandidata1, v_priori.distanciaMed1);
        else if(!v_priori.medianaCandidata2.isCheia(v_priori))
        v_priori.associarMediana(v_priori.medianaCandidata2, v_priori.distanciaMed2);
        else{
          Vertice v_mediana = null;
          double n_menorDist = -1;
          for(Vertice med : medianas){
            if(med.isCheia(v_priori)) continue;
            double dist = Main.calcularDistancia(v_priori, med);
            if(dist < n_menorDist){
              v_mediana = med;
              n_menorDist = dist;
            }
          }
          if(v_mediana == null) return -1;
          v_priori.associarMediana(v_mediana, n_menorDist);
        }
      }
      this.custo = 0;
      for(Vertice v : grafo.vertices.values())
        this.custo += v.distancia;
      return this.custo;
    }

    @Override
    public int compareTo(Solucao o) {
      double dif =  this.custo - o.custo;
      if(dif < 0) return -1;
      if(dif > 0) return 1;
      return 0;
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

  public Vertice(int id, int x, int y, int demanda, int capacidade){
    this.id = id;
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

  public boolean isCheia(Vertice v){
    return (this.capacidade < v.demanda);
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
  HashMap<Integer, Vertice> vertices;

  public Grafo(){
    vertices = new HashMap();
  }

  public void addVertice(Vertice v){
     vertices.put(v.id, v);
  }

  public Grafo copiar(){
    Grafo g = new Grafo();
    for(Vertice v : this.vertices.values()){
      g.addVertice(new Vertice(v.id, v.x, v.y, v.demanda, v.capacidade));
    }
    return g;
  }
}
