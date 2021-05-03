package de.fraunhofer.isst.dataspaceconnector.exceptions;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;

/**
 * Thrown to indicate that the rdf string could not be built.
 */
public class RdfBuilderException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a RdfBuilderException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public RdfBuilderException(final ErrorMessages msg) {
        super(msg.toString());
    }
}
