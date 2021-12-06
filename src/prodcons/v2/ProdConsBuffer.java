package prodcons.v2;

import java.util.Properties;

public class ProdConsBuffer implements IProdConsBuffer {

	int nMes;
	int nMesTotal;
	int bufSz;
	Message buffer[];
	int in, out;

	ProdConsBuffer(int bufsize) {
		nMes = 0;
		nMesTotal = 0;
		bufSz = bufsize;
		buffer = new Message[bufSz];
		in = 0;
		out = 0;
	}

	@Override
	public synchronized void put(Message m) throws InterruptedException {
		while (nMes == bufSz)
			try {
				wait();
			} catch (InterruptedException e) {
				throw new InterruptedException("Buffer plein");
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
				return null;
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

}
