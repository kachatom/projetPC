package prodcons.v5;

import java.util.Random;

public class Consumer extends Thread {
	private ProdConsBuffer buffer;
	private int id;
	private int consTime;
	private int k = 5;

	Consumer(ProdConsBuffer b, int id, int consTime) {
		this.buffer = b;
		this.id = id;
		this.consTime = consTime;
	}

	public void run() {
		System.out.println("Consumer " + id + " lancé !");
		while (true) {
			try {
				Random generator = new Random();
				if (buffer.ready()) {
					int pileface = generator.nextInt(2);
					if ((pileface == 0) && (buffer.sCons.availablePermits() >= k)){
						Message[] m = buffer.get(k);
						System.out.print("Consumer " + id + " consomme les messages : ");
						for (int i = 0; i<k-1; i++) {
							System.out.print(m[i].id + ", ");
						}
						System.out.println(m[k-1].id + ".");
						sleep((consTime * 1000 + generator.nextInt(1000) - 500) * k); //5 fois plus de temps
					} else {
						Message m = buffer.get();
						System.out.println("Consumer " + id + " consomme le message " + m.id);
						sleep(consTime * 1000 + generator.nextInt(1000) - 500);
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
