package exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        String message = exception.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .distinct()
                .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                .orElse("Validation error");

        ErrorResponse error = new ErrorResponse(
                400,
                "Bad Request",
                message,
                uriInfo.getPath());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }
}
