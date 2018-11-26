package com.madongfang.util;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MqttUtil {
	
	public void init(String serverURI, String username, String password, String subscribeTopic, MqttCallback callback) {
		try {
    		MemoryPersistence persistence = new MemoryPersistence();
    		String clientId = subscribeTopic + "_" +Long.toString(System.currentTimeMillis());
			mqttClient = new MqttClient(serverURI, clientId, persistence);
			mqttClient.setCallback(callback);
			
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
			mqttClient.subscribe(subscribeTopic);
			
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			logger.error("catch Exception:", e);
		}
	}
	
	public void destroy() {
		try {
			mqttClient.disconnect();
			mqttClient.close();
			
			try {
				Thread.sleep(1000); // 休眠1秒，确保tomcat在解除部署时，等到mqttClient资源真正释放后才停止
			} catch (InterruptedException ie) {
				// TODO Auto-generated catch block
				logger.error("catch Exception:", ie);
			}
			
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			logger.error("catch Exception:", e);
		}
	}
	
	public void publish(String topic, String payload) {
		
		if (mqttClient == null)
		{
			logger.error("mqttClient is null");
			return;
		}
		
		MqttMessage message = new MqttMessage(payload.getBytes());
		
		try {
			logger.info("mqtt publish: topic={}, payload={}", topic, message);
			mqttClient.publish(topic, message);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			logger.error("catch Exception:", e);
		}
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private MqttClient mqttClient;
}
