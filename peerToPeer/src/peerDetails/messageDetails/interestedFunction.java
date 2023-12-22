package peerDetails.messageDetails;
import java.nio.ByteBuffer;

public class interestedFunction {
	public byte[] interestedFunction = new byte[5];
	private byte[] messageLength = new byte[4];
	private byte type = 2;
	
	public interestedFunction() {
		messageLength = ByteBuffer.allocate(4).putInt(0).array();
		int i = 0;
		for (i = 0; i < messageLength.length; i++) {
			interestedFunction[i] = messageLength[i];
		}
		interestedFunction[i] = type;
	}
}
