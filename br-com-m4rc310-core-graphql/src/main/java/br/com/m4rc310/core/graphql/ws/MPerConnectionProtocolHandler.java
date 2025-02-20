package br.com.m4rc310.core.graphql.ws;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import graphql.GraphQL;
import io.leangen.graphql.spqr.spring.web.apollo.PerConnectionApolloHandler;
import io.leangen.graphql.spqr.spring.web.mvc.websocket.GraphQLWebSocketExecutor;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class MPerConnectionProtocolHandler.
 */
@Slf4j
public class MPerConnectionProtocolHandler extends PerConnectionApolloHandler{
	
	/** The handler. */
	@Autowired
	private MTextWebSocketHandler handler;
	
	/** The handlers. */
	private ConcurrentHashMap<WebSocketSession, MHandlerProxy> handlers;

	/** The send time limit. */
	private int sendTimeLimit;

	/** The send buffer size limit. */
	private int sendBufferSizeLimit;

	/**
	 * Instantiates a new m per connection protocol handler.
	 *
	 * @param graphQL the graph QL
	 * @param executor the executor
	 * @param taskScheduler the task scheduler
	 * @param keepAliveInterval the keep alive interval
	 * @param sendTimeLimit the send time limit
	 * @param sendBufferSizeLimit the send buffer size limit
	 */
	public MPerConnectionProtocolHandler(GraphQL graphQL, GraphQLWebSocketExecutor executor,
			TaskScheduler taskScheduler, int keepAliveInterval, int sendTimeLimit, int sendBufferSizeLimit) {
		super(graphQL, executor, taskScheduler, keepAliveInterval, sendTimeLimit, sendBufferSizeLimit);
		this.handlers = new ConcurrentHashMap<>();
		this.sendTimeLimit = sendTimeLimit;
		this.sendBufferSizeLimit = sendBufferSizeLimit;
	}
	
	/**
	 * After connection established.
	 *
	 * @param session the session
	 * @throws Exception the exception
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		MHandlerProxy proxy = new MHandlerProxy(handler, decorateSession(session));
		this.handlers.put(session, proxy);
		proxy.afterConnectionEstablished();
	}
	
	/**
	 * Handle message.
	 *
	 * @param session the session
	 * @param message the message
	 * @throws Exception the exception
	 */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		try {
			getHandler(session).handleMessage(message);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Handle transport error.
	 *
	 * @param session the session
	 * @param exception the exception
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		try {
			getHandler(session).handleTransportError(exception);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * After connection closed.
	 *
	 * @param session the session
	 * @param closeStatus the close status
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		try {
			getHandler(session).afterConnectionClosed(closeStatus);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.handlers.remove(session);
		}
	}

	/**
	 * Supports partial messages.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	/**
	 * Cancel all.
	 */
	@PreDestroy
	public void cancelAll() {
		this.handlers.forEach((session, handler) -> {
			try {
				session.close(CloseStatus.GOING_AWAY);
			} catch (IOException ignored) {
			}
			handler.cancelAll();
		});
	}

	/**
	 * Decorate session.
	 *
	 * @param session the session
	 * @return the web socket session
	 */
	protected WebSocketSession decorateSession(WebSocketSession session) {
		return new ConcurrentWebSocketSessionDecorator(session, sendTimeLimit, sendBufferSizeLimit);
	}

	
	/**
	 * Gets the handler.
	 *
	 * @param session the session
	 * @return the handler
	 */
	private MHandlerProxy getHandler(WebSocketSession session) {
		MHandlerProxy handler = this.handlers.get(session);
		if (handler == null) {
			log.error("WebSocketHandler not found for {}", session);
		}
		return handler;
	}

	
}
