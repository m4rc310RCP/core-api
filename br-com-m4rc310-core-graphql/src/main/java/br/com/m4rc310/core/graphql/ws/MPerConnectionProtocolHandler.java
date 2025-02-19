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

@Slf4j
public class MPerConnectionProtocolHandler extends PerConnectionApolloHandler{
	
	@Autowired
	private MTextWebSocketHandler handler;
	
	private ConcurrentHashMap<WebSocketSession, MHandlerProxy> handlers;

	private int sendTimeLimit;

	private int sendBufferSizeLimit;

	public MPerConnectionProtocolHandler(GraphQL graphQL, GraphQLWebSocketExecutor executor,
			TaskScheduler taskScheduler, int keepAliveInterval, int sendTimeLimit, int sendBufferSizeLimit) {
		super(graphQL, executor, taskScheduler, keepAliveInterval, sendTimeLimit, sendBufferSizeLimit);
		this.handlers = new ConcurrentHashMap<>();
		this.sendTimeLimit = sendTimeLimit;
		this.sendBufferSizeLimit = sendBufferSizeLimit;
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		MHandlerProxy proxy = new MHandlerProxy(handler, decorateSession(session));
		this.handlers.put(session, proxy);
		proxy.afterConnectionEstablished();
	}
	
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		try {
			getHandler(session).handleMessage(message);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		try {
			getHandler(session).handleTransportError(exception);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

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

	protected WebSocketSession decorateSession(WebSocketSession session) {
		return new ConcurrentWebSocketSessionDecorator(session, sendTimeLimit, sendBufferSizeLimit);
	}

	
	private MHandlerProxy getHandler(WebSocketSession session) {
		MHandlerProxy handler = this.handlers.get(session);
		if (handler == null) {
			log.error("WebSocketHandler not found for {}", session);
		}
		return handler;
	}

	
}
