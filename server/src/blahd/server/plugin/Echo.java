package blahd.server.plugin;
/*
 * Copyright 2017 Edmundo Carmona Antoranz
 * Released under the terms of GPLv3
 */

import java.util.Date;

import blahd.server.Client;
import blahd.server.Daemon;

/**
 * Echo plugin.
 * Will replicate the message the client sent
 *
 */
public class Echo implements Plugin {
	
	private Daemon daemon;
	
	@Override
	public String messageReceived(Client client, Date time, String message) {
		try {
			client.sendMessage("Echo", time, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setDaemon(Daemon daemon) {
		this.daemon = daemon;
	}

}
