package blahd.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import blahd.server.plugin.Plugin;

/*
 * Copyright 2017 Edmundo Carmona Antoranz
 * Released under the terms of GPLv3
 */

/**
 * Main Server class. Here is where all the magic begins
 *
 */

public class Daemon {
	
	private Client[] clients = new Client[5];
	private ServerSocket socket;
	private Plugin plugin; // a plugin, if it was set up
	
	/**
	 * Create a daemon instance.
	 * @param port port where daemon will try to listen on
	 */
	private Daemon(int port, Plugin plugin) throws IOException {
		this.plugin = plugin;
		if (plugin != null) {
			plugin.setDaemon(this);
		}
		System.out.println("Starting Daemon on port " + port);
		socket = new ServerSocket(port);
		while (true) {
			try {
				Socket clientSocket = socket.accept();
				createClient(clientSocket);
			} catch (IOException e) {
				System.err.println("Error waiting for a client to connect");
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Create a client instance and register it in the list of clients 
	 * @param socket
	 */
	synchronized private void createClient(Socket socket) throws IOException {
		for (int i = 0; i < clients.length; i++) {
			// is there an available spot?
			if (clients[i] == null) {
				clients[i] = new Client(this, socket);
				return;
			}
		}
		// if we reached this point, there's no available spot for the client
		// let's send a message to the client saying that there's no available room and to retry later on 
		new OutputStreamWriter(socket.getOutputStream()).write("There's no available spot for you at the time. Sorry\nTry again later\n");
		socket.getOutputStream().flush();
		socket.close();
	}
	
	/**
	 * Remove a client from the list because the user disconnected
	 * @param client
	 */
	synchronized public void removeClient(Client client) {
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] == client) {
				clients[i] = null;
			}
		}
		// let's close everything, just in case
		client.close();
	}
	
	/**
	 * Process a message from a client
	 * @param client
	 * @param message
	 */
	synchronized public void processMessage(Client client, String message) {
		Date now = new Date();
		// first, let's process it through the plugin (if it's set)
		if (plugin != null) {
			String broadcastMessage = plugin.messageReceived(client, now, message);
			if (broadcastMessage != null) {
				// a message has to be sent to all clients on behalf of the bot
				for (Client aClient: clients) {
					if (aClient != null) {
						try {
							aClient.sendMessage("Blahd Server", now, broadcastMessage);
						} catch (Exception e) {
							System.err.println("Error sending message to client " + aClient.getClientName());
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		// sending message to all clients
		for (Client aClient: clients) {
			if (aClient != null && aClient != client && aClient.getClientName() != null) {
				try {
					aClient.sendMessage(client.getClientName(), now, message);
				} catch (Exception e) {
					System.err.println("Error sending message to client " + aClient.getClientName());
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("BlahD Server");
		System.out.println("Copyright 2017 Edmundo Carmona Antoranz");
		System.out.println("Released under the terms of GPLv3");
		
		Plugin plugin = null;
		
		String pluginClassName = System.getenv("blahd_plugin");
		if (pluginClassName != null) {
			System.out.println("Creating plugin " + pluginClassName);
			Class<?> pluginClass = Class.forName("blahd.server.plugin." + pluginClassName);
			plugin = (Plugin) pluginClass.newInstance();
		}
		
		int port = Integer.parseInt(args[0]);
		new Daemon(port, plugin);
	}

}
