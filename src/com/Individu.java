package com;

import java.util.ArrayList;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Un individu represente un ensemble de 50 polygones convexe
 * @author alexi
 *
 */
public class Individu implements Comparable {
	
	/**
	 * Contient les polygones qui constitueront l'image finale
	 */
	protected ArrayList<ConvexPolygon> adn;
	
	/**
	 * Represente la performance de l'image associe a cette liste de polygones
	 */
	protected double performance = -1;
	
	/**
	 * Cree un individu a partir du liste de polygone placee en argument
	 * @param adn Liste de polygones a partir de laquelle l'objet est cree
	 */
	protected Individu(ArrayList<ConvexPolygon> adn) {
		this.adn = adn;
	}
	
	
	/**
	 * Cree un individu en lui donnant pour adn une liste contenant un certain nombre de polygones aleatoires 
	 * @param nbPoly Nombre de polygones a creer aleatoirement
	 * @param nbCmax Nombre maximum de points que peuvent avoir les polygones
	 */
	protected Individu(int nbPoly, int nbCmax) {
		ArrayList<ConvexPolygon> tab = new ArrayList<ConvexPolygon>();
		for (int i=0; i<nbPoly; i++) {
			tab.add(new ConvexPolygon (nbCmax));
		}
		this.adn = tab;
	}
	
	
	/**
	 * Determine la performance de la solution proposee
	 * @param reference Represente les differents pixels de l'image a copier
	 */
	protected void setPerf(Color[][] reference) {
		int maxX = 100;//BEWARE !!! a revoir
		int maxY = 149;
		// formation de l'image par superposition des polygones
		Group image = new Group();
		for (ConvexPolygon p : this.adn)
			image.getChildren().add(p);
		
		// Calcul de la couleur de chaque pixel.Pour cela, on passe par une instance de 
		// WritableImage, qui poss�de une m�thode pour obtenir un PixelReader.
		WritableImage wimg = new WritableImage(maxX,maxY);
		image.snapshot(null,wimg); 
		PixelReader pr = wimg.getPixelReader();
		// On utilise le PixelReader pour lire chaque couleur
		// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
		// de la couleur du pixel cible et celui du pixel de l'image g�n�r�e	
		double res=0;
		for (int i=0;i<maxX;i++){
			for (int j=0;j<maxY;j++){
				Color c = pr.getColor(i, j);
				//System.out.println(res);
				//System.out.println(target[i][j].getBlue());
				res += Math.pow(c.getBlue()-reference[i][j].getBlue(),2)
								+Math.pow(c.getRed()-reference[i][j].getRed(),2)
								+Math.pow(c.getGreen()-reference[i][j].getGreen(),2)
								;
			}
		}
		
		this.performance = Math.sqrt(res);
		
	}
	
	/**
	 * Renvoie 
	 * @param reference
	 * @param nbportion
	 * @return
	 */
	protected int [] setPerfZoneGlissade(Color[][] reference, double nbportion) {
		int maxX = ConvexPolygon.max_X;
		int maxY = ConvexPolygon.max_Y;
		int minX = 0;
		int minY = 0;
		double vX = maxX/(nbportion);//taille du cadre coulissant
		double vY = maxY/(nbportion);
		int X = (int)vX;
		int Y= (int)vY;
		// formation de l'image par superposition des polygones
		Group image = new Group();
		for (ConvexPolygon p : this.adn)
			image.getChildren().add(p);
		
		// Calcul de la couleur de chaque pixel.Pour cela, on passe par une instance de 
		// WritableImage, qui poss�de une m�thode pour obtenir un PixelReader.
		WritableImage wimg = new WritableImage(maxX,maxY);
		image.snapshot(null,wimg); 
		PixelReader pr = wimg.getPixelReader();
		// On utilise le PixelReader pour lire chaque couleur
		// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
		// de la couleur du pixel cible et celui du pixel de l'image g�n�r�e	
		
		double perfmin = 0.0;
		double perf = 0;
		int[] res = new int[4];
		while (Y < maxY) {
			X = (int) vX;
			while (X < maxX) {
				for (int i = minX ; i < X ; i++){
					for (int j = minY ; j < Y ; j++){
						Color c = pr.getColor(i, j);
						perf += Math.pow(c.getBlue()-reference[i][j].getBlue(),2)
										+Math.pow(c.getRed()-reference[i][j].getRed(),2)
										+Math.pow(c.getGreen()-reference[i][j].getGreen(),2);
					}
				}
				if (perf > perfmin) {
					perfmin = perf;
					res [0] = minX;
					res [1] = minY;
					res [2] = X;
					res [3] = Y;
					
				}
				minX += 1;
				X +=1;
			}
			minY += 1;
			Y += 1;
		}
		return res;		
	}

	
	
	
	
	/**
	 * Permet d'effetuer une copie de l'objet
	 * @return Renvoie la copie de l'objet
	 */
	protected Individu clonage() {
		ArrayList<ConvexPolygon> adnCopie = new ArrayList<ConvexPolygon>();
		for(ConvexPolygon c : adn) {
			adnCopie.add(c.clonage());
		}
		Individu nouveau = new Individu(adnCopie);
		nouveau.performance = this.performance;
		return nouveau;
	}
	
	
	
	
	/**
	 * Permet de comparer deux differents individus en fonction de leur performance
	 */
	public int compareTo(Object o) {
		Individu indiv = (Individu) o;
		return Double.compare(this.performance, indiv.performance);
	    }
}
