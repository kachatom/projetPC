package prodcons.v2;

import java.util.Properties;

public class ProdConsBuffer implements IProdConsBuffer {

	int nMes;
	int nMesTotal;
	int bufSz;
	Message buffer[];
	int in, out;
	int nbProdAlive;

	ProdConsBuffer(int bufsize, int nbProdAlive) {
		nMes = 0;
		nMesTotal = 0;
		bufSz = bufsize;
		buffer = new Message[bufSz];
		in = 0;
		out = 0;
		this.nbProdAlive = nbProdAlive;
	}

	@Override
	public synchronized void put(Message m) throws InterruptedException {
		while (nMes == bufSz) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new InterruptedException("Buffer plein");
			}
		}
		buffer[in] = m;
		in = (in + 1) % bufSz;
		nMes++;
		nMesTotal++;
		notifyAll();
	}

	@Override
	public synchronized Message get() throws InterruptedException {
		while (nMes == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new InterruptedException("Buffer vide");
				}
		}
		Message m = buffer[out];
		out = (out + 1) % bufSz;
		nMes--;
		notifyAll();
		return m;
	}

	@Override
	public int nmsg() {
		return nMes;
	}

	@Override
	public int totmsg() {
		return nMesTotal;
	}

	public void producerDead() {
		nbProdAlive--;
	}
	
	public boolean ready() {
//		return ((nbProdAlive > 0) && (nMes != 0)) || (!isUsed);
		return (nbProdAlive > 0) || ((nbProdAlive == 0) && (nMes != 0));
	}

}
