package br.com.m4rc310.core.graphql.messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import br.com.m4rc310.core.graphql.exceptions.MExceptionWhileDataFetching;
import br.com.m4rc310.core.graphql.messages.annotations.MConstants;
import br.com.m4rc310.core.graphql.properties.IConsts;
import graphql.GraphQL;
import graphql.GraphQL.Builder;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.language.SourceLocation;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.metadata.messages.MessageBundle;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class MMessageBuilder.
 */
@Slf4j
@Configuration
public class MMessageBuilder implements IConsts{

	/** The messages. */
	private final Map<String, Map<String, String>> messages = new HashMap<>();
	
	/** The lv. */
	private int lv = 0;

	/** The is dev. */
	@Value(VALUE_IS_DEV)
	private boolean isDev;

	/**
	 * Instantiates a new m message builder.
	 */
	public MMessageBuilder() {
	}
	
	/**
	 * Message bundle.
	 *
	 * @param messageBuilder the message builder
	 * @param messageSource the message source
	 * @return the message bundle
	 */
	@Bean
	MessageBundle messageBundle(MMessageBuilder messageBuilder, MessageSource messageSource) {
		return key -> getString(messageBuilder, messageSource, key);
	}

	/**
	 * Gets the message source.
	 *
	 * @return the message source
	 */
	@Bean("messageSource")
	MessageSource getMessageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("messages/message");
		log.debug("Load message source {}", source);
		return source;
	}

	/**
	 * The listener interface for receiving MApplication events.
	 * The class that is interested in processing a MApplication
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addMApplicationListener</code> method. When
	 * the MApplication event occurs, that object's appropriate
	 * method is invoked.
	 *
	 */
	@Component
	public class MApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
		
		/**
		 * On application event.
		 *
		 * @param event the event
		 */
		@Override
		public void onApplicationEvent(ApplicationReadyEvent event) {
			if (isDev) {
				log.info("~> Process messages");
				fixUnknowMessages();
			}
		}
	}
	
	/**
	 * Gets the string.
	 *
	 * @param messageBuilder the message builder
	 * @param messageSource the message source
	 * @param pattern the pattern
	 * @param args the args
	 * @return the string
	 */
	public String getString(MMessageBuilder messageBuilder, MessageSource messageSource, String pattern,
			Object... args) {
		String REGEX = "[^a-zA-Z0-9_]+";
		String ret = pattern.replaceAll(REGEX, "_");
		try {
			String message = messageSource.getMessage(pattern, args, Locale.forLanguageTag("pt-BR"));
			try {
				if (!pattern.startsWith("desc.")) {
					pattern = String.format("desc.%s", pattern);
					messageSource.getMessage(pattern, args, Locale.forLanguageTag("pt-BR"));
				}
			} catch (Exception e) {
				messageBuilder.appendText(pattern, ret);
			}
			return message;
		} catch (Exception e) {
			messageBuilder.appendText(pattern, ret);
			return ret;
		}
	}
	
	/**
	 * Make error interceptor.
	 *
	 * @param schema the schema
	 * @return the graph QL
	 */
	@Bean
	GraphQL makeErrorInterceptor(GraphQLSchema schema) {
		Builder builder = GraphQL.newGraphQL(schema);
		
		   SubscriptionExecutionStrategy ses = new SubscriptionExecutionStrategy(new SimpleDataFetcherExceptionHandler() {
		        @Override
		        public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
		                DataFetcherExceptionHandlerParameters handlerParameters) {
		            return CompletableFuture.completedFuture(handleExceptionImpl(handlerParameters));
		        }

		        private DataFetcherExceptionHandlerResult handleExceptionImpl(
		                DataFetcherExceptionHandlerParameters handlerParameters) {
		            Throwable exception = unwrap(handlerParameters.getException());
		            SourceLocation sourceLocation = handlerParameters.getSourceLocation();
		            ResultPath path = handlerParameters.getPath();

		            MExceptionWhileDataFetching error = new MExceptionWhileDataFetching(path, exception, sourceLocation);

		            return DataFetcherExceptionHandlerResult.newResult().error(error).build();
		        }
		    });

		AsyncExecutionStrategy aes = new AsyncExecutionStrategy(new SimpleDataFetcherExceptionHandler() {
			@Override
			public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
					DataFetcherExceptionHandlerParameters handlerParameters) {
				return CompletableFuture.completedFuture(handleExceptionImpl(handlerParameters));
			}
			

			private DataFetcherExceptionHandlerResult handleExceptionImpl(
					DataFetcherExceptionHandlerParameters handlerParameters) {
				Throwable exception = unwrap(handlerParameters.getException());
				SourceLocation sourceLocation = handlerParameters.getSourceLocation();
				ResultPath path = handlerParameters.getPath();

				MExceptionWhileDataFetching error = new MExceptionWhileDataFetching(path, exception, sourceLocation);

				return DataFetcherExceptionHandlerResult.newResult().error(error).build();
			}
			
			

		});
		

		builder.queryExecutionStrategy(aes);
		builder.mutationExecutionStrategy(aes);
		builder.subscriptionExecutionStrategy(ses);
		return builder.build();
	}
	
	
	/**
	 * Physical naming strategy standard.
	 *
	 * @return the physical naming strategy standard impl
	 */
	@Bean
	PhysicalNamingStrategyStandardImpl physicalNamingStrategyStandard() {
		return new MPhysicalNamingImpl() {

			private static final long serialVersionUID = -4054141843987604307L;

			@Override
			public Identifier apply(Identifier name, JdbcEnvironment context) {

				String cn = name == null ? "" : name.getCanonicalName();

				if (cn.contains("${") && cn.contains("}")) {
					String message = cn;
					message = message.replace("${", "");
					message = message.replace("}", "");

					try {
						message = getMessageSource().getMessage(message, null, Locale.forLanguageTag("pt-BR"));
						return Identifier.toIdentifier(message, true);
					} catch (Exception e) {

						Pattern pattern = Pattern.compile("\\b\\w+\\.\\w+\\b");
						Matcher matcher = pattern.matcher(message);

						while (matcher.find()) {
							String palavra = matcher.group();

							log.warn("Message not found for {}", palavra);

							appendText(message, palavra);
						}

						throw new UnsupportedOperationException(e);
					}

				}
				return name;
			}
		};
	}

	/**
	 * Load implicit naming strategy.
	 *
	 * @return the implicit naming strategy
	 */
	@Bean
	ImplicitNamingStrategy loadImplicitNamingStrategy() {
		return new ImplicitNamingStrategyLegacyJpaImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Identifier toIdentifier(String stringForm, MetadataBuildingContext buildingContext) {
				if (Objects.nonNull(stringForm) && stringForm.startsWith("${") && stringForm.endsWith("}")) {
					stringForm = stringForm.replace("${", "");
					stringForm = stringForm.replace("}", "");

					try {
						stringForm = getMessageSource().getMessage(stringForm, null, Locale.forLanguageTag("pt-BR"));
						return super.toIdentifier(stringForm, buildingContext);
					} catch (Exception e) {

						Pattern pattern = Pattern.compile("\\b\\w+\\.\\w+\\b");
						Matcher matcher = pattern.matcher(stringForm);

						while (matcher.find()) {
							String word = matcher.group();
							log.warn("Message not found for {}", word);
							appendText(stringForm, word);
						}
						throw new UnsupportedOperationException(e);
					}
				}
				return super.toIdentifier(stringForm, buildingContext);
			}

			@Override
			protected String transformEntityName(EntityNaming entityNaming) {
				String entityName = super.transformEntityName(entityNaming);
				if (Objects.nonNull(entityName) && entityName.startsWith("${") && entityName.endsWith("}")) {
					entityName = entityName.replace("${", "");
					entityName = entityName.replace("}", "");

					try {
						return getMessageSource().getMessage(entityName, null, Locale.forLanguageTag("pt-BR"));
					} catch (Exception e) {

						Pattern pattern = Pattern.compile("\\b\\w+\\.\\w+\\b");
						Matcher matcher = pattern.matcher(entityName);

						while (matcher.find()) {
							String palavra = matcher.group();
							log.warn("Message not found for {}", palavra);
							appendText(entityName, palavra);
						}
						throw new UnsupportedOperationException(e);
					}
				}

				return entityName;
			}
		};
	}

	/**
	 * Gets the message.
	 *
	 * @param key the key
	 * @param args the args
	 * @return the message
	 */
	public String getMessage(String key, Object... args) {
		try {
			if (key.contains("${") && key.contains("}")) {
				key = key.replace("${", "");
				key = key.replace("}", "");

				return getMessageSource().getMessage(key, args, Locale.forLanguageTag("pt-BR"));
			}
		} catch (Exception e) {
			Pattern pattern = Pattern.compile("\\b\\w+\\.\\w+\\b");
			Matcher matcher = pattern.matcher(key);

			while (matcher.find()) {
				String palavra = matcher.group();

				log.warn("Message not found for {}", palavra);

				appendText(key, palavra);
			}
		}

		return key;
	}

	/**
	 * Append text.
	 *
	 * @param key the key
	 * @param text the text
	 */
	public void appendText(String key, String text) {

		try {
			String skey = key.substring(0, key.indexOf("."));

			if (!messages.containsKey(skey)) {
				messages.put(skey, new HashMap<>());
			}

			messages.get(skey).put(key, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find all interfaces.
	 *
	 * @param clasz the clasz
	 * @return the list
	 */
	private List<Class<?>> findAllInterfaces(Class<? extends Annotation> clasz) {
		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				false) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				return super.isCandidateComponent(beanDefinition) || beanDefinition.getMetadata().isAbstract();
			}
		};
		provider.addIncludeFilter(new AnnotationTypeFilter(clasz, true, true));
		final Set<BeanDefinition> classes = provider.findCandidateComponents("br/com/m4rc310");

		List<Class<?>> ret = new ArrayList<>();

		for (BeanDefinition bean : classes) {
			try {
				Class<?> type = Class.forName(bean.getBeanClassName());
				if (type.isInterface()) {
					ret.add(type);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (ret.isEmpty()) {
			log.info(
					"\n**** ATENTION ***\nCreate a interface annotated with br.com.m4rc310.gql.annotations.MConstants");
		}

		return ret;
	}

	
	/**
	 * Fix unknow messages.
	 */
	public void fixUnknowMessages() {
		findAllInterfaces(MConstants.class).forEach(clasz -> {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
					getClass().getClassLoader());
			try {
				for (Resource res : resolver.getResources("classpath:messages/**/*.properties")) {
					Properties properties = PropertiesLoaderUtils.loadProperties(res);

					// ================================================
					// Messages not in *.properties
					messages.forEach((key, value) -> {
						value.forEach((skey, svalue) -> {
							if (!skey.startsWith("desc.")) {
								// -------
								log.info("New message registered for key {}", skey);
								svalue = String.format("%s", svalue);
								properties.put(skey, svalue);
								// -------
								skey = String.format("desc.%s", skey);
								svalue = skey;
								log.info("New message registered for key {}", skey);
								properties.put(skey, svalue);
							}
						});
					});

					// ================================================
					// Agrupamento por chave (primeira palavra - ex: NUMBER -> number.cat)
					Map<String, Map<String, String>> maps = new TreeMap<>();
					Pattern compile = Pattern.compile("(\\w+)\\..*");

					properties.forEach((key, value) -> {
						String skey = (String) key;
						String svalue = (String) value;
						Matcher matcher = compile.matcher(skey);
						if (matcher.matches()) {
							String ref = skey.substring(0, skey.indexOf("."));
							if (!maps.containsKey(ref)) {
								maps.put(ref, new TreeMap<>());
							}
							maps.get(ref).put(skey, svalue.trim());
						}
					});

					// ================================================
					// Definir largura maxima de todos os registros
					int maxlength = 0;
					for (String key : maps.keySet()) {
						Map<String, String> map = maps.get(key);
						for (String ref : map.keySet()) {
							String ss = String.format("%s=%s\n", ref, map.get(ref));
							if (ss.length() > maxlength) {
								maxlength = ss.length();
							}
						}
					}
					// ================================================
					// Recriando o arquivo messages
					StringBuilder sb = new StringBuilder();
					for (String key : maps.keySet()) {
						// ----
						String aux = String.format("# ");
						sb.append(String.format("%s%s\n", aux, "=".repeat(maxlength - aux.length())));
						// ----
						aux = String.format("# %s's   ", key.toUpperCase());
						sb.append(String.format("%s%s\n", aux, "-".repeat(maxlength - aux.length())));
						// ----
						Map<String, String> map = maps.get(key);
						int ml = 0;
						for (String key2 : map.keySet()) {
							if (key2.length() > ml) {
								ml = key2.length();
							}
						}

						for (String ref : map.keySet()) {
							String sp = " ".repeat(ml - ref.length());
							sb.append(String.format("%s%s = %s\n", ref, sp, map.get(ref).trim()));
						}
					}

					String sfile = String.format("src/main/resources/messages/%s", res.getFilename());
					File file = new File(sfile);
					if (file.exists()) {
						file.delete();
					}

					file.createNewFile();
					try (BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"))) {
						bufferedWriter.write(sb.toString());
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}

					// ================================================
					StringBuilder sb2 = new StringBuilder();
					for (String key : maps.keySet()) {
						String skey = key.toUpperCase();
						Map<String, String> map = maps.get(key);
						for (Map.Entry<String, String> entry : map.entrySet()) {
							String k = entry.getKey();
							String variable = String.format("	public static final String %s$%s", skey,
									k.replace(key + ".", "").replace(".", "_"));
							lv = variable.length() > lv ? variable.length() : lv;
						}
					}

					sb2.setLength(0);
					// ----------
					String saux = String.format("package %s;\n\n\n", clasz.getPackage().getName());
					sb2.append(saux);
					// ----------
					saux = String.format("import br.com.m4rc310.core.graphql.messages.annotations.MConstants;\n\n");
					sb2.append(saux);
					// ----------
					saux = String.format("@MConstants\n");
					sb2.append(saux);
					// ----------
					saux = String.format("public interface %s {\n", clasz.getSimpleName());
					sb2.append(saux);
					// ----------
					for (String key : maps.keySet()) {
						String skey = key.toUpperCase();
						Map<String, String> map = maps.get(key);
						sb2.append("	//").append("-".repeat(50)).append("\n");
						sb2.append("	// ").append(String.format("********** %s **********\n", skey));
						sb2.append("	//").append("-".repeat(50)).append("\n");

						map.forEach((k, v) -> {
							String a1 = String.format("%s$%s", skey, k.replace(key + ".", "").replace(".", "_"));
							String a2 = String.format("DESC$%s_%s", skey.toLowerCase(),
									k.replace(key + ".", "").replace(".", "_"));
							// sb2.append("//").append("-".repeat(50)).append("\n");

							String action = "@GraphQLQuery";
							String com = "	// %s(name=%s, description=%s)";
							if (skey.equalsIgnoreCase("QUERY")) {
								action = "@GraphQLQuery";
							} else if (skey.equalsIgnoreCase("MUTATION")) {
								action = "@GraphQLMutation";
							} else if (skey.equalsIgnoreCase("SUBSCRIPTION")) {
								action = "@GraphQLSubscription";
							} else if (skey.equalsIgnoreCase("TYPE")) {
								action = "@GraphQLType";
								com = "	// %s(name=*INTERFACE*.%s, description=*INTERFACE*.%s)".replace("*INTERFACE*",
										clasz.getSimpleName());
							} else if (skey.equalsIgnoreCase("ARGUMENT") || skey.equalsIgnoreCase("FIELD")) {
								action = "@GraphQLArgument";
							}

							if (!skey.equals("DESC")) {
								sb2.append(String.format(com, action, a1, a2)).append("\n");
							}

							String variable = String.format("	public static final String %s$%s", skey,
									k.replace(key + ".", "").replace(".", "_"));

//							int rest = lv - variable.length();
							// int rest = variable.length();
							// String sp = rest > 0 ? " ".repeat(rest) : "";

							sb2.append(String.format("%s = \"${%s}\";", variable, k)).append("\n");
						});
						sb2.append("\n");
					}

					// ----------
					saux = String.format("\n}");
					sb2.append(saux);
					// ----------
					String spath = String.format("src/main/java/%s/%s.java", clasz.getPackageName().replace(".", "/"),
							clasz.getSimpleName());
					file = new File(spath);
					if (file.exists()) {
						file.delete();
					}
					file.createNewFile();

					try (BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"))) {
						bufferedWriter.write(sb2.toString());
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}

				}

			} catch (Exception e) {

			}
		});
	}
}
