package peerDetails;

import java.net.Socket;

public class peerBin {

	private int selfPeerID;
	private int peer_ID;
	private Socket socket;
	private byte[] bitfield;
	private boolean interested;

	public int getSelfPeerID() {
		return selfPeerID;
	}
	// setter peerId
	public void setSelfPeerID(int selfPeerID) {
		this.selfPeerID = selfPeerID;
	}
	// getter getPeerID
	public int getPeer_ID() {
		return peer_ID;
	}
	// setter setPeerID
	public void setPeer_ID(int peer_ID) {
		this.peer_ID = peer_ID;
	}
	// getter getSocket
	public Socket getSocket() {
		return socket;
	}
	// setter setSocket
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	// getter getBitfield
	public byte[] getBitfield() {
		return bitfield;
	}
	// getter setBitfield
	public void setBitfield(byte[] bitfield) {
		this.bitfield = bitfield;
	}
	// getter isInterested
	public boolean isInterested() {
		return interested;
	}
	// setter setInterested
	public void setInterested(boolean interested) {
		this.interested = interested;
	}
}
