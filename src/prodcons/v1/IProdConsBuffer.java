package prodcons.v1;

public interface IProdConsBuffer {
	
	public void put (Message m) throws InterruptedException;
	
	
	public Message get() throws InterruptedException;
	
	
	public int nmsg();
	
	
	public int totmsg();
}
