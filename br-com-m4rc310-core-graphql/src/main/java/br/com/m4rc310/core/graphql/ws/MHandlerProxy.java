package br.com.m4rc310.core.graphql.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MHandlerProxy {
	private final MTextWebSocketHandler handler;
	private final WebSocketSession session;
	
	public void afterConnectionEstablished() throws Exception {
		handler.afterConnectionEstablished(session);
	}

	public void handleMessage(WebSocketMessage<?> message) throws Exception {
		handler.handleMessage(session, message);
	}

	public void handleTransportError(Throwable exception) throws Exception {
		handler.handleTransportError(session, exception);
	}

	public void afterConnectionClosed(CloseStatus closeStatus) throws Exception {
		handler.afterConnectionClosed(session, closeStatus);
	}

	public void cancelAll() {
		handler.cancelAll();
	}
}
