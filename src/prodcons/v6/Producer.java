package prodcons.v6;

import java.util.Random;

public class Producer extends Thread {
	private ProdConsBuffer buffer;
	private int id;
	private int prodTime;
	private int minProd;
	private int maxProd;
	private int n = 3; //nombre de message à produire dans le cas d'un multi-message

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
		int nbProd = generator.nextInt(maxProd - minProd) + minProd;
		for (int i = 0; i < nbProd; i++) {
			try {
				int pileface = generator.nextInt(10);
				if ((pileface == 0) && (buffer.sCons.availablePermits() <= buffer.bufSz - n)) { //si il reste assez de place dans le buffer pour n messages
					Message m = new Message(id * 1000 + i, n);
					sleep(prodTime * 1000 + generator.nextInt(1000) - 500); // n fois plus long
					buffer.put(m, n);
					System.out.println("Producer " + id + " a créé " + n + " exemplaires du message " + m.id);
				} else {  //cas de base
					Message m = new Message(id * 1000 + i, 1);
					sleep(prodTime * 1000 + generator.nextInt(1000) - 500);
					buffer.put(m);
					System.out.println("Producer " + id + " a crée le message " + m.id);
				}
			} catch (InterruptedException e) {
				System.out.println("Échec, " + e);
			}
		}
		buffer.producerDead();
		System.out.println("Producer " + id + " terminé");
	}
}
