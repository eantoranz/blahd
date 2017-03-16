package blahd.client;

/*
 * Copyright 2017 Edmundo Carmona Antoranz
 * Released under the terms of GPLv3
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Terminal Client
 *
 */
public class Terminal implements AbstractClient {
	
	/**
	 * Create an instance of Terminal
	 * @param name
	 * @param host
	 * @param port
	 */
	private Terminal(String host, int port, String name) throws IOException {
		ClientConnection connection = new ClientConnection(this, host, port, name);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String message = reader.readLine();
			if (message == null) {
				break;
			}
			connection.sendMessage(message);
		}
		System.exit(0);
	}

	public static void main(String[] args) throws IOException {
		new Terminal(args[0], Integer.parseInt(args[1]), args[2]);
	}

	@Override
	public void receiveMessage(String name, Date when, String message) {
		System.out.println(name + ": " + message);
	}
	
	public void disconnect() {
		System.out.println("disconnecting");
		System.exit(0);
	}

}
