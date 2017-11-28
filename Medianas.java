import java.util.*;
import java.io.*;

class Main{
    public static Grafo GRAFO;
    private static int a;
    
    public static void main(String[] args){
       a = -1;
    }
    
    public static int getId(){
        a = a + 1;
        return a;
    }
}

class RunTestes{
    int populacaoInicial;
    int numeroGeracoes;
    double margemMinima;
    
    public static double calcularDistancia(Vertice v1, Vertice v2){
            //TODO: Implementar
            return (0);
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
                Vertice priori = prioridades.poll();
                if(!priori.medianaCandidata1.isCheia())
                    priori.associarMediana(priori.medianaCandidata1, priori.distanciaMed1);
                else if(!priori.medianaCandidata2.isCheia())
                    priori.associarMediana(priori.medianaCandidata2, priori.distanciaMed2);
                else{
                    Vertice v_mediana = null;
                    double n_menorDist = -1;
                    for(Vertice med : medianas){
                        if(med.isCheia()) continue;
                        double dist = calcularDistancia(priori, med);
                        if(dist < n_menorDist){
                            v_mediana = med;
                            n_menorDist = dist;
                        }
                    }
                    priori.associarMediana(v_mediana, n_menorDist);
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
    
    //TODO: Implementar
    public Grafo copiar(){
        return null;
    }
}