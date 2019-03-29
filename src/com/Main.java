package com;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;



/**
 * Classe dans laquelle est execute le programme. Des exemples prets a etre execute des principales methodes utilisees on ete prepares a l'avance
 *
 */
public class Main extends Application {
	public static void main(String[] args){
		Application.launch(args);
	}
	
	
	
	public void start(Stage myStage){
		
//		////////////////////////Ceci vous permettra d'utiliser n'importe quel algorithme utilisable avec notre code//////////////////////////////
//		
//		
//		String path = "C:\\Users\\alexi\\Desktop\\monaLisa-100.jpg";
//		int nbIteration1 = 500; // Nombre d'iteration que fera la methode principale
//		int nbIteration2 = 100; // Nombre d'iteration utilise lors de la creation de la population initiale
//		int nbUpgrade = 500; // Nombre d'alternatives proposees lors de l'upgrade
//		int effectifConstitution = 50; // Effectif des populations lors des creations manuels des individus pour la population initiale
//		int nbCreeInit = 20; // Nombre d'Individu qui seront cree manuellment pour former la population initiale
//		int maxX = 100; // Taille horizontale de l'image source
//		int maxY = 149; // Taille verticale de l'image source
//		int effectif = 50; // Nombre d'individus etudies
//		int nbPoly = 50; // Nombre maximal de polygone pour un individu
//		int nbPoints = 5; //Nombre de points maximul que peuvent avoir les polygones
//		double proba = 0.2; // Probabilite que l'emlacement des points d'un polygone soient modifies lors d'une mutation
//		double probaColor = 0.2; // Probabilite que les couleurs soient modifiees lors d'une mutation
//		double probaOpa = 0.3; // Probabilite que l'opacite soit modifiee lors d'une mutation
//		double probaTranslation = 0.2; // Probabilite qu'une translation soit effectuee lors d'une mutation
//		double montantTranslation = 10; // Montant maximal du changement par translation lors d'une mutation
//		double montantPoint = 10; // Montant maximal du changement de la position d'un point lors d'une mutation sur un triangle
//		double montantColor = 30; // Montant maximal du changement des couleurs lors d'une mutation
//		double montantOpa = 0.15; // Montant maximal du changement de l'opacite lors d'une mutation
//		int expoErreur = 2; //Exposant auquel place la performance lors de la determination des reproducteurs
//		int pourcentageRandom = 100; // Pourcentage de la population qui sera preserve apres selection aleatoire
//		int pourcentageReal = 20; //Pourcentage de la population qui sera conserver apres selection
//		int nbChampions = 3; //Nombre de champions
//		
//		Gestionnaire gest = new Gestionnaire(maxX, maxY, probaTranslation, montantTranslation, pourcentageRandom, pourcentageReal, nbChampions, effectif, nbPoly, nbPoints,
//				path, proba, probaColor, probaOpa, montantPoint, montantColor, montantOpa, expoErreur);
//		
//		
//		SixFunction<SoloFunction<Integer>, SoloFunction<Individu[]>, BiFunction<Individu, Individu, Individu>, Function<Individu, Individu>, 
//		SoloFunction<ArrayList<Individu>>,Integer, Individu> mainMethod = gest::handleSimulation_v1; //Methode d'execution principal
//		
//		SixFunction<SoloFunction<Integer>, SoloFunction<Individu[]>, BiFunction<Individu, Individu, Individu>, Function<Individu, Individu>, 
//		SoloFunction<ArrayList<Individu>>,Integer, Individu> methodConstitution = gest::handleSimulation_v1; //Methode utilisee pour creer la population de depart
//		
//		
//		/////////////////////////////////////Methodes utilisees dans mainMethod///////////////////////
//		SoloFunction<Integer> selection = gest::selection_v1; 
//		SoloFunction<Individu[]> getReproducteurs = gest::getReproducteurs_v1;
//		BiFunction<Individu, Individu, Individu> croisement = gest::croisement_v1;
//		Function<Individu, Individu> mutation = gest::mutation_v1;
//		SoloFunction<ArrayList<Individu>> getChampions = gest :: getChampions_v1;
//		
//		///////////////////////////////////////////////////////////////////////////////////////////////
//		
//		/////////////////////////////////////Methodes utilisees dans methodConstitution////////////////
//		
//		SoloFunction<Integer> selection2 = gest::selection_v1;
//		SoloFunction<Individu[]> getReproducteurs2 = gest::getReproducteurs_v1;
//		BiFunction<Individu, Individu, Individu> croisement2 = gest::croisement_v1;
//		Function<Individu, Individu> mutation2 = gest::mutation_v1;
//		SoloFunction<ArrayList<Individu>> getChampions2 = gest::getChampions_v1;
//		
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		
//		BiFunction<Individu, Integer, Individu> upgrade = gest::upgrade_v1; //Methode utilisee pour l'upgrade
//		
//		
//		
//		
//		Individu res = gest.launcher(nbUpgrade,nbIteration1,nbIteration2,effectifConstitution,nbCreeInit,mainMethod,methodConstitution,selection,getReproducteurs,croisement,mutation,getChampions,selection2,getReproducteurs2,croisement2,mutation2,getChampions2,upgrade );
//		save(res.clonage(), "test");
//		printImage(res.clonage(), myStage);	
//	
	
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	String path = "/home/administrateur/Documents/dev/projetIA/projetmonalisa/image/monaLisa-100.jpg";
//	exemple1(path, myStage, 500);
	exemple5(path, myStage, 15); //attention tres long (plusieurs dizaines de minutes) mais meilleur resultat
		
	}
	
	/**
	 * Exemple simple de l'execution de l'algortihme genetique, a chaque generation on selection un echantillon a partir des meilleurs
	 * resultat, on selectionne les 3 meilleurs de l'echantillon pour aller directement a la generation et on croise les elements de l'echantillon
	 * entre sachant que plus un element est performant plus il a de chance de se reproduire, enfin on mute les enfants en changeant leur
	 * couleur, leur opacite et en faisant se deplacer leur points, et on boucle
	 * @param path Chemin du fichier image source
	 * @param myStage Stage utilise
	 * @param nbIteration Nombre d'iteration de l'algorithme
	 */
	public void exemple1(String path, Stage myStage, int nbIteration) {
		int maxX = 100;
		int maxY = 149;
		int effectif = 50;
		int nbPoly = 50;
		int nbPoints = 5;
		double proba = 0.2;
		double probaColor = 0.2;
		double probaOpa = 0.3;
		double probaTranslation = 0.2;
		double montantTranslation = 10;
		double montantPoint = 10;
		double montantColor = 30;
		double montantOpa = 0.15;
		int expoErreur = 2;
		int pourcentageRandom = 100;
		int pourcentageReal = 20;
		int nbChampions = 3;
		
		Gestionnaire gest = new Gestionnaire(maxX, maxY, probaTranslation, montantTranslation, pourcentageRandom, pourcentageReal, nbChampions, effectif, nbPoly, nbPoints,
				path, proba, probaColor, probaOpa, montantPoint, montantColor, montantOpa, expoErreur);
		
		Individu res = gest.launcher(0,nbIteration,0,0,0,gest::handleSimulation_v1,gest::handleSimulation_v1,gest::selection_v1,gest::getReproducteurs_v1,gest::croisement_v1,gest::mutation_v1,gest::getChampions_v1,gest::selection_v1,gest::getReproducteurs_v1,gest::croisement_v1,gest::mutation_v1,gest::getChampions_v1, gest::upgrade_v1 );
		save(res.clonage(), "exemple1");
		printImage(res.clonage(), myStage);
	}
	

	
	/**
	 * Ici on cree initialement une population contenant des individus ne possedant que 5 polygones aleatoires, on leur applique l'algorithme
	 * de l'exemple 1 mais en ne faisant muter que le dernier polygone de la liste, on fait tourner l'algorithme jusqu'a ce que l'individu optimal de la population n'ai pas change pendant 
	 * un certain nombre d'iteration, on ajoute alors un nouveau polygone, on s'assure que celui ci ne fait pas diminuer la performance et on 
	 * recommence jusqu'a ce qu'il y ai 50 polygones
	 * @param path Chemin du fichier image source
	 * @param myStage Stage utilise
	 * @param nbIteration Nombre de fois que la performance doit restee inchanger avant de rajouter un polygone
	 */
	public void exemple5(String path, Stage myStage, int nbIteration) {
		int maxX = 100;
		int maxY = 149;
		int effectif = 50;
		int nbPoly =  5;
		int nbPoints = 6;
		double proba = 0.3;
		double probaColor = 0.3;
		double probaOpa = 0.3;
		double probaTranslation = 0.3;
		double montantTranslation = 10;
		double montantPoint = 10;
		double montantColor = 5;
		double montantOpa = 0.1;
		int expoErreur = 4;
		int pourcentageRandom = 100;
		int pourcentageReal = 30;
		int nbChampions = 1;
		ArrayList<Individu> al = new ArrayList<Individu>();
		for (int i=0; i<effectif; i++) {
			al.add(new Individu(nbPoly, nbPoints));
		}
		Population pop = new Population(al);
		Gestionnaire gest = new Gestionnaire(maxX, maxY, probaTranslation, montantTranslation, pourcentageRandom, pourcentageReal, nbChampions, pop, effectif, nbPoly, nbPoints,
				path, proba, probaColor, probaOpa, montantPoint, montantColor, montantOpa, expoErreur );
		
		
		Individu res = gest.launcher(0,nbIteration,0,0,0,gest::handleSimulation_v2,gest::handleSimulation_v1,gest::selection_v1,gest::getReproducteurs_v1,gest::croisement_v1,gest::mutation_v4,gest::getChampions_v1,gest::selection_v1,gest::getReproducteurs_v1,gest::croisement_v1,gest::mutation_v1,gest::getChampions_v1, gest::upgrade_v1 );
		save(res.clonage(), "exemple5");
		printImage(res.clonage(), myStage);
	}
	
	
	
	/**
	 * Permet d'afficher l'image associee a un individu
	 * @param i Individu a afficher
	 * @param myStage Stage utilise
	 */
	public static void printImage (Individu i, Stage myStage) {
		Group image = new Group();
		for(ConvexPolygon p : i.adn) {
			image.getChildren().add(p);
		}
		Scene scene = new Scene(image, ConvexPolygon.max_X, ConvexPolygon.max_Y);
		myStage.setScene(scene);
		myStage.show();
	}
	
	
	/**
	 * Permet de sauvegarder l'image associee a un individu
	 * @param indi Individu a sauveguarder
	 * @param name Nom a donne au fichier cree
	 */
	protected static void save (Individu indi, String name) {
		int maxX = ConvexPolygon.max_X;//BEWARE !!! a revoir
		int maxY = ConvexPolygon.max_Y;
		// formation de l'image par superposition des polygones
		Group image = new Group();
		for (ConvexPolygon p : indi.adn)
			image.getChildren().add(p);
		
		// Calcul de la couleur de chaque pixel.Pour cela, on passe par une instance de 
		// WritableImage, qui poss�de une m�thode pour obtenir un PixelReader.
		WritableImage wimg = new WritableImage(maxX,maxY);
		image.snapshot(null,wimg); 
		PixelReader pr = wimg.getPixelReader();
		// On utilise le PixelReader pour lire chaque couleur
		// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
		// de la couleur du pixel cible et celui du pixel de l'image g�n�r�e	
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null); 
		try {
			ImageIO.write(renderedImage, "png", new File(name+ ".png"));
			System.out.println("wrote image in " + name+".png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
