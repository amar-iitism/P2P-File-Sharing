package peerDetails.messageDetails;
import java.nio.ByteBuffer;

public class notInterestedFunction {
	public byte[] notInterestedFunction = new byte[5];
	private byte[] messageLength = new byte[4];
	private byte type = 3;
	
	public notInterestedFunction() {
		messageLength = ByteBuffer.allocate(4).putInt(0).array();
		int i = 0;
		for (i = 0; i < messageLength.length; i++) {
			notInterestedFunction[i] = messageLength[i];
		}
		notInterestedFunction[i] = type;
	}
}