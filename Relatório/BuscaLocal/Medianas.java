import java.util.*;

/**
Classe principal
Modela a entrada em um grafo e exibe a soluÃ§Ã£o
*/
class Main{
  private static int idMaker;
  private static long SEED = 5;
  public static Random random = new Random(SEED);
  public static Grafo GRAFO;

  public static void main(String[] args){
    idMaker = -1;
    int numeroMedianas = 0;
    Scanner scanner = new Scanner(System.in);
    /*Primeira Linha*/
    if(scanner.hasNextLine()){
      String string = scanner.nextLine();
      String[] str = string.split("\\s+");
      //qtdVertices = Integer.valueOf(str[0]);
      numeroMedianas = Integer.valueOf(str[1]);
    }else{
      System.out.println("ENTRADA INVALIDA!");
      return;
    }

    GRAFO = getInput(scanner);

    int tamanhoPopulacao = 15; /*30% da quantidade de vertices*/
    int numeroGeracoes = 5; /*vertices/medianas = quantidade de iteraÃ§Ãµes*/
    double taxaDeMutacao = 0.03; //TODO: Alterar
    double taxaDeElitismo = 0.1; //TODO: Alterar
    int quantidadeReprodutores = 2;
    RunTestes rt = new RunTestes(tamanhoPopulacao, numeroGeracoes, numeroMedianas, quantidadeReprodutores, taxaDeElitismo, taxaDeMutacao);
    System.out.println(rt.executar());
  }

  public static Grafo getInput(Scanner scanner){
    Grafo grafo = new Grafo();
    /*Demais Linhas*/
    while(scanner.hasNextLine()){
      String string = scanner.nextLine();
      if(string.isEmpty()) break;
      String[] str = string.split("\\s+");
      if(str.length < 5)
        grafo.addVertice(Integer.valueOf(str[0]), Integer.valueOf(str[1]), Integer.valueOf(str[2]), Integer.valueOf(str[3]));
      else
        grafo.addVertice(Integer.valueOf(str[1]), Integer.valueOf(str[2]), Integer.valueOf(str[3]), Integer.valueOf(str[4]));
    }

    return grafo;
  }

  public static int getId(){
    idMaker = idMaker + 1;
    return idMaker;
  }

}

/**
Classe que roda um caso de teste
Recebe os parÃ¢metros e submete aos mÃ©todos
*/
class RunTestes{
  static int tamanhoPopulacao;
  static int numeroGeracoes;
  static int numeroMedianas;
  static int numeroVertices;
  static int quantidadeReprodutores;
  static double taxaDeElitismo;
  static double taxaDeMutacao;

  ArrayList<Solucao> populacao;

  public RunTestes(int tamanhoPopulacao, int numeroGeracoes, int numeroMedianas, int quantidadeReprodutores, double taxaDeElitismo, double taxaDeMutacao){
    this.tamanhoPopulacao = tamanhoPopulacao;
    this.numeroGeracoes = numeroGeracoes;
    this.numeroMedianas = numeroMedianas;
    this.taxaDeElitismo = taxaDeElitismo;
    this.taxaDeMutacao = taxaDeMutacao;
    this.quantidadeReprodutores = quantidadeReprodutores;
    this.numeroVertices = Main.GRAFO.vertices.values().size();
    this.populacao = gerarPopulacaoInicial();
  }

  public double executar(){
    int k=0;
    while(k < this.numeroGeracoes){
      ArrayList<Solucao> reprodutores = selecionarReprodutores();
      ArrayList<Solucao> novaPopulacao = cruzar(reprodutores);
      novaPopulacao = aplicarMutacao(novaPopulacao); /*Calcula os custos (com e sem mutação)*/
      this.populacao = atualizarPopulacao(this.populacao, novaPopulacao);
      k++;
    }
    return this.populacao.get(0).custo;
  }

  /*Seleção por torneio*/
  public ArrayList<Solucao> selecionarReprodutores(){
    int i=0, k=0, randomVal;
    ArrayList<Solucao> reprodutores = new ArrayList<Solucao>();

    while(i<this.quantidadeReprodutores){
      k=0;
      PriorityQueue<Solucao> torneio = new PriorityQueue<Solucao>();
      while(k<this.quantidadeReprodutores){
        do{
          randomVal = Main.random.nextInt(populacao.size());
        }while(torneio.contains(this.populacao.get(randomVal)) || reprodutores.contains(this.populacao.get(randomVal)));
        torneio.add(this.populacao.get(randomVal));
        k++;
      }
      reprodutores.add(torneio.poll());
      i++;
    }
    return reprodutores;
  }

  /*Cruzamento mesclando medianas*/
  public ArrayList<Solucao> cruzar(ArrayList<Solucao> reprodutores){
    ArrayList<Solucao> novosIndividuos = new ArrayList<>();
    int randomVal;
    //Cruza o melhor reprodutor com o pior
    while(reprodutores.size() > 1){
      Solucao filho = new Solucao();
      Solucao rep1 = reprodutores.get(0),
                     rep2 = reprodutores.get(reprodutores.size()-1);
      ArrayList<Integer> naoComuns = new ArrayList<>();
      for(int i=0; i<this.numeroMedianas; i++){ //Insere os comuns
        int id_medianaR1 = rep1.medianas.indices[i];
        int id_medianaR2 = rep2.medianas.indices[i];

        if(rep2.medianas.contains(id_medianaR1))
          filho.inserirMediana(id_medianaR1);
        else
          naoComuns.add(id_medianaR1);

        if(!rep1.medianas.contains(id_medianaR2))
          naoComuns.add(id_medianaR2);
      }

      int faltantes = this.numeroMedianas - (filho.medianas.index + 1);
      while(faltantes > 0){
        for(int i=0; i<faltantes; i++){ //Sorteia entre os demais
          do{
            randomVal = Main.random.nextInt(naoComuns.size());
          }while(filho.medianas.contains(naoComuns.get(randomVal)));
          filho.inserirMediana(naoComuns.get(randomVal));
        }
        faltantes = this.numeroMedianas - (filho.medianas.index + 1);
      }

      reprodutores.remove(rep1);
      reprodutores.remove(rep2);
      novosIndividuos.add(buscaLocal(filho).get(0));
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
      randomVal = Main.random.nextInt(populacao.size());
      Solucao smutavel = populacao.get(randomVal); //Seleciona um individuo para mutar
      int indexToRemove = Main.random.nextInt(RunTestes.numeroMedianas); //Seleciona uma mediana para remover
      do{
        randomVal = Main.random.nextInt(RunTestes.numeroVertices); //Seleciona um novo vértice como mediana
      }while(smutavel.medianas.contains(randomVal));
      smutavel.medianas.insertAt(indexToRemove, randomVal); //Substitui
    }
    for(Solucao s : populacao){
      s.calcularCusto();
    }
    Iterator<Solucao> it = populacao.iterator();
    while (it.hasNext()) {
        Solucao s = it.next();
        if (s.custo < 0) {
            it.remove();
        }
    }

    Collections.sort(populacao);
    return populacao;
  }

  /*Atualização da populacao*/
  public ArrayList<Solucao> atualizarPopulacao(ArrayList<Solucao> antiga, ArrayList<Solucao> nova){
    ArrayList<Solucao> pop = new ArrayList<>();
    Collections.sort(antiga); //Nova está ordenada
    pop.addAll(antiga);
    pop.addAll(nova);
    Collections.sort(pop);
    pop.subList(tamanhoPopulacao - 1, pop.size()).clear();
    return pop;
  }

  /*Aplica Busca Local e retorna a vizinhança da solução*/
  public ArrayList<Solucao> buscaLocal(Solucao individuo){
    ArrayList<Solucao> vizinhanca = new ArrayList<>();
    //Gera os vizinhos (clona o individuo)
    for(int i=0; i<this.numeroMedianas; i++){
      Solucao vizinho = new Solucao();
      for(int k=0; k<this.numeroMedianas; k++){
        vizinho.medianas.add(individuo.medianas.indices[k]);
      }
      vizinhanca.add(vizinho);
    }
    //Altera os vizinhos
    for(int i=0; i<vizinhanca.size(); i++){
      Solucao vizinho = vizinhanca.get(i);
      int mediana = vizinho.medianas.indices[i];
      do{
        mediana = ((mediana + 1)%this.numeroVertices);
      }while(vizinho.medianas.contains(mediana));
      vizinho.medianas.insertAt(i, mediana);
      vizinho.calcularCusto();
    }
    vizinhanca.add(individuo);
    Collections.sort(vizinhanca);
    return vizinhanca;
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
      int randomVal;
      do{
        randomVal = Main.random.nextInt(RunTestes.numeroVertices);
      }while(s.medianas.contains(randomVal));
      s.inserirMediana(randomVal);
      i++;
    }
    s.calcularCusto();
    return s;
  }

  class Solucao implements Comparable<Solucao>{
    double custo;
    VerticeSolucao[] vertices;
    Medianas medianas;

    public Solucao(){
      this.medianas = new Medianas();
      this.custo = -1;
      this.vertices = new VerticeSolucao[Main.GRAFO.vertices.values().size()];
    }

    public void inserirMediana(int id){
      medianas.add(id);
    }

    public double calcularCusto(){
      for(int k=0; k<this.medianas.indices.length; k++){
        this.medianas.capacidades[k] -= Main.GRAFO.vertices.get(this.medianas.indices[k]).demanda;
      }

      PriorityQueue<VerticeSolucao> prioridades = new PriorityQueue<>();

      for(int i=0; i<RunTestes.numeroVertices; i++){
        vertices[i] = new VerticeSolucao();
        vertices[i].id = i;

        if(this.medianas.contains(i)){
          vertices[i].difDistancia = -1;
          continue;
        }

        double menorDist = Double.POSITIVE_INFINITY;
        double secMenorDist = Double.POSITIVE_INFINITY;

        for(int k=0; k<this.medianas.indices.length; k++){
          double dist = Grafo.calcularDistancia(Main.GRAFO.vertices.get(this.medianas.indices[k]),
                                                Main.GRAFO.vertices.get(k));
          if(dist < menorDist){
            secMenorDist = menorDist;
            menorDist = dist;
            vertices[i].indexCandidata = k;
            vertices[i].distancia = menorDist;
          }else if(dist < secMenorDist){
            secMenorDist = dist;
          }
        }
        vertices[i].difDistancia = (secMenorDist - menorDist);
        prioridades.add(vertices[i]);
      }
      Arrays.sort(vertices);
      VerticeSolucao vs;
      int rej;
      for(int i=0; i<vertices.length; i++){
        rej = 0;
        vs = vertices[i];
        if(vs.difDistancia < 0)
          continue;
        if(this.medianas.capacidades[vs.indexCandidata] >= Main.GRAFO.vertices.get(vs.id).demanda){ //Cabe
          vs.indexMediana = vs.indexCandidata;
        }else{
          double menorDist = Double.POSITIVE_INFINITY, local = 0;
          for(int k=0; k<this.medianas.indices.length; k++){
            if(this.medianas.capacidades[k] >= Main.GRAFO.vertices.get(vs.id).demanda){ //Cabe
              local = Grafo.calcularDistancia(Main.GRAFO.vertices.get(this.medianas.indices[k]), Main.GRAFO.vertices.get(vs.id));
              if(local < menorDist){
                menorDist = local;
                vs.indexMediana = k;
                vs.distancia = local;
              }
            }else{
              rej++;
            }
          }
        }
        if(rej!=this.medianas.indices.length)
          this.medianas.capacidades[vs.indexMediana] -= Main.GRAFO.vertices.get(vs.id).demanda;
        else{
          vs.indexMediana = -1;
          vs.distancia = -1;
        }
      }

      for(VerticeSolucao v : vertices){
        if(v.indexMediana == -1){
          this.custo = -1;
          break;
        }
        this.custo += v.distancia == -1 ? 0 : v.distancia;
      }
      return 0;
    }

    @Override
    public int compareTo(Solucao o){
      double dif =  this.custo - o.custo;
      if(dif < 0) return -1;
      if(dif > 0) return 1;
      return 0;
    }
  }


  class Medianas{
    int[] indices;
    double[] capacidades;
    int index;
    public Medianas(){
      indices = new int[RunTestes.numeroMedianas];
      capacidades = new double[RunTestes.numeroMedianas];
      index = 0;
    }
    public void add(int id){
      this.indices[index] = id;
      this.capacidades[index] = Main.GRAFO.vertices.get(id).capacidade;
      index++;
    }
    public boolean contains(int id){
      for(int med : indices){
        if(id == med) return true;
      }
      return false;
    }
    public void insertAt(int idx, int id){
      this.indices[idx] = id;
      this.capacidades[idx] = Main.GRAFO.vertices.get(id).capacidade;
    }
  }

}

class VerticeSolucao implements Comparable<VerticeSolucao>{
  double difDistancia, distancia;
  int indexMediana, indexCandidata, id;

  @Override
  public int compareTo(VerticeSolucao o1) {
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

  public void addVertice(int x, int y, int capacidade, int demanda){
    Vertice v = new Vertice(x, y, capacidade, demanda);
    vertices.put(v.id, v);
  }

  public static double calcularDistancia(Vertice v1, Vertice v2){
    return (Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2)));
  }

  public Grafo copiar(){
    Grafo g = new Grafo();
    // for(Vertice v : this.vertices.values()){
    //   g.addVertice(new Vertice(v.id, v.x, v.y, v.capacidade, v.demanda));
    // }
    return g;
  }

  class Vertice{
    int id, x,y, demanda, capacidade;

    public Vertice(int x, int y, int capacidade, int demanda){
      this.id = Main.getId();
      this.x = x;
      this.y = y;
      this.demanda = demanda;
      this.capacidade = capacidade;
    }

    public Vertice(int id, int x, int y, int capacidade, int demanda){
      this.id = id;
      this.x = x;
      this.y = y;
      this.demanda = demanda;
      this.capacidade = capacidade;
    }

    public boolean comporta(Vertice v){
      return (this.capacidade >= v.demanda);
    }
  }
}
