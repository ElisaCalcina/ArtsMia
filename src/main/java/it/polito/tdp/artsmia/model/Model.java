package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	private Map<Integer,ArtObject> idMap;
	
	public Model() {
		idMap= new HashMap<Integer, ArtObject>();
	}
	//o creo grafo nel modello, oppure lo faccio nel metodo di creazione
	public void creaGrafo() {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		ArtsmiaDAO dao= new ArtsmiaDAO();
		dao.listObjects(idMap);
		
		//aggiungere i vertici
		Graphs.addAllVertices(grafo, idMap.values());
		
		//aggiungere gli archi tra i vertici
		
		//Approccio 1= doppio ciclo for sui vertici --> dati due vertici controllo se sono collegati
		//--> non giunge al termine perchè ci sono troppi vertici e facciamo troppe query al database
		//VA BENE SOLO SE IL NUMERO DI VERTICI E' MOLTO BASSO
	/*	for(ArtObject a1: this.grafo.vertexSet()) {
			for(ArtObject a2: this.grafo.vertexSet()) {
				//devo collegare a1 con a2? se si lo collego con il peso
				int peso= dao.getPeso(a1, a2);
				if(peso>0) {
					//controllo se non esiste già l'arco
					if(this.grafo.containsEdge(a1,a2)) {
					//inserisco arco
					Graphs.addEdge(grafo, a1, a2, peso);
				}
			}
		}	
	}*/
	
		
	
		
		//Approccio 2= prendo un vertice e chiedo quali sono i vertici a lui adiacenti
		//query=SELECT eo2.object_id, COUNT(*)
			//FROM exhibition_objects AS eo1, exhibition_objects AS eo2
			//WHERE eo1.exhibition_id=eo2.exhibition_id AND eo1.object_id= ? AND eo2.object_id <> eo1.object_id
			//GROUP BY eo2.object_id
		
		
		//Approccio 3= arrivo alla fine in un tempo ragionevole e mi faccio dare dal db con un'unica query una serie di coppie
		//oggetto 1 oggetto 2 dove COUNT(*) è il peso da mettere nell'arco tra i due oggetti
		//--> mi faccio dare dal db direttamente tutte le adiacenze
		
		for(Adiacenza a: dao.getAdiacenze()) {
			if(a.getPeso()>0) {
				Graphs.addEdge(grafo, idMap.get(a.getObj1()), idMap.get(a.getObj2()), a.getPeso());
			}
		}
		
	//	System.out.println(String.format("Grafo creato! # Vertici %d, #Archi %d", this.grafo.vertexSet().size(), this.grafo.edgeSet().size()));
		
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
}
