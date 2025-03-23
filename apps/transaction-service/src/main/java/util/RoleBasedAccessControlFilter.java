package util;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Project: pay-stream
 * Module: util
 * File: RoleBasedAccessControlFilter
 * <p>
 * Created by: justice.m on 23/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@Provider
@Priority(Priorities.AUTHORIZATION)
public class RoleBasedAccessControlFilter implements ContainerRequestFilter {

    @Inject
    SecurityContext securityContext;

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get the resource method being invoked
        Method method = resourceInfo.getResourceMethod();

        // Check for role requirements on method
        RequiresRole methodRoles = method.getAnnotation(RequiresRole.class);
        if (methodRoles != null && !hasRequiredRoles(methodRoles)) {
            abortWithForbidden(requestContext);
            return;
        }

        // Check for role requirements on class if method has none
        if (methodRoles == null) {
            Class<?> resourceClass = resourceInfo.getResourceClass();
            RequiresRole classRoles = resourceClass.getAnnotation(RequiresRole.class);
            if (classRoles != null && !hasRequiredRoles(classRoles)) {
                abortWithForbidden(requestContext);
                return;
            }
        }
    }

    private boolean hasRequiredRoles(RequiresRole rolesAnnotation) {
        String[] requiredRoles = rolesAnnotation.value();
        boolean allRolesRequired = rolesAnnotation.allOf();

        if (allRolesRequired) {
            // All roles must be present
            return Arrays.stream(requiredRoles)
                    .allMatch(role -> securityContext.hasRole(role));
        } else {
            // Any role is sufficient
            return Arrays.stream(requiredRoles)
                    .anyMatch(role -> securityContext.hasRole(role));
        }
    }

    private void abortWithForbidden(ContainerRequestContext requestContext) {
        JsonObject error = Json.createObjectBuilder()
                .add("status", 403)
                .add("title", "Access Denied")
                .add("detail", "You don't have the required permissions to access this resource")
                .build();

        requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                        .entity(error.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }
}
