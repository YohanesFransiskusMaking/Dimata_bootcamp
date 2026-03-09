package resource;

import java.net.URI;

import dto.request.ForgotPasswordRequest;
import dto.request.LoginRequest;
import dto.request.RefreshRequest;
import dto.request.ResetPasswordRequest;
import dto.request.UserRequest;
import dto.response.AuthResponse;
import dto.response.UserResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import service.AppUserService;
import service.AuthService;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    AppUserService userService;


    @Context
    HttpHeaders headers;

    @POST
    @Path("/register")
    public Response create(@Valid UserRequest request) {
        UserResponse response = userService.createUser(request);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @POST
    @Path("/login")
    public AuthResponse login(@Valid LoginRequest request) {
        return authService.login(
                request,
                headers.getHeaderString("User-Agent"),
                "unknown");
    }

    @POST
    @Path("/refresh")
    public AuthResponse refresh(@Valid RefreshRequest request) {
        return authService.refresh(
                request,
                headers.getHeaderString("User-Agent"),
                "unknown");
    }

    @POST
    @Path("/logout")
    public void logout(@Valid RefreshRequest request) {
        authService.logout(request.refreshToken);
    }

    @GET
    @Path("/verify")
    public Response verify(@QueryParam("token") String token) {

        authService.verifyEmail(token);

        return Response.seeOther(
                URI.create("http://localhost:3000/email-verified")).build();
    }

    @POST
    @Path("/forgot-password")
    public Response forgot(ForgotPasswordRequest request) {

        userService.forgotPassword(request.email);

        return Response.ok(
                "Jika email terdaftar, link reset telah dikirim").build();
    }

    @POST
    @Path("/reset-password")
    public Response reset(ResetPasswordRequest request) {

        userService.resetPassword(request.token, request.newPassword);

        return Response.ok("Password berhasil diubah").build();
    }

}
