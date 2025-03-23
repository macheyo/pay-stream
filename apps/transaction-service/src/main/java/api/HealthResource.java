package api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Project: pay-stream
 * Module: api
 * File: HealthResource
 * <p>
 * Created by: justice.m on 23/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@Path("/health")
public class HealthResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }
}
