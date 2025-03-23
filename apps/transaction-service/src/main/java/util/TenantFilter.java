package util;

import io.quarkus.vertx.http.runtime.FilterConfig;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;

/**
 * Project: pay-stream
 * Module: util
 * File: TenantFilter
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class TenantFilter implements ContainerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Inject
    TenantContext tenantContext;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String tenantId = requestContext.getHeaderString(TENANT_HEADER);

        if (tenantId == null || tenantId.trim().isEmpty()) {
            // Create error response
            JsonObject error = Json.createObjectBuilder()
                    .add("status", 400)
                    .add("title", "Missing Required Header")
                    .add("detail", "The X-Tenant-ID header is required for all API requests")
                    .build();

            // Abort with error
            requestContext.abortWith(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(error.toString())
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
            return;
        }

        tenantContext.setTenantId(tenantId);
    }
}
