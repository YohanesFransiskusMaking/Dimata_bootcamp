package exception;

import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(UnauthorizedException exception) {

        ErrorResponse error = new ErrorResponse(
                401,
                "Unauthorized",
                exception.getMessage(),
                uriInfo.getPath());

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(error)
                .build();
    }
}
