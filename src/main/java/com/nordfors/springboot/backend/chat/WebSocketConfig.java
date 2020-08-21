package com.nordfors.springboot.backend.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Habilita el broker de websocket en spring
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// Es la mismo ruta que se va a usar desde angular
		registry.addEndpoint("/chat-websocket")
		.setAllowedOrigins("http://localhost:4200")
		// Permite que utilice el protocolo http para conectarse ademas del ws nativo de html5
		.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// "/chat/" prefijo para el nombre de evento cuando el servidor emite un mensaje hay que indicar el nombre de ese evento
		registry.enableSimpleBroker("/chat/");
		// /app prefijo para el destino donde se va a publicar
		registry.setApplicationDestinationPrefixes("/app");
	}
	
	

}
