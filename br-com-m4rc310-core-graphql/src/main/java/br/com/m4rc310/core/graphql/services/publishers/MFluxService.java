package br.com.m4rc310.core.graphql.services.publishers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Configuration;

import br.com.m4rc310.core.graphql.configurations.security.IMFluxService;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

// TODO: Auto-generated Javadoc
/**
 * The Class MFluxService.
 */
@Configuration
public class MFluxService implements IMFluxService {
	
	/** The registry. */
	protected final MMultiRegitry<String, Object> registry = new MMultiRegitry<>();

	/** The user. */
	private MUser user;

	/** The ip client. */
	private String ipClient;

	
	/**
	 * <p>Constructor for MFluxService.</p>
	 */
	public MFluxService() {
	}
	
	/**
	 * Publish.
	 *
	 * @param <T>  the generic type
	 * @param type the type
	 * @param key  the key
	 * @return the publisher
	 */
	@Override
	public <T> Publisher<T> publish(Class<T> type, Object key) {
		return publish(type, key, null);
	}

	/**
	 * <p>
	 * publish.
	 * </p>
	 *
	 * @param <T>          a T class
	 * @param type         a {@link java.lang.Class} object
	 * @param key          a {@link java.lang.Object} object
	 * @param defaultValue a T object
	 * @return a {@link org.reactivestreams.Publisher} object
	 */
	@Override
	public <T> Publisher<T> publish(Class<T> type, Object key, T defaultValue) {
		String skey = makeId(type, key);

		return Flux.create(fs -> {
			registry.add(skey, fs.onDispose(() -> registry.remove(skey)));
			if (Objects.nonNull(defaultValue)) {
				fs.next(defaultValue);
			}
		}, FluxSink.OverflowStrategy.BUFFER);
	}

	/**
	 * <p>
	 * getSizeRegistries.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object
	 * @return a {@link java.lang.Integer} object
	 */
	@Override
	public Integer getSizeRegistries(Class<?> type) {
		return registry.getSizeRegistries(type);
	}

	/**
	 * <p>
	 * publishList.
	 * </p>
	 *
	 * @param <T>          a T class
	 * @param type         a {@link java.lang.Class} object
	 * @param key          a {@link java.lang.Object} object
	 * @param defaultValue a {@link java.util.List} object
	 * @return a {@link reactor.core.publisher.Flux} object
	 */
	@Override
	public <T> Flux<List<T>> publishList(Class<T> type, Object key, List<T> defaultValue) {
		String skey = makeId(type, key);
		return Flux.create(fs -> {
			registry.add(skey, fs.onDispose(() -> registry.remove(skey)));
			fs.next(defaultValue);

		}, FluxSink.OverflowStrategy.BUFFER);
	}

	/**
	 * In publish.
	 *
	 * @param type the type
	 * @param key  the key
	 * @return true, if successful
	 */
	@Override
	public boolean inPublish(Class<?> type, Object key) {
		String skey = makeId(type, key);
		return registry.contains(skey);
	}

	/**
	 * Call publish.
	 *
	 * @param <T>   the generic type
	 * @param key   the key
	 * @param value the value
	 * @throws Exception the exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> void callPublish(Object key, T value) throws Exception {
		if (value == null) {
			throw new Exception("Value is null");
		}

		Class<?> type = value.getClass();

		if (!inPublish(type, key)) {
			throw new Exception("No published listener!");
		}

		String skey = makeId(type, key);

		List<FluxSink<?>> sinks = registry.get(skey);
		if (sinks != null) {
			sinks.forEach(sub -> ((FluxSink<T>) sub).next(value));
		}

		// registry.get(skey).forEach(sub -> sub.next(value));
	}

	/**
	 * Call publish from type.
	 *
	 * @param <T>   the generic type
	 * @param type the type
	 * @param value the value
	 * @throws Exception the exception
	 */
	@Override
	public <T> void callPublish(Class<T> type, T value) throws Exception {
		List<String> keys = registry.getKeys(type);
		for (String key : keys) {
			// System.out.println(key);
			callPublish(type, key, value);
		}
//		registry.getKeys(type).forEach((key) -> {
//			try {
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});

	}

	/**
	 * Gets the keys.
	 *
	 * @param type the type
	 * @return the keys
	 */
	@Override
	public List<String> getKeys(Class<?> type) {
		return registry.getKeys(type);
	}

	/**
	 * Removes the publish.
	 *
	 * @param <T>  the generic type
	 * @param type the type
	 * @param key  the key
	 */
	@Override
	public <T> void removePublish(Class<T> type, String key) {
		String skey = makeId(type, key);
		registry.remove(skey);
	}

	/**
	 * Call publish.
	 *
	 * @param <T>   the generic type
	 * @param type  the type
	 * @param key   the key
	 * @param value the value
	 * @throws Exception the exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> void callPublish(Class<T> type, Object key, T value) throws Exception {
		// String skey = String.format("%s", key); // makeId(type, key);
		String skey = makeId(type, key);
		List<FluxSink<?>> sinks = registry.get(skey);
		if (sinks != null) {
			sinks.forEach(sub -> ((FluxSink<T>) sub).next(value));
		}
	}

	/**
	 * <p>
	 * callListPublish.
	 * </p>
	 *
	 * @param <T>  a T class
	 * @param type a {@link java.lang.Class} object
	 * @param key  a {@link java.lang.Object} object
	 * @param list a {@link java.util.List} object
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> void callListPublish(Class<T> type, Object key, List<T> list) {
		String skey = makeId(type, key);
		List<FluxSink<?>> sinks = registry.get(skey);
		if (sinks != null) {
			sinks.forEach(sub -> ((FluxSink<List<T>>) sub).next(list));
		}
	}

	/**
	 * Authenticated user.
	 *
	 * @param type the type
	 * @param key the key
	 * @return the m user
	 */
//	public MUser authenticatedUser() {
//		try {
//			return (MUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		} catch (Exception e) {
//			return null;
//		}
//	}

	/**
	 * Make id.
	 *
	 * @param type the type
	 * @param key  the key
	 * @return the string
	 */
	private String makeId(Class<?> type, Object key) {
		return String.format("%s-%s", type.getSimpleName(), key);
	}

	/**
	 * Clone ato B.
	 *
	 * @param a the a
	 * @param b the b
	 */
	@Override
	public void cloneAtoB(Object a, Object b) {

		List<Field> fas = listAllFields(a);
		List<Field> fbs = listAllFields(b);

		fas.forEach(fa -> {
			try {
				fa.setAccessible(true);
				fbs.forEach(fb -> {
					fb.setAccessible(true);
					if (fa.getName().equals(fb.getName())) {
						try {
							Object value = fa.get(a);
							if (Objects.nonNull(value)) {
								try {
									if (Objects.isNull(fb.get(b))) {
										fb.set(b, value);
									}
								} catch (Exception e) {
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Gets the field from type.
	 *
	 * @param type      the type
	 * @param typeField the type field
	 * @return the field from type
	 */
	@Override
	public Field getFieldFromType(Class<?> type, Class<?> typeField) {
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(type.getFields()));
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		for (Field field : fields) {
			field.setAccessible(true);
			if (field.getType().getName().equals(typeField.getName())) {
				return field;
			}
		}
		;

		return null;
	}

	/**
	 * List all fields.
	 *
	 * @param o the o
	 * @return the list
	 */
	private List<Field> listAllFields(Object o) {
		List<Field> fields = new ArrayList<>();
		Class<?> type = o.getClass();

		fields.addAll(Arrays.asList(type.getFields()));
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		return fields;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	@Override
	public void setUser(MUser user) {
		this.user = user;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	@Override
	public MUser getUser() {
		return user;
	}

	/**
	 * Sets the IP client.
	 *
	 * @param ipClient the new IP client
	 */
	@Override
	public void setIPClient(String ipClient) {
		this.ipClient = ipClient;
	}

	/**
	 * Gets the IP client.
	 *
	 * @return the IP client
	 */
	@Override
	public String getIPClient() {
		return ipClient;
	}

}
