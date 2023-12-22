package peerDetails.messageDetails;

import java.nio.ByteBuffer;

public class requestFunction {
	
	public byte[] request = new byte[9];
	private byte[] requestMessageLength = new byte[4];
	private byte requestType = 6;
	private byte[] requestPayload = new byte[4];
	
	public requestFunction(int index) {
		requestMessageLength = ByteBuffer.allocate(4).putInt(4).array();
		requestPayload = ByteBuffer.allocate(4).putInt(index).array();
		
		int i = 0;
		for (i = 0; i < requestMessageLength.length; i++) {
			request[i] = requestMessageLength[i];
		}
		
		request[i] = requestType;
		
		for (int j = 0; j < requestPayload.length; j++) {
			i++;
			request[i] = requestPayload[j];
		}
		
	}

}
