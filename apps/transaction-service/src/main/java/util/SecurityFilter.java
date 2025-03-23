package util;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Project: pay-stream
 * Module: util
 * File: SecurityFilter
 * <p>
 * Created by: justice.m on 23/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class SecurityFilter implements ContainerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String ROLES_HEADER = "X-User-Roles";


    @Inject
    SecurityContext securityContext;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Extract tenant ID
        String tenantId = requestContext.getHeaderString(TENANT_HEADER);
        if (tenantId == null || tenantId.trim().isEmpty()) {
            abortWithError(requestContext, "Missing required X-Tenant-ID header");
            return;
        }
        securityContext.setTenantId(tenantId);

        // Extract user ID
        String userId = requestContext.getHeaderString(USER_ID_HEADER);
        if (userId == null || userId.trim().isEmpty()) {
            abortWithError(requestContext, "Missing required X-User-ID header");
            return;
        }
        securityContext.setUserId(userId);

        // Extract user email (optional)
        String userEmail = requestContext.getHeaderString(USER_EMAIL_HEADER);
        securityContext.setUserEmail(userEmail);

        // Extract roles
        String rolesHeader = requestContext.getHeaderString(ROLES_HEADER);
        if (rolesHeader != null && !rolesHeader.trim().isEmpty()) {
            // Assuming roles are comma-separated
            Set<String> roles = Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            securityContext.setRoles(roles);
        }

    }

    private void abortWithError(ContainerRequestContext requestContext, String message) {

        // Exclude OPTIONS requests (CORS) and Swagger
        if (requestContext.getMethod().equals("OPTIONS") ||
                requestContext.getUriInfo().getPath().startsWith("swagger") ||
                requestContext.getUriInfo().getPath().startsWith("openapi")) {
            return;
        }

        JsonObject error = Json.createObjectBuilder()
                .add("status", 400)
                .add("title", "Authentication Error")
                .add("detail", message)
                .build();

        requestContext.abortWith(
                Response.status(Response.Status.BAD_REQUEST)
                        .entity(error.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }
}
