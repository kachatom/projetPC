package prodcons.v5;

import java.util.Properties;
import java.util.concurrent.Semaphore;

public class ProdConsBuffer implements IProdConsBuffer {

	int nMesTotal; // nombre total de message produit ??
	int bufSz; // taille du buffer
	Message buffer[]; // liste des messages dans le buffer
	int in, out; // indice d'entrée et de sorties des messages dans le buffer
	int nbProdAlive; // nombre de producer qui n'ont pas fini toutes leur production
	Semaphore sProd, sCons; // semaphore pour le consumer et consumer

	ProdConsBuffer(int bufsize, int nbProdAlive) {
		nMesTotal = 0;
		bufSz = bufsize;
		buffer = new Message[bufSz];
		in = 0;
		out = 0;
		this.nbProdAlive = nbProdAlive;
		sProd = new Semaphore(bufsize); // initialise le semaphore avec la valeur de la taille du buffer (nfree)
		sCons = new Semaphore(0); // initialise le semaphore avec 0 (nfree)
	}

	// insere un msg m dans le buffer si c'est possible
	@Override
	public void put(Message m) throws InterruptedException {

		sProd.acquire(); // on vérifie qu'il y a de la place dans le buffer, si oui nfree - 1 . sinon on attend
		synchronized (this) { // synchronised pour etre sur d'être le seul à insérer un msg à la fois
			buffer[in] = m; // le message est placé dans le buffer
			in = (in + 1) % bufSz; // actualisation de la place d'entrée
			nMesTotal++;
		}

		sCons.release(); // nfree + 1 . un nouveau message => on peut utiliser un sCons
	}

	// recupère le msg m (1er de la liste type FIFO avec in et out)
	@Override
	public Message get() throws InterruptedException {

		sCons.acquire(); // on vérifie qu'on a des message à lire dans le buffer, si oui nfree - 1 . sinon on attend
		Message m;
		synchronized (this) { // synchronised pour etre sur d'être le seul à récupérer un msg à la fois
			m = buffer[out]; // on selectionne le premier message
			out = (out + 1) % bufSz; // actualisation de la place de sortie
		}

		sProd.release(); // nfree + 1 . un message est consumé donc on a de nouveau de la place pour produire un nouveau message
		return m;
	}
	
	// on récupère k messages (dans l'order FIFO)
	@Override
	public Message[] get(int k) throws InterruptedException {
		sCons.acquire(k); // on vérifie qu'on a k messages à lire dans le buffer, si oui nfree - k . sinon on attend
		Message[] m = new Message[k]; // on crée un liste de messages de taille k
		for (int i=0; i<k; i++) {
			synchronized (this) { // synchronised pour etre sur d'être le seul à récupérer un msg à la fois
				m[i] = buffer[out]; // on selectionne le premier message
				out = (out + 1) % bufSz; // actualisation de la place de sortie
			}
		}
		sProd.release(k); // nfree + k . k messages sont consumés donc on a de nouveau de la place pour produire un nouveau message
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

	// décrémente le nombre de producer en activité
	public void producerDead() {
		nbProdAlive--;
	}

	// teste que des producteurs n'ont pas finis, ou que le buffer n'est pas vide
	public boolean ready() {
		return (nbProdAlive > 0) || ((nbProdAlive == 0) && (sCons.availablePermits() != 0));
	}
}
