package dev.mwhitney.remote;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import dev.mwhitney.enums.RemoteCMD;
import dev.mwhitney.listeners.MessageListener;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class RemoteClient {
	
	//	WebSocket Components
	/** The <tt>WebSocketFactory</tt> used to create <tt>WebSocket</tt> instances. */
	private WebSocketFactory webSocketFactory;
	/** The <tt>WebSocket</tt> used to communicate with the server. */
	private WebSocket webSocket;
	
	//	Listeners
	/** The <tt>MessageListener</tt> responsible for handling incoming messages. */
	private MessageListener messageListener;
	
	//	Variables
	/** A <tt>Timer</tt> used for keeping the connection to the server alive. */
	private Timer keepAliveTimer = new Timer(20000, (actionEvent) -> {
		//	If the WebSocket is open, send a keep-alive message.
		if(webSocket.isOpen()) {
			webSocket.sendText("#connection=keep-alive");
		}
	});
	/** A <tt>Timer</tt> used for reconnecting to the server if the connection is lost. */
	private Timer reconnectTimer = new Timer(5000, (actionEvent) -> {
		//	Attempt to reconnect.
		if(!webSocket.isOpen() && webSocket.getState() != WebSocketState.CONNECTING) {
			setupClient();
			connectToServer();
		}
	});
	
	
	/**
	 * <ul>
	 * <p>	<b><i>RemoteClient</i></b>
	 * <p>	<code>public RemoteClient()</code>
	 * <p>	Creates a new <tt>RemoteClient</tt>.
	 * </ul>
	 */
	public RemoteClient() {		
		//	WebSocket & Server Connection Setup
		webSocketFactory = new WebSocketFactory();
		webSocketFactory.setVerifyHostname(false);
		setupClient();
		connectToServer();
		
		//	General Setup
		keepAliveTimer.start();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupClient</i></b>
	 * <p>	<code>private void setupClient()</code>
	 * <p>	Sets up the local client that communicates with the server.
	 * <p>	This method creates a new <tt>WebSocket</tt> instance and configures it appropriately.
	 * 		It does not, however, connect to the server with that newly created instance.
	 * 		To connect to the server, use the dedicated <code>connectToServer()</code> method.
	 * </ul>
	 */
	private void setupClient() {
		try {
			webSocket = webSocketFactory.createSocket("wss://one-server.minimunch57.club");
		} catch (IOException ioe) {
			System.out.println("<!> Error creating WebSocket instance. (setCli)");
			ioe.printStackTrace();
		}
		/* TODO: Ensure system has this environment variable set or it will fail to connect. */
		webSocket.addHeader("token", System.getenv("PROJECT_ONE_SYSTEM"));
		webSocket.addListener(new WebSocketAdapter() {
			//	Connected to the server.
			@Override
			public void onConnected(WebSocket webSocket, Map<String, List<String>> headers) throws Exception {
				System.out.println("<#> Connected to the server.");
				
				//	Don't continue any existing reconnection attempts.
				if(reconnectTimer.isRunning()) {
					reconnectTimer.stop();
				}
				
				//	Send first keep-alive message.
				webSocket.sendText("#connection=keep-alive");
			}
			//	Disconnected from the server.
			@Override
			public void onDisconnected(WebSocket webSocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
				System.out.println("<#> Disconnected from the server.");
				
				//	Set sleep time before attempting to reconnect.
				reconnectTimer.start();
			}
			//	Received a message from the server.
			@Override
			public void onTextMessage(WebSocket webSocket, String message) throws Exception {
				//	Fire the appropriate listener method based on the received message's type.
				message = message.trim();
				if(message.startsWith("#broadcast")) {
					messageListener.broadcastReceived(message.replaceFirst("#broadcast=", "").trim());
				}
				else if(message.startsWith("#command")) {
					//	Message and Variable Preparation
					message = message.replaceFirst("#command=", "").trim();
					RemoteCMD command = null;
					String[] args = null;
					
					//	Command Handling
					if(message.equals("!security:lock")) {
						command = RemoteCMD.LOCK;
					}
					else if(message.startsWith("!security:unlock")) {
						command = RemoteCMD.UNLOCK;
						args = message.replaceFirst("!security:unlock", "").trim().split(" ");
					}
					else if(message.equals("!security:system:lock")) {
						command = RemoteCMD.SYSTEM_LOCK;
					}
					else if(message.equals("!security:system:unlock")) {
						command = RemoteCMD.SYSTEM_UNLOCK;
					}
					else if(message.equals("!security:manualunlocks:enable")) {
						command = RemoteCMD.MANUALUNLOCKS_ENABLE;
					}
					else if(message.equals("!security:manualunlocks:disable")) {
						command = RemoteCMD.MANUALUNLOCKS_DISABLE;
					}
					else {
						command = RemoteCMD.UNRECOGNIZED;
						if(message.length() > 40) message = message.substring(0, 39).concat("...");
						System.out.println("<!> Unrecognized Command Received from Server: " + message);
					}
					
					//	Forward Command and Arguments
					messageListener.commandReceived(command, args);
				}
				else if(message.startsWith("#connection")) {
					messageListener.connectionMessageReceived(message.replaceFirst("#connection=", "").trim());
				}
				else if(message.startsWith("#response")) {
					messageListener.responseMessageReceived(message.replaceFirst("#response=", "").trim());
				}
				else {
					messageListener.messageReceived(message);
				}
			}
		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>connectToServer</i></b>
	 * <p>	<code>public void connectToServer()</code>
	 * <p>	Connects to the communications server using the current <tt>WebSocket</tt> instance.
	 * </ul>
	 */
	public void connectToServer() {
		try {
			webSocket.connect();
		} catch (WebSocketException wse) {
			System.out.println("<!> Could not connect to server. (Is it down?)");
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>disconnectFromServer</i></b>
	 * <p>	<code>public void disconnectFromServer()</code>
	 * <p>	Disconnects from the communications server.
	 * <p>	<b>Note:</b> This method is only intended to be used when closing the application.
	 * 		Otherwise, it is ideal for the system to always be connected to the server in order to receive messages and commands.
	 * </ul>
	 */
	public void disconnectFromServer() {
		webSocket.disconnect();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>isConnected</i></b>
	 * <p>	<code>public boolean isConnected()</code>
	 * <p>	Returns a <code>boolean</code> for whether or not this <tt>RemoteClient</tt> is connected to the server.
	 * @return <code>true</code> if connected to the server; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean isConnected() {
		return webSocket.isOpen();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>sendMessage</i></b>
	 * <p>	<code>public boolean sendMessage(String message)</code>
	 * <p>	Sends the passed <tt>String</tt> message to the server.
	 * @param message - the message to be sent to the server in the form of a <tt>String</tt>.
	 * @return <code>true</code> if the message was sent successfully; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean sendMessage(String message) {
		if(webSocket.isOpen()) {
			webSocket.sendText(message);
			return true;
		}
		return false;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setMessageListener</i></b>
	 * <p>	<code>public void setMessageListener(MessageListener ml)</code>
	 * <p>	Sets the one and only <tt>MessageListener</tt> to be used by this <tt>RemoteClient</tt>.
	 * @param ml - the <tt>MessageListener</tt> to be used by this <tt>RemoteClient</tt>.
	 * </ul>
	 */
	public void setMessageListener(MessageListener ml) {
		messageListener = ml;
	}
}
