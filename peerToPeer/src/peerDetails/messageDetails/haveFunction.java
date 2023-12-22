package peerDetails.messageDetails;
import java.nio.ByteBuffer;

public class haveFunction {
	
	public byte[] haveFunction = new byte[9];
	private byte[] messageLength = new byte[4];
	private byte type = 4;
	private byte[] payload = new byte[4];
	
	public haveFunction(int ind) {
		messageLength = ByteBuffer.allocate(4).putInt(4).array();
		payload = ByteBuffer.allocate(4).putInt(ind).array();
		
		int i = 0;
		for (i = 0; i < messageLength.length; i++) {
			haveFunction[i] = messageLength[i];
		}
		haveFunction[i] = type;
		for (int j = 0; j < payload.length; j++) {
			i++;
			haveFunction[i] = payload[j];
		}
		
	}
}