package br.com.m4rc310.core.graphql.ws;

import static io.leangen.graphql.spqr.spring.web.apollo.ApolloMessage.GQL_CONNECTION_INIT;
import static io.leangen.graphql.spqr.spring.web.apollo.ApolloMessage.GQL_CONNECTION_TERMINATE;
import static io.leangen.graphql.spqr.spring.web.apollo.ApolloMessage.GQL_START;
import static io.leangen.graphql.spqr.spring.web.apollo.ApolloMessage.GQL_STOP;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.m4rc310.core.graphql.configurations.security.IMGraphQLJwtService;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MEnumToken;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUserPrincipal;
import br.com.m4rc310.core.graphql.configurations.security.impls.MGraphQLJwtService;
import br.com.m4rc310.core.graphql.messages.MMessage;
import br.com.m4rc310.core.graphql.messages.MMessage.MInitMessage;
import br.com.m4rc310.core.graphql.messages.MMessage.MStartMessage;
import br.com.m4rc310.core.graphql.messages.MMessages;
import br.com.m4rc310.core.graphql.properties.IConsts;
import graphql.ExecutionResult;
import graphql.GraphQL;
import io.leangen.graphql.spqr.spring.web.dto.ExecutorParams;
import io.leangen.graphql.spqr.spring.web.dto.GraphQLRequest;
import io.leangen.graphql.spqr.spring.web.dto.TransportType;
import io.leangen.graphql.spqr.spring.web.mvc.websocket.GraphQLWebSocketExecutor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

// TODO: Auto-generated Javadoc
/**
 * The Class MTextWebSocketHandler.
 */
@Slf4j
@Configuration
@EqualsAndHashCode(callSuper = false)
@ConditionalOnProperty(name = IConsts.ENABLE_GRAPHQL_WS, havingValue = "false", matchIfMissing = false)
public class MTextWebSocketHandler extends TextWebSocketHandler {
	
	/** The session. */
	private WebSocketSession session;
	
	/** The task scheduler. */
	private TaskScheduler taskScheduler;
	
	/** The keep alive interval. */
	private int keepAliveInterval;
	
	/** The jwt service. */
	private MGraphQLJwtService jwtService;
	
	/** The context. */
	private ApplicationContext context;
	
	/** The subscriptions. */
	private final Map<String, Disposable> subscriptions = new ConcurrentHashMap<>();
	
	/** The keep alive. */
	private final AtomicReference<ScheduledFuture<?>> keepAlive = new AtomicReference<>();

	/** The graph QL. */
	@Autowired
	private GraphQL graphQL;

	/** The executor. */
	@Autowired
	private GraphQLWebSocketExecutor executor;
	
	/** The jwt. */
	@Autowired
	private IMGraphQLJwtService jwt;
	
	/**
	 * <p>Constructor for MTextWebSocketHandler.</p>
	 */
	public MTextWebSocketHandler(){}

	/**
	 * After connection established.
	 *
	 * @param session the session
	 * @throws Exception the exception
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
	}

	/**
	 * Handle text message.
	 *
	 * @param session the session
	 * @param message the message
	 * @throws Exception the exception
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		MMessage mmessage = MMessages.from(message);
		try {
			mmessage = MMessages.from(message);
		} catch (Exception e) {
			session.sendMessage(MMessages.connectionError());
			return;
		}

		switch (mmessage.getType()) {
		case GQL_CONNECTION_INIT:
			try {
				MUser user = getUserFromPayload(message.getPayload());
				if (Objects.isNull(user)) {
					throw new Exception("User not found.");
				}

				MUserPrincipal principal = MUserPrincipal.create(user);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
						null, principal.getAuthorities());
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				context.setAuthentication(authentication);
				SecurityContextHolder.setContext(context);
			} catch (Exception e) {
				log.error(e.getMessage());
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				SecurityContextHolder.setContext(context);
				fatalError(session, e);
			}
			break;
		case GQL_START:
			try {
				GraphQLRequest request = ((MStartMessage) mmessage).getPayload();
				ExecutorParams<WebSocketSession> params = new ExecutorParams<>(request, session,
						TransportType.WEBSOCKET);
				ExecutionResult result = executor.execute(graphQL, params);

				Object data = result.getData();

				if (data instanceof Publisher) {
					handleSubscription(mmessage.getId(), result, session);
				} else {
					handleQueryOrMutation(mmessage.getId(), result, session);
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				SecurityContextHolder.setContext(context);
				fatalError(session, e);
			}
			break;
		case GQL_STOP:
			Disposable toStop = subscriptions.get(mmessage.getId());
			if (toStop != null) {
				toStop.dispose();
				subscriptions.remove(mmessage.getId(), toStop);
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				SecurityContextHolder.setContext(context);
			}
			break;
		case GQL_CONNECTION_TERMINATE:
			session.close();
			cancelAll();
			break;
		default:
			session.sendMessage(MMessages.connectionError("Unexpected message type."));
			session.close();
		}
	}

	/**
	 * Gets the user from payload.
	 *
	 * @param payload the payload
	 * @return the user from payload
	 * @throws Exception the exception
	 */
	private MUser getUserFromPayload(String payload) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		MInitMessage message = mapper.readValue(payload, MInitMessage.class);
		Map<String, Object> mpay = message.getPayload();
		String token = (String) mpay.get("Authorization");
		token = token.trim();

		int i = token.indexOf(" ");
		String stype = token.substring(0, i).toLowerCase();
		String stoken = token.substring(i).trim();

		MEnumToken type = MEnumToken.fromDescription(stype);
		return jwt.getMUser(type, stoken);
	}

	/**
	 * Handle query or mutation.
	 *
	 * @param id the id
	 * @param result the result
	 * @param session the session
	 */
	private void handleQueryOrMutation(String id, ExecutionResult result, WebSocketSession session) {
		try {
			session.sendMessage(MMessages.data(id, result));
			session.sendMessage(MMessages.complete(id));
		} catch (IOException e) {
			fatalError(session, e);
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
		fatalError(session, exception);
	}

	/**
	 * Fatal error.
	 *
	 * @param session the session
	 * @param exception the exception
	 */
	private void fatalError(WebSocketSession session, Throwable exception) {
		try {
			session.close(
					exception instanceof IOException ? CloseStatus.SESSION_NOT_RELIABLE : CloseStatus.SERVER_ERROR);
		} catch (Exception suppressed) {
			exception.addSuppressed(suppressed);
		}
		cancelAll();
	}

	/**
	 * Handle subscription.
	 *
	 * @param id the id
	 * @param executionResult the execution result
	 * @param session the session
	 */
	private void handleSubscription(String id, ExecutionResult executionResult, WebSocketSession session) {
		Publisher<ExecutionResult> events = executionResult.getData();

		Disposable subscription = Flux.from(events).subscribe(result -> onNext(result, id, session),
				error -> onError(error, id, session), () -> onComplete(id, session));
		synchronized (subscriptions) {
			subscriptions.put(id, subscription);
		}
	}

	/**
	 * On next.
	 *
	 * @param result the result
	 * @param id the id
	 * @param session the session
	 */
	private void onNext(ExecutionResult result, String id, WebSocketSession session) {
		try {
			if (result.getErrors().isEmpty()) {
				session.sendMessage(MMessages.data(id, result));
			} else {
				session.sendMessage(MMessages.error(id, result.getErrors()));
			}
		} catch (IOException e) {
			fatalError(session, e);
		}
	}

	/**
	 * On error.
	 *
	 * @param error   the error
	 * @param id      the id
	 * @param session the session
	 */
	private void onError(Throwable error, String id, WebSocketSession session) {
		try {
			session.sendMessage(MMessages.error(id, error));
			session.sendMessage(MMessages.complete(id));
		} catch (IOException e) {
			fatalError(session, e);
		}
	}

	/**
	 * On complete.
	 *
	 * @param id      the id
	 * @param session the session
	 */
	private void onComplete(String id, WebSocketSession session) {
		try {
			session.sendMessage(MMessages.complete(id));
		} catch (IOException e) {
			fatalError(session, e);
		}
	}

	/**
	 * Cancel all.
	 */
	void cancelAll() {
		synchronized (subscriptions) {
			subscriptions.values().forEach(Disposable::dispose);
			subscriptions.clear();
		}
	}

}
