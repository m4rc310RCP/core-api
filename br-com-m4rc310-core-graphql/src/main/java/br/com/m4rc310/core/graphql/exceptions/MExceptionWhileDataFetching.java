package br.com.m4rc310.core.graphql.exceptions;

import static java.lang.String.format;
import static graphql.Assert.assertNotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.execution.ResultPath;
import graphql.language.SourceLocation;

// TODO: Auto-generated Javadoc
/**
 * The Class MExceptionWhileDataFetching.
 */
public class MExceptionWhileDataFetching implements GraphQLError {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3489316369045027073L;
	
	/** The message. */
	private final String message;
    
    /** The path. */
    private final List<Object> path;
    
    /** The exception. */
    private final Throwable exception;
    
    /** The locations. */
    private final List<SourceLocation> locations;
    
    /** The extensions. */
    private final Map<String, Object> extensions;

    /**
     * <p>Constructor for MExceptionWhileDataFetching.</p>
     *
     * @param path a {@link graphql.execution.ResultPath} object
     * @param exception a {@link java.lang.Throwable} object
     * @param sourceLocation a {@link graphql.language.SourceLocation} object
     */
    public MExceptionWhileDataFetching(ResultPath path, Throwable exception, SourceLocation sourceLocation) {
        this.path = assertNotNull(path).toList();
        this.exception = assertNotNull(exception);
        this.locations = Collections.singletonList(sourceLocation);
        this.extensions = mkExtensions(exception);
        this.message = mkMessage(path, exception);
    }

    /**
     * Mk message.
     *
     * @param path the path
     * @param exception the exception
     * @return the string
     */
    private String mkMessage(ResultPath path, Throwable exception) {
//        return format("Exception while fetching data (%s) : %s", path, exception.getMessage());
        return format("%s", exception.getMessage());
    }

    /**
     * Mk extensions.
     *
     * @param exception the exception
     * @return the map
     */
    /*
     * This allows a DataFetcher to throw a graphql error and have "extension data" be transferred from that
     * exception into the ExceptionWhileDataFetching error and hence have custom "extension attributes"
     * per error message.
     */
    private Map<String, Object> mkExtensions(Throwable exception) {
        Map<String, Object> extensions = null;
        if (exception instanceof GraphQLError) {
            Map<String, Object> map = ((GraphQLError) exception).getExtensions();
            if (map != null) {
                extensions = new LinkedHashMap<>(map);
            }
        }
        return extensions;
    }

    /**
     * <p>Getter for the field <code>exception</code>.</p>
     *
     * @return a {@link java.lang.Throwable} object
     */
    public Throwable getException() {
        return exception;
    }


    /** {@inheritDoc} */
    @Override
    public String getMessage() {
        return message;
    }

    /** {@inheritDoc} */
    @Override
    public List<SourceLocation> getLocations() {
        return locations;
    }

    /** {@inheritDoc} */
    @Override
    public List<Object> getPath() {
        return path;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ExceptionWhileDataFetching{" +
                "path=" + path +
                ", exception=" + exception +
                ", locations=" + locations +
                '}';
    } 

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        return GraphqlErrorHelper.equals(this, o);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return GraphqlErrorHelper.hashCode(this);
    }

}
