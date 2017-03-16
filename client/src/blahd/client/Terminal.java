package blahd.client;

/*
 * Copyright 2017 Edmundo Carmona Antoranz
 * Released under the terms of GPLv3
 */

import java.io.IOException;
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
		new ClientConnection(host, port, name);
	}

	public static void main(String[] args) throws IOException {
		new Terminal(args[0], Integer.parseInt(args[1]), args[2]);
	}

	@Override
	public void receiveMessage(String name, Date when, String message) {
		System.out.println((char)0x0d + name + ": " + message);
	}

}
