package prodcons.v2;

import java.util.Properties;

public class ProdConsBuffer implements IProdConsBuffer {

	int nMes; // nombre des message dans le buffer
	int nMesTotal; // nombre total de message produit ??
	int bufSz; // taille du buffer
	Message buffer[]; // liste des messages dans le buffer
	int in, out; // indice d'entrée et de sorties des messages dans le buffer
	int nbProdAlive; // nombre de producer qui n'ont pas fini toutes leur production

	ProdConsBuffer(int bufsize, int nbProdAlive) {
		nMes = 0;
		nMesTotal = 0;
		bufSz = bufsize;
		buffer = new Message[bufSz];
		in = 0;
		out = 0;
		this.nbProdAlive = nbProdAlive;
	}

	// insere un msg m dans le buffer si c'est possible
	@Override
	public synchronized void put(Message m) throws InterruptedException {
		while (nMes == bufSz){ // tant que le buffer est plein
			try {
				wait();	// on attend
			} catch (InterruptedException e) {
				throw new InterruptedException("Buffer plein");
			}
		}
		buffer[in] = m; // le message est placé dans le buffer
		in = (in + 1) % bufSz; // actualisation de la place d'entrée
		nMes++; // il prend une place dans le buffer
		nMesTotal++;
		notifyAll();
	}

	// recupère le msg m (1er de la liste type FIFO avec in et out)
	@Override
	public synchronized Message get() throws InterruptedException {
		while (nMes == 0) { // vérifie que le buffer n'est pas vide
			try {
				wait(); // sinon attend
			} catch (InterruptedException e) {
				throw new InterruptedException("Buffer vide");
			}
		}
		Message m = buffer[out]; // on selectionne le premier message
		out = (out + 1) % bufSz; // actualisation de la place de sortie
		nMes--;  // il libere une place dans le buffer
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
	
	// teste que des producteurs n'ont pas finis, ou que le buffer n'est pas vide
	public boolean ready() { 
		return (nbProdAlive > 0) || ((nbProdAlive == 0) && (nMes != 0));
	}

}
