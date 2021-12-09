package prodcons.v1;

import java.util.Random;

public class Consumer extends Thread {
	private ProdConsBuffer buffer; // buffer qui va contenir les messages produit et pas encore consommé
	private int id; // id unique qui définit ce consumer
	private int consTime; // durree de consommation d'un msg

	Consumer(ProdConsBuffer b, int id, int consTime) { // creation d'un consumer
		this.buffer = b;
		this.id = id;
		this.consTime = consTime;
	}

	public void run() { // lancement d'un thread consumer
		System.out.println("Consumer " + id + " lancé !");
		while (true) {
			try {
				Random generator = new Random();
				Message m = buffer.get(); // recupere un message
				System.out.println(id + " consomme le message " + m.id);
				sleep(consTime* 1000 + generator.nextInt(1000) - 500); // simule le temps de consommation d'un message
			} catch (InterruptedException e) {
				System.out.println("Échec, " + e);
			}
		}
		//System.out.println("Consumer " + id + " terminé"); // on n'y arrive pas car while(true)
	}
}
