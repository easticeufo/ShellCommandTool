package com.madongfang.command;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ShellComponent
public class MqttCommands {

	@ShellMethod("Initialize mqtt connection with serverURI, username and password")
	public String mqttInit(String serverURI, String username, String password) {
		try {
    		MemoryPersistence persistence = new MemoryPersistence();
    		String clientId = "init" + "_" + Long.toString(System.currentTimeMillis());
    		MqttClient mqttClient = new MqttClient(serverURI, clientId, persistence);
			
			MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
			if (username != null)
			{
				mqttConnectOptions.setUserName(username);
			}			
			if (password != null)
			{
				mqttConnectOptions.setPassword(password.toCharArray());
			}
			
			mqttClient.connect(mqttConnectOptions);
			
			this.serverURI = serverURI;
			this.username = username;
			this.password = password;
			
			mqttClient.disconnect();
			mqttClient.close();
			
			return "mqtt connect success";
			
		} catch (MqttException e) {
			logger.info("catch Exception:", e);
			return "mqtt connect failed";
		}
		
	}
	
	@ShellMethod("Create a number of mqtt connections")
	public String mqttCreateConnection(int number) {
		if (serverURI == null)
		{
			return "Please initialize mqtt connection first: use mqtt-init command";
		}
		
		MemoryPersistence persistence = new MemoryPersistence();
		MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
		if (username != null)
		{
			mqttConnectOptions.setUserName(username);
		}			
		if (password != null)
		{
			mqttConnectOptions.setPassword(password.toCharArray());
		}
		
		int i;
		for (i = 0; i < number; i++)
		{
			String clientId = "con" + i + "_" + Long.toString(System.currentTimeMillis());
			try {
				logger.debug("create connection i={}", i);
				MqttClient mqttClient = new MqttClient(serverURI, clientId, persistence);
				mqttClient.connect(mqttConnectOptions);
				mqttClients.add(mqttClient);
			} catch (MqttException e) {
				logger.info("mqtt connection create failed: i={}", i);
				logger.info("catch Exception:", e);
				break;
			}
			
		}
		
		return i + " mqtt connections have been created";
	}
	
	@ShellMethod("Disconnect all mqtt connections")
	public String mqttDisconnectAll() {
		for (MqttClient mqttClient : mqttClients) {
			try {
				mqttClient.disconnect();
				mqttClient.close();
			} catch (MqttException e) {
				logger.info("catch Exception:", e);
			}
		}
		mqttClients.clear();
		
		return "All mqtt connections have been disconnected";
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String serverURI = null;
	
	private String username = null;
	
	private String password = null;
	
	private List<MqttClient> mqttClients = new LinkedList<>();
}
