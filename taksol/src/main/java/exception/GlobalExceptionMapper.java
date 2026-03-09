package exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {

        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                "Terjadi kesalahan pada server",
                uriInfo.getPath());

        // return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        // .entity(error)
        // .build();

        exception.printStackTrace();
        return Response.status(500).entity(exception.getMessage()).build();

    }

}
