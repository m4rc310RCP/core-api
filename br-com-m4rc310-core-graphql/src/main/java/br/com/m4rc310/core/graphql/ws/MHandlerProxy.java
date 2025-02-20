package br.com.m4rc310.core.graphql.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

// TODO: Auto-generated Javadoc
/**
 * The Class MHandlerProxy.
 */
//@AllArgsConstructor
public class MHandlerProxy {
	
	/** The handler. */
	private final MTextWebSocketHandler handler;
	
	/** The session. */
	private final WebSocketSession session;
	
	
	
	
	/**
	 * @param handler
	 * @param session
	 */
	public MHandlerProxy(MTextWebSocketHandler handler, WebSocketSession session) {
		super();
		this.handler = handler;
		this.session = session;
	}

	/**
	 * After connection established.
	 *
	 * @throws Exception the exception
	 */
	public void afterConnectionEstablished() throws Exception {
		handler.afterConnectionEstablished(session);
	}

	/**
	 * Handle message.
	 *
	 * @param message the message
	 * @throws Exception the exception
	 */
	public void handleMessage(WebSocketMessage<?> message) throws Exception {
		handler.handleMessage(session, message);
	}

	/**
	 * Handle transport error.
	 *
	 * @param exception the exception
	 * @throws Exception the exception
	 */
	public void handleTransportError(Throwable exception) throws Exception {
		handler.handleTransportError(session, exception);
	}

	/**
	 * After connection closed.
	 *
	 * @param closeStatus the close status
	 * @throws Exception the exception
	 */
	public void afterConnectionClosed(CloseStatus closeStatus) throws Exception {
		handler.afterConnectionClosed(session, closeStatus);
	}

	/**
	 * Cancel all.
	 */
	public void cancelAll() {
		handler.cancelAll();
	}
}
