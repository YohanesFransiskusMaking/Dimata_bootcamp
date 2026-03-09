package exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.Map;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    @Override
    public Response toResponse(DomainException ex) {

        return Response.status(ex.getStatus())
                .entity(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", ex.getStatus(),
                        "error", Response.Status.fromStatusCode(ex.getStatus()).getReasonPhrase(),
                        "message", ex.getMessage()
                ))
                .build();
    }
}