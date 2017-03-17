package blahd.server.plugin;

import java.util.Date;

import blahd.server.Client;
/*
 * Copyright 2017 Edmundo Carmona Antoranz
 * Released under the terms of GPLv3
 */
import blahd.server.Daemon;

/**
 * Interface for all plugins
 *
 */
public interface Plugin {
	
	/**
	 * Register daemon instance
	 * @param daemon
	 */
	public void setDaemon(Daemon daemon);
	
	/**
	 * A message was received from a given client
	 * @param client
	 * @param time
	 * @param message
	 * 
	 * @return if not null, output message will be sent to all clients
	 */
	public String messageReceived(Client client, Date time, String message);

}
