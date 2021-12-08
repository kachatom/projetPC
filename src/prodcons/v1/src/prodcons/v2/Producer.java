package prodcons.v2;

import java.util.Random;

public class Producer extends Thread{
	private ProdConsBuffer buffer;
	private boolean actif = true;
	private int id;
	private int prodTime;
	private int minProd;
	private int maxProd;

	Producer(ProdConsBuffer b, int id, int prodTime, int minProd, int maxProd) {
		this.buffer = b;
		this.id = id;
		this.prodTime = prodTime;
		this.minProd = minProd;
		this.maxProd = maxProd;
	}

	public void run() {
		System.out.println("Producer " + id + " lancé !");
		Random generator = new Random();
		int nbProd = generator.nextInt(maxProd-minProd)+minProd;
		for (int i = 0; i<nbProd; i++) {
			try {
				sleep(prodTime* 1000 + generator.nextInt(1000) - 500);
				Message m = new Message(id*1000 + i);
				buffer.put(m);
				System.out.println("Producer " + id + " a créé le message " + m.id);
				actif = false;
			} catch (InterruptedException e) {
				System.out.println("Échec, " + e);
			}
		}
		buffer.producerDead();
		System.out.println("Producer " + id + " terminé");
	}
}
