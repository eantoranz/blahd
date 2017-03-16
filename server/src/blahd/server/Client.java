package blahd.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

/*
 * Copyright 2017 Edmundo Carmona Antoranz
 * Released under the terms of GPLv3
 */

/**
 * A client instance
 * <p>
 * Client takes care of all communication with a client
 * (get messages, send messages)
 * <p>
 * Protocol specifies that:
 * <ul>
 * 	<li>1 Server will send a "Sup!" message
 * 	<li>2 Client will respond with a "Sup, Dude!" message
 * 	<li>3 Server will send a "You said your name was?" message
 * 	<li>4 Client will reply with his/her name
 * 	<li>5 Server will reply back with a "welcome" message.
 * <p>
 * Messages sent <b>from</b> the client will only consist of the line that the client wrote
 * <p>
 * Messages sent <b>to</b> the client will consist of 4 lines:
 * <ul>
 *	<li>name of the client who sent it
 *	<li>Date when it was sent (milliseconds since UNIX epoch)
 *	<li>Line of the message
 *	<li>An empty line
 * </ul>
 */
public class Client extends Thread {
	
	private Daemon daemon;
	private Socket socket;
	
	// reader/writer
	private BufferedWriter writer;
	private BufferedReader reader;

	private String name; // name of the client
	
	/**
	 * Try to establish communication with the client.
	 * <p>
	 * @param socket socket used to communicate with client
	 */
	public Client(Daemon daemon, Socket socket) throws IOException {
		this.daemon = daemon;
		this.socket = socket;
		
		// let's try to establish communication with the client
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		this.start();
	}
	
	public void run() {
		// first, need to establish the communication with the client
		try {
			writer.write("Sup!");
			writer.newLine();
			writer.flush();
			String line = reader.readLine();
			if (!"Sup, Dude!".equals(line)) {
				// not the right protocol from client
				throw new IOException("Didn't get the right response from client");
			}
			writer.write("You said your name was?");
			writer.newLine();
			writer.flush();
			line = reader.readLine();
			if (line.trim().length() == 0) {
				writer.write("Haha! Very funny!\nTry connecting later when someone from anonymous comes around!\n");
				writer.flush();
				throw new IOException("User didn't provide a name");
			}
			this.name = line;
			writer.write("welcome");
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			System.err.println("Exception trying to establish communication with client");
			e.printStackTrace();
			// if there's any kind of exception when establishing the communication, will ask Daemon to drop us
			daemon.removeClient(this);
		}
		
		// Let's start getting messages until client disconnects
		while (true) {
			try {
				String line = reader.readLine();
				if (line == null) {
					// connection closed
					break;
				}
				daemon.processMessage(this, line);
			} catch (Exception e) {
				// there was an error communicating with client. Let's disconnect
				System.out.println("Error communicating with client... letting it go");
			}
		}
		daemon.removeClient(this);
		return;
	}
	
	/**
	 * Send a message to client
	 * @param name Sender
	 * @param when When the message was sent
	 * @param message Content of the message
	 */
	public void sendMessage(String name, Date when, String message) throws IOException {
		writer.write(name);
		writer.newLine();
		writer.write(Long.toString(when.getTime()));
		writer.newLine();
		writer.write(message);
		writer.newLine();
		writer.newLine();
		writer.flush();
	}
	
	/**
	 * We are being called to close everything
	 */
	public void close() {
		try {
			writer.flush();
			writer.close();
		} catch (Exception e) {}
		try {
			reader.close();
		} catch (Exception e) {}
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Closed connection to " + name);
	}
	
	/**
	 * Name of the client
	 */
	public String getClientName() {
		return name;
	}

}
