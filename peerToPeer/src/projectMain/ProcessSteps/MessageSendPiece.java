package projectMain.ProcessSteps;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import peerDetails.messageStructure;
import projectMain.mainPeerProcessing;

public class MessageSendPiece extends Thread {
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.err.println(e);
			}

			if(!mainPeerProcessing.messageBody.isEmpty()) {
				
				synchronized (mainPeerProcessing.messageBody) {
					messageStructure msg = mainPeerProcessing.messageBody.poll();
					Socket socket = msg.getSocket();
					byte[] messages = msg.getMessage();
					messageSendFunction(socket, messages);
				}
			}
		}
	}
	
public void messageSendFunction(Socket socket, byte[] messages) {
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			synchronized (socket) {
				out.writeObject(messages);
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
}