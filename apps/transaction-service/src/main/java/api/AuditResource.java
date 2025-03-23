package api;

import dto.AuditLogResponseDTO;
import dto.BankResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import models.AuditLog;
import models.Bank;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import service.IAuditService;
import util.RequiresRole;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project: pay-stream
 * Module: api
 * File: AuditResource
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/


@Path("/api/v1/audit")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Tag(name = "AuditLog", description = "Audit trail of all operations")
@RequiresRole("ADMIN")
public class AuditResource {

    @Context
    UriInfo uriInfo;
    @Inject
    IAuditService auditService;
    @GET
    @Operation(summary = "Get all audit logs",
            description = "Returns a list of all audit logs")
    @APIResponse(
            responseCode = "200",
            description = "List of audit logs",
            content = @Content(mediaType = "application/json")
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-Tenant-ID header"
    )
    @Parameters({
            @Parameter(
                    name = "X-Tenant-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Tenant identifier",
                    schema = @Schema(type = SchemaType.STRING)
            )
    })
    public Response getAllAuditLogs() {
        List<AuditLog> auditLogs = auditService.getAllAuditLogs();

        // Create a wrapper object to hold the list and add links to each bank
        List<Map<String, Object>> enrichedAuditLogs = auditLogs.stream()
                .map(auditLog -> {
                    AuditLogResponseDTO dto = new AuditLogResponseDTO(auditLog);

                    // Create links for this specific bank
                    URI selfUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(auditLog.id))
                            .build();

                    // Create a map with the bank data and its links
                    Map<String, Object> auditLogsWithLinks = new HashMap<>();
                    auditLogsWithLinks.put("auditLog", dto);

                    Map<String, String> links = new HashMap<>();
                    links.put("self", selfUri.toString());

                    auditLogsWithLinks.put("_links", links);

                    return auditLogsWithLinks;
                })
                .collect(Collectors.toList());

        // Create a wrapper object for the whole collection
        Map<String, Object> response = new HashMap<>();
        response.put("auditLogs", enrichedAuditLogs);
        response.put("count", enrichedAuditLogs.size());

        // Collection-level links
        Map<String, String> collectionLinks = new HashMap<>();
        collectionLinks.put("self", uriInfo.getAbsolutePath().toString());

        response.put("_links", collectionLinks);

        return Response.ok(response).build();
    }
}
