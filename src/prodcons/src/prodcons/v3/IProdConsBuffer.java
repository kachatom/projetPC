package prodcons.v3;

public interface IProdConsBuffer {
	
	
	public void put (Message m) throws InterruptedException;
	
	
	public Message get() throws InterruptedException;
	
	
	public int nmsg();
	
	
	public int totmsg();
}
