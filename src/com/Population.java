package com;

import java.util.ArrayList;

import javafx.scene.paint.Color;

/**
 * 
 * La population representera un ensemble d'individu surlesquels nous opererons, par exemple en les croisants entre eux.
 * Dans notre algorithme genetique, une population representera une generation
 *
 */
public class Population {
	
	
	/**
	 * Contient l'ensemble des individus sur lesquels nous opereront
	 */
	protected ArrayList<Individu> ensemble;
	
	/**
	 * Represente le nombre d'individu actuellement contenu dans la population
	 */
	protected int effectif;
	
	
	/**
	 * Cree une population a partir d'un ensemble d'individus donne
	 * @param ensemble L'ensemble d'individus a partir duquel creer la population
	 */
	protected Population(ArrayList<Individu> ensemble) {
		this.ensemble = ensemble;
		this.effectif = ensemble.size();
	}
	
	
	/**
	 * Cree populatons d'individus aleatoires
	 * @param effectif Nombre d'individus a creer
	 * @param nbPoly Nombre de polygones que contiendra chaque individu
	 * @param nbCmax Nompre de points qu'auront chaque polygone
	 */
	protected Population(int effectif, int nbPoly, int nbCmax) {
		ArrayList<Individu> res = new ArrayList<Individu>();
		for (int i=0; i<effectif; i++) {
			res.add(new Individu (nbPoly, nbCmax));
		}
		ensemble = res;
		this.effectif = effectif;
	}
	
	/**
	 * Etablie la performance des tous les Individus compris dans la population
	 * @param reference Represente les pixels de l'image originelle
	 */
	protected void setAllPerf(Color[][] reference) {
		for(Individu i : ensemble) {
			i.setPerf(reference);
		}
	}
	
	
	/**
	 * Ajoute un Individu a la liste en mettant a jour la valeur de l'attribut effectif
	 * @param indi Individu a rajouter
	 */
	protected void add(Individu indi) {
		ensemble.add(indi);
		effectif += 1;
	}
	
	
	/**
	 * Retire un Individu a la liste en mettant a jour la valeur de l'attribut effectif
	 * @param i Index de l'individu a retirer
	 */
	protected void remove(int i) {
		ensemble.remove(i);
		effectif -= 1;
	}
	
	
	/**
	 * Permet de copie l'objet
	 * @return Renvoie la copie de l'objet
	 */
	protected Population clonage() {
		ArrayList<Individu> adnCopie = new ArrayList<Individu>();
		for(Individu c : ensemble) {
			adnCopie.add(c.clonage());
		}
		Population nouveau = new Population(adnCopie);
		return nouveau;
	}
	
	
	
}
