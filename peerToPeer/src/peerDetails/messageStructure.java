package peerDetails;

import java.net.Socket;

public class messageStructure {
	private Socket socket;
	private byte[] message;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}
}