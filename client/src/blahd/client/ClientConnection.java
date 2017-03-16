package blahd.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/*
 * Copyright 2017 Edmundo Carmona Antoranz
 * Released under the terms of GPLv3
 */

/**
 * Connection to server (independent of type of client used)
 *
 */
public class ClientConnection {

	private BufferedReader reader;
	private BufferedWriter writer;
	
	private Socket socket;

	public ClientConnection(String host, int port, String name)
			throws IOException {
		// let's try to establish a connection, we send a message and then we disconnect
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
		writer.write("Hi!");
		writer.newLine();
		writer.flush();
		socket.close();
	}

}
