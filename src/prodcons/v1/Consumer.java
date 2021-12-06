package prodcons.v1;

import java.util.Random;

public class Consumer extends Thread {
	private ProdConsBuffer buffer;
	private boolean actif = true;
	private int id;
	private int consTime;

	Consumer(ProdConsBuffer b, int id, int consTime) {
		this.buffer = b;
		this.id = id;
		this.consTime = consTime;
	}

	public void run() {
		System.out.println("Consumer " + id + " lancé !");
		while (actif) {
			try {
				Random generator = new Random();
				sleep(consTime* 1000 + generator.nextInt(1000) - 500);
				Message m = buffer.get();
				System.out.println(id + " consomme le message " + m.id);	
			} catch (InterruptedException e) {
				System.out.println("Échec, " + e);
			}
		}
		System.out.println("Consumer " + id + " terminé");
	}
}
