package br.com.m4rc310.core.graphql.configurations.security;

import java.lang.reflect.Field;
import java.util.List;

import org.reactivestreams.Publisher;

import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import reactor.core.publisher.Flux;

public interface IMFluxService {

	/**
	 * Publish.
	 *
	 * @param <T>  the generic type
	 * @param type the type
	 * @param key  the key
	 * @return the publisher
	 */
	<T> Publisher<T> publish(Class<T> type, Object key);

	/**
	 * <p>
	 * publish.
	 * </p>
	 *
	 * @param type         a {@link java.lang.Class} object
	 * @param key          a {@link java.lang.Object} object
	 * @param defaultValue a T object
	 * @param <T>          a T class
	 * @return a {@link org.reactivestreams.Publisher} object
	 */
	<T> Publisher<T> publish(Class<T> type, Object key, T defaultValue);

	/**
	 * <p>
	 * getSizeRegistries.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object
	 * @return a {@link java.lang.Integer} object
	 */
	Integer getSizeRegistries(Class<?> type);

	/**
	 * <p>
	 * publishList.
	 * </p>
	 *
	 * @param type         a {@link java.lang.Class} object
	 * @param key          a {@link java.lang.Object} object
	 * @param defaultValue a {@link java.util.List} object
	 * @param <T>          a T class
	 * @return a {@link reactor.core.publisher.Flux} object
	 */
	<T> Flux<List<T>> publishList(Class<T> type, Object key, List<T> defaultValue);

	/**
	 * In publish.
	 *
	 * @param type the type
	 * @param key  the key
	 * @return true, if successful
	 */
	boolean inPublish(Class<?> type, Object key);

	/**
	 * Call publish.
	 *
	 * @param <T>   the generic type
	 * @param key   the key
	 * @param value the value
	 * @throws java.lang.Exception the exception
	 */
	<T> void callPublish(Object key, T value) throws Exception;

	/**
	 * Call publish from type.
	 *
	 * @param <T>   the generic type
	 * @param value the value
	 * @throws java.lang.Exception the exception
	 */
	<T> void callPublish(Class<T> type, T value) throws Exception;

	List<String> getKeys(Class<?> type);

	/**
	 * Removes the publish.
	 *
	 * @param <T>  the generic type
	 * @param type the type
	 * @param key  the key
	 */
	<T> void removePublish(Class<T> type, String key);

	/**
	 * Call publish.
	 *
	 * @param <T>   the generic type
	 * @param type  the type
	 * @param key   the key
	 * @param value the value
	 * @throws java.lang.Exception the exception
	 */
	<T> void callPublish(Class<T> type, Object key, T value) throws Exception;

	/**
	 * <p>
	 * callListPublish.
	 * </p>
	 *
	 * @param type a {@link java.lang.Class} object
	 * @param key  a {@link java.lang.Object} object
	 * @param list a {@link java.util.List} object
	 * @param <T>  a T class
	 */
	<T> void callListPublish(Class<T> type, Object key, List<T> list);

	/**
	 * Clone ato B.
	 *
	 * @param a the a
	 * @param b the b
	 */
	void cloneAtoB(Object a, Object b);

	/**
	 * Gets the field from type.
	 *
	 * @param type      the type
	 * @param typeField the type field
	 * @return the field from type
	 */
	Field getFieldFromType(Class<?> type, Class<?> typeField);

	void setUser(MUser user);

	MUser getUser();

	void setIPClient(String ipClient);

	String getIPClient();

}