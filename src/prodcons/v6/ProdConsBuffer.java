package prodcons.v6;

import java.util.Properties;
import java.util.concurrent.Semaphore;

public class ProdConsBuffer implements IProdConsBuffer {

	int nMesTotal;
	int bufSz;
	Message buffer[];
	int in, out;
	int nbProdAlive;
	Semaphore sProd, sCons;
	Semaphore sPmul, sCmul; //creation de semaphores servant à bloquer et débloquer les threads pour les multi-messages
	int multCompt[]; //tableau repertoriant le nombre de message qu'il reste à lire par multi-messages
	int multIn; //meme fonctionnement que pour le buffer
	int multOut;

	ProdConsBuffer(int bufsize, int nbProdAlive) {
		nMesTotal = 0;
		bufSz = bufsize;
		buffer = new Message[bufSz];
		in = 0;
		out = 0;
		this.nbProdAlive = nbProdAlive;
		sProd = new Semaphore(bufsize);
		sCons = new Semaphore(0);
		sPmul = new Semaphore(1);
		sCmul = new Semaphore(2);
		multCompt = new int[bufSz];
		multIn = 0;
		multOut = 0;

	}

	@Override
	public void put(Message m) throws InterruptedException {

		sProd.acquire();
		sPmul.acquire();
		synchronized (this) {
			buffer[in] = m;
			in = (in + 1) % bufSz;
			nMesTotal++;
		}

		sPmul.release();
		sCons.release();
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
		sProd.acquire(n);
		sPmul.acquire(); // bloquer ce producer
		multCompt[multIn] = n; //initialisation du compteur
		multIn = (multIn + 1) % bufSz;
		for (int i = 0; i < n; i++) {
			synchronized (this) {
				buffer[in] = m;
				in = (in + 1) % bufSz;
				nMesTotal++;
			}
		}
		sCons.release(n);
	}

	@Override
	public Message get() throws InterruptedException {
		sCons.acquire();
		Message m;
		synchronized (this) {
			m = buffer[out];
			out = (out + 1) % bufSz;
			if (m.nbExemp > 1) {   //si le message est dans un multi-message
				if (multCompt[multOut] == 1) {  //si c'est le dernier message à consommer du multi-message
					multCompt[multOut]--;
					sCmul.release(2); // reveiller tous les threads
					sPmul.release();
					multOut = (multOut + 1) % bufSz; //passer au multi-message suivant
				} else {
					multCompt[multOut]--;
					sCmul.acquire(); // bloquer ce consumer
				}
			}
		}
		sProd.release();
		return m;
	}

	@Override
	public Message[] get(int k) throws InterruptedException {
		sCons.acquire(k);
		Message[] m = new Message[k];
		for (int i = 0; i < k; i++) {
			synchronized (this) {
				m[i] = buffer[out];
				out = (out + 1) % bufSz;
			}
		}
		sProd.release(k);
		return m;
	}

	@Override
	public int nmsg() {
		return sCons.availablePermits();
	}

	@Override
	public int totmsg() {
		return nMesTotal;
	}

	public void producerDead() {
		nbProdAlive--;
	}

	public boolean ready() {
		return (nbProdAlive > 0) || ((nbProdAlive == 0) && (sCons.availablePermits() != 0));
	}

	// true si un consommateur peut consommer k messages consécutifs à exemplaires
	// uniques
	public boolean isValid(int k) {
		if (sCons.availablePermits() < k) {
			return false;
		} else {
			for (int i = 0; i < k; i++) {
				if (buffer[out + i % bufSz].nbExemp > 1)
					return false;
			}
		}
		return true;
	}

}
