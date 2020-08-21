package com.nordfors.springboot.backend.chat.controllers;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.nordfors.springboot.backend.chat.models.documents.Mensaje;
import com.nordfors.springboot.backend.chat.models.service.ChatService;

// En este caso no es restcontroller
@Controller
public class ChatController {
	
	private String[] colores = {"red", "green", "magenta", "purple", "orange"};
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private SimpMessagingTemplate webSocket;
	
	// El cliente va a usar app/mensaje para enviar un nuevo mensaje al broker (app de clase config)
	@MessageMapping("/mensaje")
	// Para enviar a los demas clientes suscritos a algun evento indicando el nombre del evento y el prefijo ("/chat/" del config)
	@SendTo("/chat/mensaje")
	// Recibe los mensajes u objeto completo
	public Mensaje recibeMensaje(Mensaje mensaje) {
		mensaje.setFecha(new Date().getTime());
		
		if(mensaje.getTipo().equals("NUEVO_USUARIO")) {
			// Toma un valor aleatorio entre 0 y 5
			mensaje.setColor(colores[new Random().nextInt(colores.length)]);
			mensaje.setTexto("Mensaje del sistema: ");
		} else {
			chatService.guardar(mensaje);
		}
		//mensaje.setTexto("Recibido por el broker: " + mensaje.getTexto());
		
		// Se emite a todos los clientes suscritps con el evento "/chat/mensaje"
		return mensaje;
	}
	
	
	@MessageMapping("/escribiendo")
	@SendTo("/chat/escribiendo")
	public String estaEscribiendo(String username) {
		return username.concat(" está escribiendo...");
	}
	
	
	// Para enviar el clienteId desde angular (para que solamente reciba los ultimos 10 mensajes el nuevo usuario) no se puede utilizar la anotacion @SendTo
	// En su lugar se utiliza SimpMessagingTemplate para personalizar el nombre del evento al cual se va a suscribir un cliente
	@SendTo("/chat/historial")
	@MessageMapping("/historial")
	public void historial(String clienteId) {
		webSocket.convertAndSend("/chat/historial/"+clienteId, chatService.obtenerUltimos10Mensajes());
		//return chatService.obtenerUltimos10Mensajes();
	}

}
