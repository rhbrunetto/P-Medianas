package pmedianas;

import java.util.*;

/**
 *
 * @author Ricardo Henrique Brunetto
 * 27 de Novembro de 2017
 */
class Pmedianas {

    public static void main(String[] args) {
        // TODO code application logic here
    }
}
    
class RunTestes{
    int semente;
    
    public RunTestes(int semente){
        this.semente = semente;
    }
       
    public Grafo read_input(){
        Grafo input = new Grafo();
        Scanner scanner;
        scanner = new Scanner(System.in);
        
        while(scanner.hasNextLine()){
            String string = scanner.nextLine();
            if(string.isEmpty()) break;
            String[] str = string.split(" ");
            Integer i;
            try{
                i =  Integer.valueOf(str[2]);
            }catch(Exception e){
                continue;
            }
            grafo.adicionarAresta(grafo.adicionarVertice(new Vertice(Integer.valueOf(str[0])), grafo.adicionarVertice(new Vertice(Integer.valueOf(str[1]))), i);
        }
        return input;
        }
        
        
    }
}

class Vertice{
    int x, y, id;
    double demanda;
    boolean mediana;
    PriorityQueue<Adjacencia> adjacentes;
    
    public Vertice(int x, int y, double demanda, boolean mediana){
        this.x = x;
        this.y = y;
        this.demanda = demanda;
        this.mediana = mediana;
        this.adjacentes = new PriorityQueue<>();
    }
    
    public boolean isMediana(){
        return mediana;
    }
    
    /**
     * Inclui uma Adjacencia na lista de vertices adjacentes
     * @param adjacencia
     */
    public void adicionarAdjacencia(Adjacencia adjacencia){
        adjacentes.add(adjacencia);
    }
    
    List<Vertice> get2MaisProximos(){
        return null;
    }
}

/**
 * Uma adjacencia entre dois vertices implica em uma ARESTA
 */
class Adjacencia implements Comparable<Adjacencia>{
    Vertice vertice;
    double distancia;

    public Adjacencia(Vertice vertice, double distancia) {
        this.vertice = vertice;
        this.distancia = distancia;
    }
    
    @Override
    public int compareTo(Adjacencia o1) {
        double dif =  this.distancia - o1.distancia;
        if(dif > 0) return 1;
        if(dif < 0) return -1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return this.vertice.id == ((Adjacencia)o).vertice.id;
    }
    
}


class Grafo {
    private Map<Integer, Vertice> vertices;
    private int quantidadeArestas; //Apenas para controle no print

    public Grafo() {
        vertices = new HashMap<>();
        quantidadeArestas = 0;
    }

    /**
     * Adiciona um vertice ao um grafo
     * @param v
     */
    public Vertice adicionarVertice(Vertice v){
        Vertice p = existsVertice(v.id);
        if(p == null){
            vertices.put(v.id, v);
            return v;
        }else{
            return p;
        }
    }

    /**
     * Adicionar uma aresta entre dois vertices
     * @param v
     * @param u
     * @param distancia
     */
    public void adicionarAresta(Vertice v, Vertice u){
        double distancia = Math.sqrt(Math.pow(v.x - u.x, 2) + Math.pow(v.y - u.y, 2));
        v.adicionarAdjacencia(new Adjacencia(u, distancia));
        u.adicionarAdjacencia(new Adjacencia(v, distancia));
        quantidadeArestas++;
    }

    /**
     * Funcao que informa se o vertice que possui tal rotulo ja esta no grafo
     * @param rotulo
     */
    private Vertice existsVertice(int rotulo){
        return vertices.get(rotulo);
    }
    
}
   

