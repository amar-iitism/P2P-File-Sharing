package peerDetails.messageDetails;
import java.nio.ByteBuffer;

public class unchokeFunction {
	public byte[] unchokeFunction = new byte[5];
	private byte[] messageLength = new byte[4];
	private byte type = 1;
	
	public void unchokeFunction() {
		messageLength = ByteBuffer.allocate(4).putInt(0).array();
		int i = 0;
		for (i = 0; i < messageLength.length; i++) {
			unchokeFunction[i] = messageLength[i];
		}
		unchokeFunction[i] = type;
	}		
}