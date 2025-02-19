package br.com.m4rc310.core.graphql.ws.configurations;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

import br.com.m4rc310.core.graphql.properties.IConsts;
import br.com.m4rc310.core.graphql.ws.MPerConnectionProtocolHandler;
import graphql.GraphQL;
import io.leangen.graphql.spqr.spring.autoconfigure.DataLoaderRegistryFactory;
import io.leangen.graphql.spqr.spring.autoconfigure.SpqrProperties;
import io.leangen.graphql.spqr.spring.autoconfigure.WebSocketAutoConfiguration;
import io.leangen.graphql.spqr.spring.web.apollo.PerConnectionApolloHandler;
import io.leangen.graphql.spqr.spring.web.mvc.websocket.GraphQLWebSocketExecutor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableWebSocket
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebSocketConfigurer.class)
@ConditionalOnProperty(name = IConsts.ENABLE_GRAPHQL_WS, havingValue = "false", matchIfMissing = false)
public class MGraphQLWsAutoConfiguration  extends WebSocketAutoConfiguration{

	private SpqrProperties config;
	private GraphQL graphQL;
	
	public MGraphQLWsAutoConfiguration(GraphQL graphQL, SpqrProperties config,
			Optional<DataLoaderRegistryFactory> dataLoaderRegistryFactory) {
		super(graphQL, config, dataLoaderRegistryFactory);
		this.graphQL = graphQL;
		this.config = config;
	}

	@PostConstruct
	void init() {
		log.info("~> Loading {}...", getClass().getSimpleName());
	}
	
	@Override
	public PerConnectionApolloHandler webSocketHandler(GraphQLWebSocketExecutor executor) {
		
		boolean keepAliveEnabled = config.getWs().getKeepAlive().isEnabled();
		int keepAliveInterval = config.getWs().getKeepAlive().getIntervalMillis();
		int sendTimeLimit = config.getWs().getSendTimeLimit();
		int sendBufferSizeLimit = config.getWs().getSendBufferSizeLimit();
		TaskScheduler taskScheduler = keepAliveEnabled ? defaultTaskScheduler() : null;
		
		return new MPerConnectionProtocolHandler(graphQL, executor, taskScheduler, keepAliveInterval, sendTimeLimit, sendBufferSizeLimit);
	}
	
	private TaskScheduler defaultTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolScheduler = new ThreadPoolTaskScheduler();
		threadPoolScheduler.setThreadNamePrefix("GraphQLWSKeepAlive-");
		threadPoolScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		threadPoolScheduler.setRemoveOnCancelPolicy(true);
		threadPoolScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
		threadPoolScheduler.initialize();
		return threadPoolScheduler;
	}

}
