package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	private ArtsmiaDAO dao;
	private Map<Integer, ArtObject> idMap;
	
	public Model() {
		dao= new ArtsmiaDAO();
		this.idMap= new HashMap<Integer, ArtObject>();
		
	}
	
	public void creaGrafo() {//GRAFO NON ORIENTATO!
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class); //non lo inizializzo nel costruttore ma qua così sono sicuro che ogni volta che sia distrutto il precedente e ricreato
		
		//aggiungo i vertici
		this.dao.listObjects(idMap); //riempio la mappa
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//aggiungo gli archi
		
		//APPROCCIO 1: doppio ciclo for per torvare le coppie di vertici e poi chiedo al database se sono collegati
		//In questo caso non va bene perchè il database è grande
		//non va sempre bene tranne in qualche caso; se la ricorsione all'esame cercano di farla funzionare, il grafo deve assolutamente venire al primo colpo
//		for(ArtObject a1: this.grafo.vertexSet()) {
//			for(ArtObject a2: this.grafo.vertexSet()) {
//				if(!a1.equals(a2) && !this.grafo.containsEdge(a1,a2)) { //se non c'è già un arco, non mi interessa orientato
//					//chiedo al db se devo collegare a1 e a2
//					int peso= dao.getPeso(a1, a2);
//					if(peso>0)
//						Graphs.addEdgeWithVertices(this.grafo, a1, a2, peso);
//					
//				}
//			}
//		}
//		System.out.println("Grafo creato!");
//		System.out.println("#VERTICI: "+this.grafo.vertexSet());
//		System.out.println("#ARCHI: "+ this.grafo.edgeSet());
		//approccio troppo dispendioso. Questo metodo funziona solo con un numero piccolo di vertici. Conviene quindi farci dare più cose dal database in una sola volta 
		
		
		//APPROCCIO 2
		for(Adiacenza a : this.dao.getAdiacenze(idMap)) {
			Graphs.addEdgeWithVertices(grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
		System.out.println("Grafo creato!");
		System.out.println("#VERTICI: "+this.grafo.vertexSet());
		System.out.println("#ARCHI: "+ this.grafo.edgeSet());
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}

	public ArtObject getObject(int objectId) {
		return idMap.get(objectId);
	}

	public int getComponenteConnessa(ArtObject vertice) {//posso decidere se fare visita in ampiezza o in profondita
		Set<ArtObject> visitati= new HashSet<>();
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> it= new DepthFirstIterator<>(this.grafo, vertice);
		while(it.hasNext())
			visitati.add(it.next());
		return visitati.size();
	}

}
