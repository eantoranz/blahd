package blahd.client;

import java.util.Date;

/*
 * Copyright 2017 Edmundo Carmona Antoranz
 * Released under the terms of GPLv3
 */

/**
 * Interface that all clients have to implement to be able to use ClientConnection
 *
 */
public interface AbstractClient {
	
	/**
	 * Receive message from another user
	 */
	public void receiveMessage(String name, Date when, String message);
	
}
