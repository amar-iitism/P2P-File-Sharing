package peerDetails;

import java.net.Socket;

public class peerHasCompleteFile {
	private Socket socket;
	private boolean peerHasCompleteFile;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public boolean isHasDownLoadedCompleteFile() {
		return peerHasCompleteFile;
	}

	public void setHasDownLoadedCompleteFile(boolean peerHasCompleteFile) {
		this.peerHasCompleteFile = peerHasCompleteFile;
	}
}