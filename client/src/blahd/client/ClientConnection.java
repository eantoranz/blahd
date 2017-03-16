package blahd.client;

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
 * Connection to server (independent of type of client used)
 *
 */
public class ClientConnection extends Thread {
	
	private AbstractClient client;

	private BufferedReader reader;
	private BufferedWriter writer;
	
	private Socket socket;

	public ClientConnection(AbstractClient client, String host, int port, String name)
			throws IOException {
		// let's try to establish a connection, we send a message and then we disconnect
		this.client = client;
		socket = new Socket(host, port);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		// server will say "Sup"
		String line = reader.readLine();
		if (!"Sup!".equals(line)) {
			throw new IOException("Daemon didn't send right startup message");
		}
		writer.write("Sup, Dude!");
		writer.newLine();
		writer.flush();
		line = reader.readLine();
		if (!"You said your name was?".equals(line)) {
			throw new IOException("Daemon didn't ask for our name");
		}
		writer.write(name);
		writer.newLine();
		writer.flush();
		line = reader.readLine();
		if (!"welcome".equals(line)) {
			throw new IOException("Didn't get a welcome message");
		}
		start();
	}
	
	public void run() {
		System.out.println("Connection established");
		while (true) {
			/*
			 * a message is comprised of:
			 * - name of person who sent it
			 * - when the message was sent (milliseconds since UNIX epoch
			 * - content of the message itself
			 * - empty line
			 */
			try {
				String name = reader.readLine();
				if (name == null) {
					break;
				}
				if (name.trim().length() == 0) {
					throw new IOException("Didn't get a name for the message");
				}
				String dateLine = reader.readLine();
				if (dateLine == null) {
					break;
				}
				Date date = new Date(Long.parseLong(dateLine));
				String message = reader.readLine();
				if (message == null) {
					break;
				}
				String separator = reader.readLine();
				if (separator == null) {
					break;
				}
				client.receiveMessage(name, date, message);
			}catch (IOException e) {
				System.err.println("Error reading message");
				e.printStackTrace();
			}
		}
		// notify of disconnection
		client.disconnect();
	}
	
	public void sendMessage(String message) throws IOException {
		writer.write(message);
		writer.newLine();
		writer.flush();
	}

}
