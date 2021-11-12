package prodcons.v1;

public class ProdConsBuffer implements IProdConsBuffer{

	
	
	public synchronized void produce(Message m){
		
	}
	
	
	public synchronized Message consume() {
		
	}

	@Override
	public void put(Message m) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Message get() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int nmsg() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int totmsg() {
		// TODO Auto-generated method stub
		return 0;
	}
}
