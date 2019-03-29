package com;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.imageio.ImageIO;

import javafx.scene.paint.Color;


/**
 * 
 * Dans cette classe nous effectueront toutes nos methodes nous permettant d'operer sur les populations et les individus.
 * Ici, un individu representera un ensemble de polygone, soit une solution, et une population representera un ensemble d'individu.
 * Nous utiliserons dans la plupart des cas une approche du type algorithme genetique, un individu representera donc une solution et
 * une population une generation. La plupart du temps nos algorithmes aplliqueront donc des selections, reproductions, mutations et croisement a
 * nos populations.
 * 
 * Vous remarquerez que certaines methodes prennent en argument d'autre methodes, nous avont fait ce choix car ayant plusierurs versions
 * de methodes telles que la mutations ou la selection, cela nous permet de ne pas avoir a recoder certaines methodes.
 *
 */
public class Gestionnaire {
	/**
	 * Il s'agit du nombre d'individu qui constituront la population etudiee
	 */
	protected int effectif;
	
	/**
	 * Il s'agit du nombre maximal de polygone que pourra contenir un individu
	 */
	protected int nbPoly;
	
	
	protected int nbPolyMax = 50;
	
	
	/**
	 *Il s'agit du nombre de points maximal que pourra contenir un polygone 
	 */
	protected int nbPoints;
	
	/**
	 * Il s'agit de la population actuellement en train d'etre etudiee
	 */
	protected Population currentPop;
	
	/**
	 * Il s'agit du chemin menant au fichier de l'image source
	 */
	protected String path;
	
	/**
	 * Represente les differentts pixels de l'image source
	 */
	protected Color[][] reference;
	
	/**
	 * Il s'agit simplement d'un outil qui sera utilise pour determiner les valeurs aleatoires dans les differentes
	 * methodes du gestionnaire
	 */
	protected static Random gen = new Random();
	
	/**
	 * Il s'agit dans la plupart des mutations de la probabilite qu'une mutation soit operee, dans certaines mutations il s'agit de la probabilite
	 * que l'emplecement des points soit changes
	 */
	protected double proba;
	
	/**
	 * Il s'agit dans certaines mutations de la probabilite qu'une mutation change la couleur d'un polygone
	 */
	protected double probaColor;
	
	/**
	 * Il s'agit dans certaines mutations de la probabilite qu'une mutation change l'opacite d'un polygone
	 */
	protected double probaOpa;
	
	/**
	 * Il s'agit dans certaines mutations de la probabilite qu'une mutation translate un polygone,
	 * c'est a dire qu'elle deplace tous les points de ce polygone celon le meme vecteur
	 */
	protected double probaTranslation;
	
	/**
	 * Il s'agit de l'ampleur maximale que pourra prendre la translation d'un polygone lors d'une mutation
	 */
	protected double montantTranslation;
	
	/**
	 * Il s'agit de l'ampleur maximale que pourra prendre le deplacement d'un point lors de certaines mutations sur les triangles
	 */
	protected double montantPoint;
	
	/**
	 * Il s'agit de la valeur maximale qui sera rajoutee ou retiree a la couleur d'un polygone lors de certaines mutations
	 */
	protected double montantColor;
	
	/**
	 * Il s'agit de la valeur maximale qui sera rajoutee ou retiree a l'opacite d'un polygone lors de certaines mutations
	 */
	protected double montantOpa;
	
	/**
	 * Si lors d'une reproduction , le choix des individus qui se reproduiront depend de leur performance, il s'agit de l'exposant auquel sera place
	 * l'inverse de la performance. Plus l'exposant est grand plus les ecarts de valeur entre les performances de deux individus seront accentues
	 * lorsqu'il faudra choisir les reproducteurs
	 */
	protected int expoErreur;
	
	/**
	 * Il s'agit lors de la selection du pourcentage d'individus qui seront conserves apres une selection aleatoire des individus contenus dans
	 * la population
	 */
	int pourcentageRandom;
	
	/**
	 * Il s'agit lors de la selection du pourcentage d'individus restant dans la population qui seront conserves conserves apres la selection
	 */
	int pourcentageReal;
	
	/**
	 * Il s'agit du nombre d'individus qui seront automatiquement conserves d'une generation a l'autre sans subir ni croisement ni mutation.
	 * Cela permettra de conserver les meilleurs elements d'une generation a l'autre sans les alterer
	 */
	int nbChampions;
	
	
	protected Gestionnaire(int maxX, int maxY, double probaTranslation, double montantTranslation, int pourcentageRandom, int pourcentageReal, int nbChampions, int effectif, int nbPoly, 
			int nbPoints, String path, double proba, double probaColor, double probaOpa, double montantPoint, double montantColor, double montantOpa, int expoErreur) {
		ConvexPolygon.max_X=maxX;
		ConvexPolygon.max_Y=maxY;
		this.effectif = effectif;
		this.nbPoly = nbPoly;
		this.nbPoints = nbPoints;
		this.path = path;
		this.proba = proba;
		this.probaColor = probaColor;
		this.probaOpa = probaOpa;
		this.montantColor = montantColor;
		this.montantOpa = montantOpa;
		this.expoErreur = expoErreur;
		this.currentPop = new Population(effectif, nbPoly, nbPoints);
		this.reference = imageREF(path);
		this.pourcentageRandom = pourcentageRandom;
		this.pourcentageReal = pourcentageReal;
		this.nbChampions = nbChampions;
		this.montantPoint = montantPoint;
		this.probaTranslation = probaTranslation;
		this.montantTranslation = montantTranslation;
		
		if(proba < 0 || proba > 1) {
			throw new IllegalArgumentException("Une probabilite doit etre comprise en 0 et 1");
		}
		if(probaColor < 0 || probaColor > 1) {
			throw new IllegalArgumentException("Une probabilite doit etre comprise en 0 et 1");
		}
		if(probaOpa < 0 || probaOpa > 1) {
			throw new IllegalArgumentException("Une probabilite doit etre comprise en 0 et 1");
		}
		if(expoErreur < 1) {
			throw new IllegalArgumentException("La valeur de l'exposant place sur la performance doit ertre superieur ou egale a 1");
		}
		if(nbPoints<3) {
			throw new IllegalArgumentException("Les polygones doivent avoir au moins 3 cotes");
		}
	}
		
	protected Gestionnaire(int maxX, int maxY, double probaTranslation, double montantTranslation, int pourcentageRandom, int pourcentageReal, int nbChampions, Population currentPop, int effectif, int nbPoly, int nbPoints, String path, double proba, double probaColor, double probaOpa,
				double montantPoint, double montantColor, double montantOpa, int expoErreur) {
			ConvexPolygon.max_X=maxX;
			ConvexPolygon.max_Y=maxY;
			this.currentPop = currentPop;
			this.effectif = effectif;
			this.nbPoly = nbPoly;
			this.nbPoints = nbPoints;
			this.path = path;
			this.proba = proba;
			this.probaColor = probaColor;
			this.probaOpa = probaOpa;
			this.montantColor = montantColor;
			this.montantOpa = montantOpa;
			this.expoErreur = expoErreur;
			this.reference = imageREF(path);
			this.pourcentageRandom = pourcentageRandom;
			this.pourcentageReal = pourcentageReal;
			this.nbChampions = nbChampions;
			this.montantPoint = montantPoint;
			this.probaTranslation = probaTranslation;
			this.montantTranslation = montantTranslation;
			
			
			if(proba < 0 || proba > 1) {
				throw new IllegalArgumentException("Une probabilite doit etre comprise en 0 et 1");
			}
			if(probaColor < 0 || probaColor > 1) {
				throw new IllegalArgumentException("Une probabilite doit etre comprise en 0 et 1");
			}
			if(probaOpa < 0 || probaOpa > 1) {
				throw new IllegalArgumentException("Une probabilite doit etre comprise en 0 et 1");
			}
			if(expoErreur < 1) {
				throw new IllegalArgumentException("La valeur de l'exposant place sur la performance doit ertre superieur ou egale a 1");
			}
			if(nbPoints<3) {
				throw new IllegalArgumentException("Les polygones doivent avoir au moins 3 cotes");
			}
	}
	
	/**
	 * Permet de transformer une image en tableau de pixel qui sera utilise lors de la determination de la performance d'un individu
	 * @param path Chemin vers l'image a considerer
	 * @return Renvoie le tableau de pixel associe a l'image placee en argument
	 */
	public static Color[][] imageREF (String path){
		Color[][] target=null;
		int maxX=0;
    	int maxY=0;
		try{
			BufferedImage bi = ImageIO.read(new File(path));
			maxX = bi.getWidth();
			maxY = bi.getHeight();
        	target = new Color[maxX][maxY];
        	for (int i=0;i<maxX;i++){
        		for (int j=0;j<maxY;j++){
        			int argb = bi.getRGB(i, j);
        			int b = (argb)&0xFF;
        			int g = (argb>>8)&0xFF;
        			int r = (argb>>16)&0xFF;
        			int a = (argb>>24)&0xFF;
        			target[i][j] = Color.rgb(r,g,b);
        		}//remplis un tab de pixels de l'image de ref
        	}
		}
		catch(IOException e){
        	System.err.println(e);
        	System.exit(9);
        }
		return target;
	}
	
	/**
	 * Permet de reduire la population courrante a un echantillon determiner aleatoirement
	 * @param pourcentage Pourcentage de l'effectif de la population de depart qui determinera la taille de l'echantillon qui deviendra la nouvelle
	 * population courrante
	 */
	protected void selectionRandom (int pourcentage) {
		int nbSelection = (int) Math.ceil(this.currentPop.effectif*(pourcentage/100.0));
		if(nbSelection<2) {
			throw new IllegalArgumentException("La selection aleatoire ne laisse pas assez d'individu dans la population");
		}
		if(nbSelection>currentPop.effectif) {
			throw new IllegalArgumentException("Impossible de selectionner plus d'element qu'il n'y en a dans la population");
		}
		for(int i=0; i<currentPop.effectif-nbSelection; i++) {
			currentPop.remove(gen.nextInt(effectif - i));
		}
	}
	
	/**
	 * Cette selection reduit la population courrante a un echantillon de celle-ci, dans un premier temps par une selection aleatoire des individus
	 * et dans un second temps en conservant les elements ayant la performance la plus faible
	 * @return Inutile
	 */
	protected int selection_v1() {
		selectionRandom(pourcentageRandom);
		int nbSelection = (int) Math.ceil(this.currentPop.effectif*(pourcentageReal/100.0));
		if(nbSelection<2) {
			throw new IllegalArgumentException("La selection ne laisse pas assez d'individu dans la population");
		}
		if(nbSelection>currentPop.effectif) {
			throw new IllegalArgumentException("Impossible de selectionner plus d'element qu'il n'y en a dans la population");
		}
		currentPop.setAllPerf(reference);
		Collections.sort(currentPop.ensemble);
		for(int j = currentPop.effectif-1; j >=nbSelection; j--) {
			currentPop.remove(j);
		}
		return -1;
	}
	
	/**
	 * Reduit la population courrante a un unique individu, celui ayant la perofrmance la plus faible
	 * @return
	 */
	protected int selection_v2() {
		currentPop.setAllPerf(reference);
		Collections.sort(currentPop.ensemble);
		for(int j = currentPop.effectif-1; j != 0; j--) {
			currentPop.remove(j);
		}
		return -1;
	}
	
	/**
	 * Determine les meilleurs individus de la population courrante
	 * @return Renvoie la liste des meilleurs individus
	 */
	protected ArrayList<Individu> getChampions_v1() {
		if(nbChampions>currentPop.effectif) {
			throw new IllegalArgumentException("Impossible de determiner plus de champions qu'il n'ya d'individu dans la population");
		}
		ArrayList<Individu> champions = new ArrayList<Individu>();
		Collections.sort(currentPop.ensemble);
		for(int i = 0; i<nbChampions; i++) {
			champions.add(currentPop.ensemble.get(i));
		}
		return champions;
	}

	
	/**
	 * Modifie un individu place en argument, dans un premier temps en modifiant les coordonnees des points de ses polygones,
	 * si le polygone est est un triangle, ses points sont deplaces aleatoirement, sinon les points sont deplaces en maintenant la convexite 
	 * du polygone. Dans un second temps, les polygones de l'individu seront eventuellement translatees, c'est a dire que tous leurs points
	 * seront deplaces celon le meme vecteur. Enfin la couleur et l'opacite de chaque polygone seront eventuellement modifies
 	 * @param source Il s'agit de l'individu a partir duquel on obtiendra l'individu modifie
	 * @return Renvoie l'individu modifie
	 */
	protected Individu mutation_v1(Individu source){
			ArrayList<ConvexPolygon> adnMute = new ArrayList<ConvexPolygon>();
			for(ConvexPolygon c : source.adn) {
				ConvexPolygon i = c.clonage();
				if(nbPoints == 3) {
					i = mutationTriangle(i);
				}
				else{
					i = mutationPoint(i);
				}
				if (gen.nextDouble() < probaTranslation) {
					i= translation (i, montantTranslation);
				}
				if(gen.nextDouble() < probaColor) {
					i.b = (int) Math.min(255.0, Math.max(0.0, (i.b + ((-1 + gen.nextDouble() * 2) * montantColor))));
				}
				if(gen.nextDouble() < probaColor) {
					i.g = (int) Math.min(255.0, Math.max(0.0, (i.g + ((-1 + gen.nextDouble() * 2) * montantColor))));
				}
				if(gen.nextDouble() < probaColor) {
					i.r = (int) Math.min(255.0, Math.max(0.0, (i.r + ((-1 + gen.nextDouble() * 2) * montantColor))));
				}
				i.setFill(Color.rgb(i.r, i.g, i.b));
				if(gen.nextDouble() < probaOpa) {
					i.setOpacity(Math.min(1.0, Math.max(0.0, i.getOpacity() + (-1 + gen.nextDouble() * 2) * montantOpa)));
				}
				adnMute.add(i);
			}
			return new Individu(adnMute);
			
		}
	
	
	/**
	 * Modifie un individu place en argument en remplacant certains de ses polygones par des polygones generes aleatoirement
 	 * @param source Il s'agit de l'individu a partir duquel on obtiendra l'individu modifie
	 * @return Renvoie l'individu modifie
	 */
	protected Individu mutation_v2(Individu source) {
		ArrayList<ConvexPolygon> adnMute = new ArrayList<ConvexPolygon>();
		for(ConvexPolygon i : source.adn) {
			if(gen.nextDouble()<proba) {
				adnMute.add(new ConvexPolygon(nbPoints));
			}
			else {
				adnMute.add(i);
			}
		}
		return new Individu(adnMute);
	}
	
	
	/**
	 * Modifie l'individu place en argument, si l'individu ne possede pas autant de polygones qu'il le pourrait, on lui en rajoute un genere aleatoirement
 	 * @param source Il s'agit de l'individu a partir duquel on obtiendra l'individu modifie
	 * @return Renvoie l'individu modifie
	 */
	protected Individu mutation_v3(Individu source) {
		Individu indi = source.clonage();
		if(indi.adn.size()<nbPoly) {
			indi.adn.add(new ConvexPolygon(nbPoints));
		}
		return indi;
	}

	/**
	 * Modifie l'individu place en argument de la meme facon que la mutation_v1 a ceci pret que seul le dernier polygone de la liste de polygones
	 * que contient l'individu peut etre mute
 	 * @param source Il s'agit de l'individu a partir duquel on obtiendra l'individu modifie
	 * @return Renvoie l'individu modifie
	 */
	protected Individu mutation_v4(Individu source){
		ArrayList<ConvexPolygon> adnMute = new ArrayList<ConvexPolygon>();
		for(int j = 0; j<source.adn.size() ; j++) {
			ConvexPolygon i = source.adn.get(j).clonage();
			if (j == source.adn.size()-1) {
				if(nbPoints == 3) {
					i = mutationTriangle(i);
				}
				else{
					i = mutationPoint(i);
				}
				if (gen.nextDouble() < probaTranslation) {
					i= translation (i, montantTranslation);
				}
				if(gen.nextDouble() < probaColor) {
					i.b = (int) Math.min(255.0, Math.max(0.0, (i.b + ((-1 + gen.nextDouble() * 2) * montantColor))));
				}
				if(gen.nextDouble() < probaColor) {
					i.g = (int) Math.min(255.0, Math.max(0.0, (i.g + ((-1 + gen.nextDouble() * 2) * montantColor))));
				}
				if(gen.nextDouble() < probaColor) {
					i.r = (int) Math.min(255.0, Math.max(0.0, (i.r + ((-1 + gen.nextDouble() * 2) * montantColor))));
				}
				i.setFill(Color.rgb(i.r, i.g, i.b));
				if(gen.nextDouble() < probaOpa) {
					i.setOpacity(Math.min(1.0, Math.max(0.0, i.getOpacity() + (-1 + gen.nextDouble() * 2) * montantOpa)));
				}
			}
			adnMute.add(i);
		}
		return new Individu(adnMute);
		
	}

	
	
	//ne sert que pour l'apparition dun nouveaux polygon
	protected ConvexPolygon mutationPoly (ConvexPolygon i) {
		i = mutationPoint(i);
		if (gen.nextDouble() < probaTranslation) {
			i= translation (i, montantTranslation);
		}
		if(gen.nextDouble() < probaColor) {
			i.b = (int) Math.min(255.0, Math.max(0.0, (i.b + ((-1 + gen.nextDouble() * 2) * montantColor))));
		}
		if(gen.nextDouble() < probaColor) {
			i.g = (int) Math.min(255.0, Math.max(0.0, (i.g + ((-1 + gen.nextDouble() * 2) * montantColor))));
		}
		if(gen.nextDouble() < probaColor) {
			i.r = (int) Math.min(255.0, Math.max(0.0, (i.r + ((-1 + gen.nextDouble() * 2) * montantColor))));
		}
		i.setFill(Color.rgb(i.r, i.g, i.b));
		i.setOpacity(0.1);
		return i;
	}
	
	
	/**
	 * Permet de translater un polygone , c'est a dire de deplacer tous ses points selon le meme vecteur
	 * @param poly Il s'agit du polygone a translater
	 * @param max Il s'agit du deplacement maximum que puisse subir le polygone sur x comme sur y
	 * @return Renvoie le Polygone translate
	 */
	protected ConvexPolygon translation(ConvexPolygon poly, double max) {
		ArrayList<Double> abs = new ArrayList<Double>();
		ArrayList<Double> ord = new ArrayList<Double>();
		for (int i = 0; i < poly.getPoints().size(); i++) {
			if (i%2 == 0) {
				abs.add(poly.getPoints().get(i));
			}
			else {
				ord.add(poly.getPoints().get(i));
			}
		}
		double minx, miny, maxx, maxy;
		minx = Collections.min(abs);
		miny = Collections.min(ord);
		maxx = Collections.max(abs);
		maxy = Collections.max(ord);
		double mvtX = (-1 + gen.nextDouble() * 2)*max;
		double mvtY = (-1 + gen.nextDouble() * 2)*max;
		for (int i = 0; i < poly.getPoints().size(); i++) {
			double p = poly.getPoints().get(i);
			if (i%2==0) {//pour les x
				poly.getPoints().set(i, p + Math.max(0.0-minx + 1, Math.min( mvtX, ConvexPolygon.max_X - maxx - 1)));
			}
			else {//pour les y
				poly.getPoints().set(i, p + Math.max(0.0-miny + 1, Math.min( mvtY, ConvexPolygon.max_Y - maxy - 1)));
			}
		}
		return poly;
	}
	
	/**
	 * Permet de modifier l'emplacement des points d'un triangle
	 * @param poly Il s'agit du triangle a muter
	 * @return Renvoie le nouveau triangle
	 */
	protected ConvexPolygon mutationTriangle(ConvexPolygon poly) {
		for(int j = 0; j < poly.getPoints().size(); j++) {
			double tirage = gen.nextDouble();
			if(tirage < proba) {
				if(j%2 == 0) {
					poly.getPoints().set(j, Math.min(ConvexPolygon.max_X, Math.max(0, poly.getPoints().get(j) + (gen.nextDouble()*2-1) *  montantPoint))); // A changer
				}
				else {
					poly.getPoints().set(j, Math.min(ConvexPolygon.max_Y, Math.max(0, poly.getPoints().get(j) + (gen.nextDouble()*2-1) *  montantPoint))); // A changer

				}
			}
		}
		return poly;
	}
	
	/**
	 * Permet de modifier l'emplacement des points d'un polygone tout en maintenant sa convexite 
	 * @param poly Il s'agit du polygone a muter
	 * @return Renvoie le nouveau polygone
	 */
	protected ConvexPolygon mutationPoint(ConvexPolygon poly) {
		for(int j = 0; j < poly.getPoints().size(); j+=2) {
			double tirage = gen.nextDouble();
			if(tirage < proba) {
				double [] valeurs = valeurMutation(poly , j);
				poly.getPoints().set(j, valeurs[0]); // A changer
				poly.getPoints().set(j+1, valeurs[1]);
			}
		}
		return poly;
	}	
	
	
	/**
	 * Permet de determiner aleatoirement de nouvelle coordonnees pour un point d'un polygone tout en maintenant la convexite du polygone
	 * Cette methode n'etant pas facile a explique par ecrit elle sera expliquee lors de la soutenance ou dans le rapport
	 * @param poly Il s'agit du polygone auquel on s'interesse
	 * @param index Il s'agit de l'indice correspondant a la valeur d'abcisse du point que l'on veut deplacer dans le tableau des points du polygone
	 * @return Renvoie les nouvelles coordonees du point
	 */
	protected double [] valeurMutation(ConvexPolygon poly, int index) {
		double[] d1 = new double[2];
		double[] d2 = new double[2];
		double[] d3 = new double[2];
		int n = poly.getPoints().size();
		
		// determination d1
		d1[0] = (poly.getPoints().get((index+3+n)%n) - poly.getPoints().get((index-1+n)%n))/
				(poly.getPoints().get((index+2+n)%n) - poly.getPoints().get((index-2+n)%n));
		
		d1[1] = poly.getPoints().get((index+3+n)%n) - d1[0] * poly.getPoints().get((index+2+n)%n);
		
		
		d2[0] = (poly.getPoints().get((index+3+n)%n) - poly.getPoints().get((index+5+n)%n))/
				(poly.getPoints().get((index+2+n)%n) - poly.getPoints().get((index+4+n)%n));
		
		d2[1] = poly.getPoints().get((index+3+n)%n) - d2[0] * poly.getPoints().get((index+2+n)%n);
		
		
		d3[0] = (poly.getPoints().get((index-1+n)%n) - poly.getPoints().get((index-3+n)%n))/
				(poly.getPoints().get((index-2+n)%n) - poly.getPoints().get((index-4+n)%n));
		
		d3[1] = poly.getPoints().get((index-1+n)%n) - d3[0] * poly.getPoints().get((index-2+n)%n);
		
		double[] dp1 = new double[2];
		double[] dp2 = new double[2];
		double[] dp3 = new double[2];
		
		dp1[0] = -1/d1[0];
		dp2[0] = -1/d2[0];
		dp3[0] = -1/d3[0];
		
		dp1[1] = poly.getPoints().get((index+1)%n) - dp1[0] * poly.getPoints().get((index)%n);

		dp2[1] = poly.getPoints().get((index+1)%n) - dp2[0] * poly.getPoints().get((index)%n);

		dp3[1] = poly.getPoints().get((index+1)%n) - dp3[0] * poly.getPoints().get((index)%n);

		double[] p1 = new double[2];
		double[] p2 = new double[2];
		double[] p3 = new double[2];
		
		p1[0] = (dp1[1] - d1[1])/(d1[0]-dp1[0]);
		p1[1] = p1[0] * dp1[0] + dp1[1];
		
		p2[0] = (dp2[1] - d2[1])/(d2[0]-dp2[0]);
		p2[1] = p2[0] * dp2[0] + dp2[1];
		
		p3[0] = (dp3[1] - d3[1])/(d3[0]-dp3[0]);
		p3[1] = p3[0] * dp3[0] + dp3[1];
		
		double[][] tabPoints = {p1, p2, p3};
		double res = -1;
		for (int i = 0; i<3; i++) {
			double[] point = tabPoints[i];
			double temp = Math.sqrt(Math.pow(point[0]-poly.getPoints().get(index), 2) + Math.pow(point[1] - poly.getPoints().get(index+1), 2));
			if( temp < res || res < 0) {
				res = temp;
			}
		}
		double X = res*Math.pow(gen.nextDouble(), 3)*Math.cos(gen.nextDouble()*Math.PI*2);
		double Y = res*Math.pow(gen.nextDouble(), 3)*Math.sin(gen.nextDouble()*Math.PI*2);
		double [] resultat = {Math.min(100.0, Math.max(0.0, X + poly.getPoints().get(index))),Math.min(150.0, Math.max(0.0, Y + poly.getPoints().get(index+1)))};
		return resultat;
	}
	
	
	/**
	 * Permet de croiser deux individu afin d'en creer un nouveau, le nouvel individu est cree en recuperant aleatoirement des polygones de ses parents
	 * @param pere Il s'agit de l'un des individus parents
	 * @param mere Il s'agit de l'un des individus parents
	 * @return Renvoie le nouvel individu
	 */
	protected Individu croisement_v1(Individu pere, Individu mere) {
		if(pere == mere) {
			return pere;
		}
		ArrayList<ConvexPolygon> adnFils = new ArrayList<ConvexPolygon>();
		for(int i = 0; i < pere.adn.size(); i++) {
			int random = gen.nextInt(2);
			if(random == 0) {
				adnFils.add(pere.adn.get(i));
			}
			else {
				adnFils.add(mere.adn.get(i));
			}
		}
		return new Individu(adnFils);
	}
	
	
	/**
	 * Permet de choisir deux individus dans la population courrante dans le but de les faire se reproduire. Les individus sont choisis aleatoirement
	 * mais leur chance d'être selectionne depend de leur performance, plus elle est faible, meilleur sont leur chance
	 * @return Renvoie le couple d'individus choisis
	 */
	protected Individu[] getReproducteurs_v1() {
		if(currentPop.effectif == 1) {
			Individu[] res = {currentPop.ensemble.get(0), currentPop.ensemble.get(0)};
			return res;
		}
		
		ArrayList<Double> tabProb = new ArrayList<Double>();
		double sumPerf = 0;
		for(Individu i : currentPop.ensemble) {
			sumPerf += 1/Math.pow((i.performance + 1), expoErreur);
		}
		double res = 0;
		for(Individu i : currentPop.ensemble) {
			res += (1/Math.pow(i.performance+1, expoErreur))/sumPerf;
			tabProb.add(res);
		}
		
		double random = gen.nextDouble();
		int indexPere = 0;
		while(random < tabProb.get(indexPere) && indexPere != tabProb.size()-1) {
			indexPere +=1;
		}
		double[] bornes = new double[2];
		if(indexPere ==0) {
			bornes[0] = 0;
			bornes[1] = tabProb.get(1);
		}
		else {
			bornes[0] = tabProb.get(indexPere-1);
			bornes[1] = tabProb.get(indexPere);
			if(indexPere == tabProb.size()-1) {
				bornes[1] = 1;
			}
		}
		while( bornes[0]<random && random<=bornes[1]) {
			random = gen.nextDouble();
		}
		
		int indexMere = 0;
		while(random < tabProb.get(indexMere) && indexMere != tabProb.size()-1) {
			indexMere +=1;
		}
		
		Individu[] parents = {currentPop.ensemble.get(indexPere), currentPop.ensemble.get(indexMere)};
		
		return parents;
	}
		
	/**
	 * Permet de choisir deux individus dans la population courrante dans le but de les faire se reproduire. Les individus sont choisis aleatoirement
	 * independament de leur performance
	 * @return Renvoie le couple d'individus choisis
	 */
	protected Individu[] getReproducteurs_v2() {
		if(currentPop.effectif == 1) {
			Individu[] res = {currentPop.ensemble.get(0), currentPop.ensemble.get(0)};
			return res;
		}
		int random1 = gen.nextInt(currentPop.effectif);
		int random2 = gen.nextInt(currentPop.effectif);
		while(random1 == random2) {
			random2 = gen.nextInt(currentPop.effectif);
		}
		Individu[] res = {currentPop.ensemble.get(random1), currentPop.ensemble.get(random2)};
		return res;
	}

	
	/**
	 * Cette methode permet de rajouter un nouveau polygone aleatoire a l'ensemble des individus d'une population
	 * @param source Il s'agit de la population a modifier
	 * @return Renvoie la nouvelle population

	 */
	protected Population developpement (Population source) {
		ArrayList<Individu> res = new ArrayList<Individu>();
		for (Individu i : source.ensemble) {
			Individu indi = i.clonage();
			indi.adn.add(mutationPoly(new ConvexPolygon(nbPoints)));
			res.add(indi);
			}
		return (new Population(res));
	}
	
	
	/**
	 * Cette methode permet de rajouter un nouveau polygone a l'ensemble des individus d'une population en placant le nouveau polygone dans une
	 * sous-zone de l'image ou la performance est particulierement mauvaise
	 * @param source Il s'agit de la population a modifier
	 * @return Renvoie la nouvelle population
	 */
	protected Population developpement_v2 (Population source) {
		ArrayList<Individu> res = new ArrayList<Individu>();
		for (Individu i : source.ensemble) {
			Individu indi = i.clonage();
			if(i.adn.size()<nbPoly) {
				indi.adn.add(new ConvexPolygon (nbPoints, i.setPerfZoneGlissade(reference, 3)));
			}
			res.add(indi);
			}
		return (new Population(res));
	}
	
	
	/**
	 * Permet a partir de la population courrante de combiner les differentes methodes de selection, de reproduction de mutation etc.. placees
	 * en argument et ce un certains nombre de fois place en argument.
	 * Le deroulement d'une iteration de cette algorithme ce deroule de la facon suivante : 
	 * dans un premier temps on effectue une selection sur la population courrante, puis on recupere les meilleurs elements de cette selection
	 * et on les places directement dans la population initiale de la prochaine iteration, ensuite on ajoute a la futur population initiale de la
	 * prochaine iteration des individus obtenus par croisement et mutation des elements de notre selection jusqu'a reremplir notre population
	 * puis on recommence. Lorsque toutes les iterations ont etes effectuees, on recupere le meilleur individu de la derniere population et on le renvoie. 
	 * @param selection Il s'agit de la methode de selection choisie pour cet methode
	 * @param getReproducteurs Il s'agit de la methode parlaquel seront choisis les individus qui se reproduiront dans cette methode
	 * @param croisement Il s'agit de la methode de croisement d'individus choisie pour cette methode
	 * @param mutation Il s'agit de la methode de mutation d'individu choisie pour cette methode
	 * @param getChampions Il s'agit de la methode qui determine les elements qui seront directement ajoutees a la generation suivante
	 *  choisie pour cette methode
	 * @param nbIteration Il s'agit du nombre de fois que la methode va repeter l'algorithme
	 * @return Renvoie le meilleur individu de la population finale
	 */
	protected Individu handleSimulation_v1(SoloFunction<Integer> selection, SoloFunction<Individu[]> getReproducteurs,
			BiFunction<Individu, Individu, Individu> croisement, Function<Individu, Individu> mutation, 
			SoloFunction<ArrayList<Individu>> getChampions, int nbIteration){
		
		for(int i=0; i<nbIteration; i++) {
			System.out.println("Iteration numero : " + i);
			selection.apply();
			ArrayList<Individu> futurIndividus = new ArrayList<Individu>();
			futurIndividus.addAll(getChampions.apply());
			System.out.println("  La meilleur performance pour cette generation est : " +  currentPop.ensemble.get(0).performance);
			while(futurIndividus.size() < effectif) {
				Individu[] parents = getReproducteurs.apply();
				Individu temp = mutation.apply(croisement.apply(parents[0], parents[1]));
				futurIndividus.add(temp);
			}
			currentPop = new Population(futurIndividus);

		}
		
		currentPop.setAllPerf(reference);
		Collections.sort(currentPop.ensemble);
		return currentPop.ensemble.get(0);
	}
	
	
	/**
	 * Cette methode fonctionne selon la facon suivante : on commence avec une population d'individu possedant peu de polygones
	 * on fait tourner le meme algorithme que pour handlSimulation_v1 jusqu'a ce que pendant un certains nombre d'iterations la meilleur performance
	 * de la population ne change pas, on rajoute alors un nouveau polygone et on recommence
	 * @param selection Il s'agit de la methode de selection choisie pour cet methode
	 * @param getReproducteurs Il s'agit de la methode parlaquel seront choisis les individus qui se reproduiront dans cette methode
	 * @param croisement Il s'agit de la methode de croisement d'individus choisie pour cette methode
	 * @param mutation Il s'agit de la methode de mutation d'individu choisie pour cette methode
	 * @param getChampions Il s'agit de la methode qui determine les elements qui seront directement ajoutees a la generation suivante
	 *  choisie pour cette methode
	 * @param nbIteration Il s'agit du nombre de fois que la methode va repeter l'algorithme
	 * @return Renvoie le meilleur individu de la population finale
	 */
	protected Individu handleSimulation_v2(SoloFunction<Integer> selection, SoloFunction<Individu[]> getReproducteurs,
			BiFunction<Individu, Individu, Individu> croisement, Function<Individu, Individu> mutation, 
			SoloFunction<ArrayList<Individu>> getChampions, int nbIteration){
		
		double perfSoFar = 10000.0;
		double perfbefore = 10000.0;
		while(nbPoly <= nbPolyMax) {
			perfbefore = perfSoFar;
			perfSoFar = 10000.0;
			int cp = 0;
			while(cp < nbIteration) {
				System.out.println("cp egale "+cp);
				selection.apply();
				ArrayList<Individu> futurIndividus = new ArrayList<Individu>();
				futurIndividus.addAll(getChampions.apply());
				
				System.out.println("La meilleur performance pour cette generation est : " +  currentPop.ensemble.get(0).performance);
				System.out.println("nb de poly "+currentPop.ensemble.get(0).adn.size());
				while(futurIndividus.size() < effectif) {
					Individu[] parents = getReproducteurs.apply();
					Individu temp = mutation.apply(croisement.apply(parents[0], parents[1]));
					futurIndividus.add(temp);
				}
				currentPop = new Population(futurIndividus);
				currentPop.setAllPerf(reference);
				Collections.sort(currentPop.ensemble);
				double perfmax = currentPop.ensemble.get(0).performance;
				if (perfmax >= perfSoFar && perfSoFar < perfbefore) {
					cp++;
				}
				else {
					perfSoFar = perfmax;
					cp = 0;
				}
			}

			System.out.println("NBPOLY "+nbPoly+" ADN SIZE "+currentPop.ensemble.get(1).adn.size());
			currentPop = developpement_v2(currentPop);	
			nbPoly++;
		}
		currentPop.setAllPerf(reference);
		Collections.sort(currentPop.ensemble);
		return currentPop.ensemble.get(0);
	}
	
	
	/**
	 * Cette methode commence par cree une population d'un certain effectif d'individus ne contenant qu'un seul polygone et en fait la population courrante,
	 * ensuite a chaque iteration, on reduit la population courrante a son meilleur element, puis on remplit la population initiale de la 
	 * prochaine iteration en mutant ce meilleur element et on recommence. Cet methode effectue un nombre d'iteration egal au nombre de polygone
	 * maximum que peut contenir un individu car elle est prevu pour etre utilisee avec une mutation qui rajoute un polygone aleatoire a 
	 * l'individu source. Lorsque toutes les iterations sont finis, on renvoie le meilleur individu de la derniere population
	 * @param selection Inutile ici
	 * @param getReproducteurs Inutile ici
	 * @param croisement Inutile ici
	 * @param mutation Il s'agit de la methode de mutation d'individu choisie pour cet methode
	 * @param getChampions Il s'agit de la methode qui determine les elements qui seront directement ajoutees a la generation suivante
	 *  choisie pour cet methode
	 * @param nbIteration Inutile ici
	 * @return Renvoie le meilleur individu de la population finale
	 */
	protected Individu handleSimulation_v3(SoloFunction<Integer> selection, SoloFunction<Individu[]> getReproducteurs,
			BiFunction<Individu, Individu, Individu> croisement, Function<Individu, Individu> mutation, 
			SoloFunction<ArrayList<Individu>> getChampions, int nbIteration) {
		int temp = nbChampions;
		nbChampions = 1;
		ArrayList<Individu> listIndiv = new ArrayList<Individu>();
		for(int i=0; i<effectif; i++) {
			ArrayList<ConvexPolygon> cp = new ArrayList<ConvexPolygon>();
			cp.add(new ConvexPolygon(nbPoints));
			listIndiv.add(new Individu(cp)); 
		}
		currentPop = new Population(listIndiv);
		for(int i=1; i<nbPoly; i++) {
			System.out.println("Iteration numero : " + i);
			ArrayList<Individu> listPop = new ArrayList<Individu>();
			currentPop.setAllPerf(reference);
			Individu indiv = getChampions.apply().get(0);
			System.out.println("La performance maximale pour cette iteration est de : " + indiv.performance);

			for(int j = 0; j<effectif; j++) {
				listPop.add(mutation.apply(indiv));
			}
			currentPop = new Population(listPop);
		}
		nbChampions = temp;
		Collections.sort(currentPop.ensemble);
		return currentPop.ensemble.get(0);
	}
	
	
	
	/**
	 * Cette methode est la methode qui lance tout l'algorithme, elle permet de creer une population initiale selon une certaine methode que 
	 * l'on appelera secondaire, d'utiliser une certaine methode que l'on appelera principale sur cette population afin d'obtenir une solution
	 * et d'ameliorer cette solution selon une certaine methode
	 * @param nbUpgrade Il s'agit du nombre de fois que la solution finale sera amelioree
	 * @param nbIteration Il s'agit du nombre d'iteration qu'effectuera la methode principale
	 * @param nbIteration2 Il s'agit du nombre d'iteration qu'effectuera la methode secondaire
	 * @param effectifConstitution Il s'agit de l'effectif des populations initiales utilisees par la methode secondaire
	 * @param nbCree Il s'agit du nombre d'individu qui seront crees manuellement lors de la creation de la population initiale
	 * @param mainMethod Il s'agit de la methode principale, cette methode sera par exemple handleSimulation_v1
	 * @param methodConstitution Il s'agit de la methode qui sera utilisee pour creer la population de depart, cette methode sera par exemple handleSimulation_v1
	 * @param selection Il s'agit de la methode de selection utilisee par la methode principale
	 * @param getReproducteurs Il s'agit de la methode de choix des reproducteurs utilisee par la methode principale
	 * @param croisement Il s'agit de la methode de croisement utilisee par la methode principale
	 * @param mutation Il s'agit de la methode de mutation utilisee par la methode principale
	 * @param getChampions Il s'agit de la methode determination des champions utilisee par la methode principale
	 * @param selection2 Il s'agit de la methode de selection utilisee par la methode secondaire
	 * @param getReproducteurs2 Il s'agit de la methode de chois des reproducteurs utilisee par la methode secondaire
	 * @param croisement2 Il s'agit de la methode de croisement utilisee par la methode secondaire
	 * @param mutation2 Il s'agit de la methode de mutation utilisee par la methode secondaire
	 * @param getChampions2 Il s'agit de la methode de determination des champions utilisee par la methode secondaire
	 * @param upgrade Il s'agit de la methode utilisee pour ameliorer l'individu obtenu grace a la methode principale
	 * @return Renvoie l'individu resultat
	 */
	protected Individu launcher(int nbUpgrade, int nbIteration, int nbIteration2, int effectifConstitution, int nbCree,
			SixFunction<SoloFunction<Integer>, SoloFunction<Individu[]>, BiFunction<Individu, Individu, Individu>, Function<Individu, Individu>, 
			SoloFunction<ArrayList<Individu>>,Integer, Individu> mainMethod,
			
			SixFunction<SoloFunction<Integer>, SoloFunction<Individu[]>, BiFunction<Individu, Individu, Individu>, Function<Individu, Individu>, 
			SoloFunction<ArrayList<Individu>>,Integer, Individu> methodConstitution,
			
			SoloFunction<Integer> selection, SoloFunction<Individu[]> getReproducteurs, BiFunction<Individu, Individu, Individu> croisement,
			Function<Individu, Individu> mutation, SoloFunction<ArrayList<Individu>> getChampions, SoloFunction<Integer> selection2, 
			SoloFunction<Individu[]> getReproducteurs2, BiFunction<Individu, Individu, Individu> croisement2, 
			Function<Individu, Individu> mutation2, SoloFunction<ArrayList<Individu>> getChampions2,
			BiFunction<Individu, Integer, Individu> upgrade) {
		
		Population popInit = constitutePopulation(nbCree, methodConstitution, selection2, getReproducteurs2, croisement2, mutation2, getChampions2, nbIteration2, effectifConstitution);
		System.out.println("Constitution de la population initiale terminee");
		currentPop = popInit;
		Individu res = mainMethod.apply(selection, getReproducteurs, croisement, mutation, getChampions, nbIteration);
		System.out.println("Fin de l'execution de la methode principale");
		res.setPerf(reference);
		res = upgrade.apply(res, nbUpgrade);
		return res;
	}
	
	/**
	 * Cette methode permet de creer une population a partir d'individu qui ont ete cree via des methodes telles que les differentes versions
	 * de handleSimulation. La methode utilise, a partir d'une population aleatoire, une methode passee en argument pour creer un nombre 
	 * d'individu passe en argument qui seront integres a la population renvoyee. S'il la population n'est pas rempli a la fin on y 
	 * ajoute des individus aleatoires
	 * @param nbCree Il s'agit du nombre d'individu dans la population renvoyee a creer en utilisant la methode method passee en argument
	 * @param method Il s'agit de la methode qui sera utilise pour creer les individus
	 * @param selection Il s'agit de la methode de selection qu'utilisera method
	 * @param getReproducteurs Il s'agit de la methode de choix des reproducteurs qu'utilisera method
	 * @param croisement Il s'agit de la methode de croisement qu'utilisera method
	 * @param mutation Il s'agit de la methode de mutation qu'utilisera method
	 * @param getChampions Il s'agit de la methode de determination des champions qu'utilisera method
	 * @param nbIteration Il s'agit du nombre d'iterations qu'effectuera method
	 * @param effectif Il s'agit de l'effectif des populations generes aleatoirement utilises dans method
	 * @return Renvoie un nouvelle population
	 */
	protected Population constitutePopulation(int nbCree, 
			SixFunction<SoloFunction<Integer>, SoloFunction<Individu[]>, BiFunction<Individu, Individu, Individu>, Function<Individu, Individu>, SoloFunction<ArrayList<Individu>>,Integer, Individu> method,
			SoloFunction<Integer> selection, SoloFunction<Individu[]> getReproducteurs,
			BiFunction<Individu, Individu, Individu> croisement, Function<Individu, Individu> mutation, 
			SoloFunction<ArrayList<Individu>> getChampions, int nbIteration, int effectif) {
		ArrayList<Individu> listIndiv = new ArrayList<Individu>();
		if(nbCree != 0) {
			int temp = this.effectif;
			this.effectif = effectif;
			for(int i=0; i<nbCree; i++) {
				currentPop = new Population(effectif, nbPoly, nbPoints);
				listIndiv.add(method.apply(selection, getReproducteurs, croisement, mutation, getChampions, nbIteration));
				System.out.println("Fin de la creation de l'individu : " + i);
			}
			this.effectif = temp;
			while(listIndiv.size()<effectif){
				listIndiv.add(new Individu(nbPoly, nbPoints));
			}
		}
		else {
			return new Population(this.effectif, nbPoly, nbPoints);
		}
		return new Population(listIndiv);
	}
	
	/**
	 * Cette methode permet d'ameliorer un individu en generant pour chaque polygone de l'individu source un certain nombre de polygones aleatoires
	 * qui remplaceront le polygone actuel si cela permet d'obtenir une meilleur performance
	 * @param indivInit Il s'agit de l'individu a ameliorer
	 * @param nbUpgrade Il s'agit du nombres de polygones alternatives qui seront generes pour remplace chaque polygone de l'individu source
	 * @return Renvoie l'individu ameliore
	 */
	protected Individu upgrade_v1(Individu indivInit, int nbUpgrade) {
		if(nbUpgrade >0) {
			for(int i= 0; i<indivInit.adn.size(); i++) {
				double perf = indivInit.performance;
				ConvexPolygon temp = indivInit.adn.get(i).clonage();
				for(int j=0 ; j<nbUpgrade; j++) {
					indivInit.adn.set(i, new ConvexPolygon(nbPoints));
					indivInit.setPerf(reference);
					if(indivInit.performance<perf) {
						perf = indivInit.performance;
						temp = indivInit.adn.get(i).clonage();
					}
				}
				indivInit.adn.set(i, temp);
				indivInit.performance = perf;
				System.out.println("Fin de l'amelioration du polygone numero : " + i + "   La performance est desormais de " + perf);
			}
		}
		return indivInit;
	}
	
	
}
	interface SixFunction<One, Two, Three, Four, Five, Six, Seven>{
		public Seven apply(One one, Two two, Three three, Four four, Five five, Six six);
	}
	interface SoloFunction<One> {
	    public One apply();
	}




