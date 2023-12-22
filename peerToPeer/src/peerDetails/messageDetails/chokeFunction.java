package peerDetails.messageDetails;

import java.nio.ByteBuffer;

public class chokeFunction {
	public byte[] chokeFunction = new byte[5];
	private byte[] messageLength = new byte[4];
	private byte type = 0;

	public chokeFunction() {
		messageLength = ByteBuffer.allocate(4).putInt(0).array();

		int i = 0;
		for (i = 0; i < messageLength.length; i++) {
			chokeFunction[i] = messageLength[i];
		}
		chokeFunction[i] = type;
	}
}