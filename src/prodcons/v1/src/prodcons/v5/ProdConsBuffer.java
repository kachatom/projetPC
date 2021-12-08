package prodcons.v5;

import java.util.Properties;
import java.util.concurrent.Semaphore;

public class ProdConsBuffer implements IProdConsBuffer {

	int nMesTotal;
	int bufSz;
	Message buffer[];
	int in, out;
	int nbProdAlive;
	Semaphore sProd, sCons;

	ProdConsBuffer(int bufsize, int nbProdAlive) {
		nMesTotal = 0;
		bufSz = bufsize;
		buffer = new Message[bufSz];
		in = 0;
		out = 0;
		this.nbProdAlive = nbProdAlive;
		sProd = new Semaphore(bufsize);
		sCons = new Semaphore(0);
	}

	@Override
	public void put(Message m) throws InterruptedException {

		sProd.acquire();
		synchronized (this) {
			buffer[in] = m;
			in = (in + 1) % bufSz;
			nMesTotal++;
		}

		sCons.release();
	}

	@Override
	public Message get() throws InterruptedException {

		sCons.acquire();
		Message m;
		synchronized (this) {
			m = buffer[out];
			out = (out + 1) % bufSz;
		}

		sProd.release();
		return m;
	}
	
	@Override
	public Message[] get(int k) throws InterruptedException {
		sCons.acquire(k);
		Message[] m = new Message[k];
		for (int i=0; i<k; i++) {
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
}
