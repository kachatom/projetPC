package prodcons.v5;

import java.util.Random;

public class Producer extends Thread{
	private ProdConsBuffer buffer; // buffer qui va contenir les messages produit et pas encore consommé
	private int id; // id unique qui définit ce producer
	private int prodTime; // temps de prod d'un message
	private int minProd; // minimum de msg produit
	private int maxProd; // max de msg produit

	//creation d'un produceur
	Producer(ProdConsBuffer b, int id, int prodTime, int minProd, int maxProd) {
		this.buffer = b;
		this.id = id;
		this.prodTime = prodTime;
		this.minProd = minProd;
		this.maxProd = maxProd;
	}

	public void run() { // lancement d'un thread producer
		System.out.println("Producer " + id + " lancé !");
		Random generator = new Random();
		int nbProd = generator.nextInt(maxProd-minProd)+minProd; // nb de messages à produire (semi random)
		for (int i = 0; i<nbProd; i++) {
			try { 
				Message m = new Message(id*1000 + i); // creer un id unique de message pour le différencier
				sleep(prodTime* 1000 + generator.nextInt(1000) - 500); //simulation du temps de production
				buffer.put(m); // place le message dans le buffer
				System.out.println("Producer " + id + " crée le message " + m.id);
			} catch (InterruptedException e) {
				System.out.println("Échec, " + e);
			}
		}
		buffer.producerDead();// après avoir fini ses taches il ne faut plus le compter comme actif
		System.out.println("Producer " + id + " terminé"); 
		// une fois qu'il a produit tous ses messages,
		// il signale qu'il a termine toutes ses taches
	}
}
