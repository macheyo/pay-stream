package api;

import dto.BankRequestDTO;
import dto.BankResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
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
import service.IBankService;
import util.RequiresRole;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project: pay-stream
 * Module: api
 * File: BankResource
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@Path("/api/v1/banks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Tag(name = "Bank", description = "Bank management operations")
public class BankResource {

    @Inject
    IBankService bankService;

    @Context
    UriInfo uriInfo;

    @POST
    @RequiresRole("ADMIN")
    @Operation(summary = "Create a new bank",
            description = "Creates a new bank with provided details")
    @APIResponse(
            responseCode = "201",
            description = "Bank created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BankResponseDTO.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-Tenant-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Email header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Roles header"
    )
    @Parameters({
            @Parameter(
                    name = "X-Tenant-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Tenant identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Email",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Email",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Roles",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Roles",
                    schema = @Schema(type = SchemaType.STRING)
            )
    })
    public Response createBank(@Valid BankRequestDTO bankRequestDTO) {
        // For now, using a mock user ID. In a real app, this would come from authentication
        String userId = "system";

        Bank bank = bankService.createBank(bankRequestDTO, userId);
        BankResponseDTO responseDTO = new BankResponseDTO(bank);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("bank", responseDTO);

        // Create all the necessary links
        Map<String, String> links = new HashMap<>();

        // Self link - link to this resource
        URI selfUri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(bank.id))
                .build();
        links.put("self", selfUri.toString());

        // Update link - PUT to the same URI
        URI updateUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(bank.id))
                .build();
        links.put("update", updateUri.toString());

        // Delete link - DELETE to the same URI
        URI deleteUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(bank.id))
                .build();
        links.put("delete", deleteUri.toString());

        // Deactivate link
        URI toggleStatusUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(bank.id))
                .path("toggle-status")
                .build();
        links.put("toggle-status", toggleStatusUri.toString());

        // Collection link - back to all banks
        URI collectionUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .build();
        links.put("collection", collectionUri.toString());

        // Add the links to the response
        response.put("_links", links);

        return Response.created(selfUri)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a bank by ID",
            description = "Returns a bank by its ID")
    @APIResponse(
            responseCode = "200",
            description = "Bank found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BankResponseDTO.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "Bank not found"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-Tenant-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Email header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Roles header"
    )
    @Parameters({
            @Parameter(
                    name = "X-Tenant-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Tenant identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Email",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Email",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Roles",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Roles",
                    schema = @Schema(type = SchemaType.STRING)
            )
    })
    public Response getBank(@PathParam("id") Long id) {
        Bank bank = bankService.getBank(id);
        BankResponseDTO responseDTO = new BankResponseDTO(bank);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("bank", responseDTO);

        // Create all the necessary links
        Map<String, String> links = new HashMap<>();

        // Self link - link to this resource
        URI selfUri = uriInfo.getAbsolutePath();
        links.put("self", selfUri.toString());

        // Update link - PUT to the same URI
        URI updateUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(id))
                .build();
        links.put("update", updateUri.toString());

        // Delete link - DELETE to the same URI
        URI deleteUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(id))
                .build();
        links.put("delete", deleteUri.toString());

        // Deactivate link
        URI toggleStatusUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(id))
                .path("toggle-status")
                .build();
        links.put("toggle-status", toggleStatusUri.toString());

        // Collection link - back to all banks
        URI collectionUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .build();
        links.put("collection", collectionUri.toString());

        // Add the links to the response
        response.put("_links", links);

        return Response.ok(response).build();
    }

    @PUT
    @RequiresRole("ADMIN")
    @Path("/{id}/toggle-status")
    @Operation(summary = "Toggle status of a bank",
            description = "Toggles the status of a bank")
    @APIResponse(
            responseCode = "200",
            description = "Bank status changed successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BankResponseDTO.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "Bank not found"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-Tenant-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Email header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Roles header"
    )
    @Parameters({
            @Parameter(
                    name = "X-Tenant-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Tenant identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Email",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Email",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Roles",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Roles",
                    schema = @Schema(type = SchemaType.STRING)
            )
    })
    public Response toggleBankStatus(@PathParam("id") Long id) {
        // For now, using a mock user ID. In a real app, this would come from authentication
        String userId = "system";

        // Create a DTO with active=false
        Bank selectedBank = bankService.getBank(id);
        BankRequestDTO bankRequestDTO = BankRequestDTO.toggleStatus(selectedBank);
        Bank bank = bankService.updateBank(id, bankRequestDTO, userId);
        BankResponseDTO responseDTO = new BankResponseDTO(bank);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("bank", responseDTO);

        // Create all the necessary links
        Map<String, String> links = new HashMap<>();

        // Self link - link to this resource
        URI selfUri = uriInfo.getAbsolutePath();
        links.put("self", selfUri.toString());

        // Update link - PUT to the same URI
        URI updateUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(id))
                .build();
        links.put("update", updateUri.toString());

        // Delete link - DELETE to the same URI
        URI deleteUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(id))
                .build();
        links.put("delete", deleteUri.toString());

        // Deactivate link
        URI toggleStatusUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path(String.valueOf(id))
                .path("toggle-status")
                .build();
        links.put("toggle-status", toggleStatusUri.toString());

        // Collection link - back to all banks
        URI collectionUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .build();
        links.put("collection", collectionUri.toString());

        // Add the links to the response
        response.put("_links", links);

        return Response.ok(response).build();

    }


    @GET
    @Operation(summary = "Get all banks",
            description = "Returns a list of all banks")
    @APIResponse(
            responseCode = "200",
            description = "List of banks",
            content = @Content(mediaType = "application/json")
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-Tenant-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Email header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Roles header"
    )
    @Parameters({
            @Parameter(
                    name = "X-Tenant-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Tenant identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Email",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Email",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Roles",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Roles",
                    schema = @Schema(type = SchemaType.STRING)
            )
    })
    public Response getAllBanks() {
        List<Bank> banks = bankService.getAllBanks();

        // Create a wrapper object to hold the list and add links to each bank
        List<Map<String, Object>> enrichedBanks = banks.stream()
                .map(bank -> {
                    BankResponseDTO dto = new BankResponseDTO(bank);

                    // Create links for this specific bank
                    URI selfUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(bank.id))
                            .build();

                    URI updateUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(bank.id))
                            .build();

                    URI deleteUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(bank.id))
                            .build();

                    URI toggleStatusUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(bank.id))
                            .path("toggle-status")
                            .build();

                    // Create a map with the bank data and its links
                    Map<String, Object> bankWithLinks = new HashMap<>();
                    bankWithLinks.put("bank", dto);

                    Map<String, String> links = new HashMap<>();
                    links.put("self", selfUri.toString());
                    links.put("update", updateUri.toString());
                    links.put("delete", deleteUri.toString());
                    links.put("toggle-status", toggleStatusUri.toString());

                    bankWithLinks.put("_links", links);

                    return bankWithLinks;
                })
                .collect(Collectors.toList());

        // Create a wrapper object for the whole collection
        Map<String, Object> response = new HashMap<>();
        response.put("banks", enrichedBanks);
        response.put("count", enrichedBanks.size());

        // Collection-level links
        Map<String, String> collectionLinks = new HashMap<>();
        collectionLinks.put("self", uriInfo.getAbsolutePath().toString());

        // Add link to create a new bank
        URI createUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .build();
        collectionLinks.put("create", createUri.toString());

        // Add link to active banks
        URI activeBanksUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path("active")
                .build();
        collectionLinks.put("active-banks", activeBanksUri.toString());

        response.put("_links", collectionLinks);

        return Response.ok(response).build();
    }

    @GET
    @Path("/active")
    @Operation(summary = "Get all active banks",
            description = "Returns a list of all active banks")
    @APIResponse(
            responseCode = "200",
            description = "List of active banks",
            content = @Content(mediaType = "application/json")
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-Tenant-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-ID header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Email header"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-User-Roles header"
    )
    @Parameters({
            @Parameter(
                    name = "X-Tenant-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Tenant identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User identifier",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Email",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Email",
                    schema = @Schema(type = SchemaType.STRING)
            ),
            @Parameter(
                    name = "X-User-Roles",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "User Roles",
                    schema = @Schema(type = SchemaType.STRING)
            )
    })
    public Response getAllActiveBanks() {
        List<Bank> banks = bankService.getActiveBanks();

        // Create a wrapper object to hold the list and add links to each bank
        List<Map<String, Object>> enrichedBanks = banks.stream()
                .map(bank -> {
                    BankResponseDTO dto = new BankResponseDTO(bank);

                    // Create links for this specific bank
                    URI selfUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(bank.id))
                            .build();

                    URI updateUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(bank.id))
                            .build();

                    URI deleteUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(bank.id))
                            .build();

                    URI toggleStatusUri = uriInfo.getBaseUriBuilder()
                            .path(BankResource.class)
                            .path(String.valueOf(bank.id))
                            .path("toggle-status")
                            .build();

                    // Create a map with the bank data and its links
                    Map<String, Object> bankWithLinks = new HashMap<>();
                    bankWithLinks.put("bank", dto);

                    Map<String, String> links = new HashMap<>();
                    links.put("self", selfUri.toString());
                    links.put("update", updateUri.toString());
                    links.put("delete", deleteUri.toString());
                    links.put("toggle-status", toggleStatusUri.toString());

                    bankWithLinks.put("_links", links);

                    return bankWithLinks;
                })
                .collect(Collectors.toList());

        // Create a wrapper object for the whole collection
        Map<String, Object> response = new HashMap<>();
        response.put("banks", enrichedBanks);
        response.put("count", enrichedBanks.size());

        // Collection-level links
        Map<String, String> collectionLinks = new HashMap<>();
        collectionLinks.put("self", uriInfo.getAbsolutePath().toString());

        // Add link to create a new bank
        URI createUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .build();
        collectionLinks.put("create", createUri.toString());

        // Add link to active banks
        URI activeBanksUri = uriInfo.getBaseUriBuilder()
                .path(BankResource.class)
                .path("active")
                .build();
        collectionLinks.put("active-banks", activeBanksUri.toString());

        response.put("_links", collectionLinks);

        return Response.ok(response).build();
    }
}