package prodcons.v5;

import java.util.Random;

public class Consumer extends Thread {
	private ProdConsBuffer buffer; // buffer qui va contenir les messages produit et pas encore consommé
	private int id; // id unique qui définit ce consumer
	private int consTime; // durree de consommation d'un msg
	private int k = 5; // nombre de messages que le consumer va prendre dans le buffer

	Consumer(ProdConsBuffer b, int id, int consTime) { // creation d'un consumer
		this.buffer = b;
		this.id = id;
		this.consTime = consTime;
	}

	public void run() { // fonctionnement d'un thread consumer
		System.out.println("Consumer " + id + " lancé !");
		while (true) {
			try {
				Random generator = new Random();
				if (buffer.ready()) {  // test pour s'assurer qu'on a besoin d'un consumer
					int pileface = generator.nextInt(2); // un peu de random pour décider du nombre de messages que le consumer va retirer
					if ((pileface == 0) && (buffer.sCons.availablePermits() >= k)){ // on regarde si il existe bien au moins k messages dans le buffer
						Message[] m = buffer.get(k); // on retire une liste de msg de taille k
						System.out.print("Consumer " + id + " consomme les messages : ");
						for (int i = 0; i<k-1; i++) {
							System.out.print(m[i].id + ", ");
						}
						System.out.println(m[k-1].id + ".");
						sleep((consTime * 1000 + generator.nextInt(1000) - 500) * k); // on simule le temps de consomation de 5 messages
					} else { // cas ou on ne retire qu'un seul message
						Message m = buffer.get(); // on recupere un message
						System.out.println("Consumer " + id + " consomme le message " + m.id);
						sleep(consTime * 1000 + generator.nextInt(1000) - 500); // on simule le temps de consomation d'un message
					}
				} else {
					break;
				}
			} catch (InterruptedException e) {
				System.out.println("Échec, " + e);
			}
		}
		System.out.println("Consumer " + id + " terminé");
	}

}
