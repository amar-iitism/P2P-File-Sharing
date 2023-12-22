package peerDetails;

public class handshakeMechanism {
	
	public byte[] handshakeMechanism = new byte[32];
	private String header = "P2PFILESHARINGPROJ";
	private String zero_bits = "\"0000000000";
	private int peerID;
	
	public handshakeMechanism (int peerID) {
		this.peerID = peerID;
		String array = header + zero_bits + Integer.toString(this.peerID);
		handshakeMechanism = array.getBytes();
	}
	
	
}