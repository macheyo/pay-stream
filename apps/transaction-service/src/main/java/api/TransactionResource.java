package api;

import dto.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import models.Transaction;
import models.TransactionStatus;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import service.TransactionService;
import util.RequiresRole;
import util.SecurityContext;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project: pay-stream
 * Module: api
 * File: TransactionResource
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@Path("/api/v1/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Tag(name = "Transaction", description = "Transaction management operations")
public class TransactionResource {

    @Inject
    TransactionService transactionService;

    @Inject
    SecurityContext securityContext;

    @Context
    UriInfo uriInfo;

    @POST
    @RequiresRole("TRANSACTION_CREATOR")
    @Operation(summary = "Create a new transaction",
            description = "Creates a new transaction with PENDING_APPROVAL status")
    @APIResponse(
            responseCode = "201",
            description = "Transaction created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TransactionResponseDTO.class))
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
    public Response createTransaction(@Valid TransactionRequestDTO requestDTO) {
        // For now, using a mock user ID. In a real app, this would come from authentication
        String userId = "system";

        Transaction transaction = transactionService.createTransaction(requestDTO, userId);
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(transaction);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("transaction", responseDTO);

        // Create all the necessary links
        Map<String, String> links = new HashMap<>();

        // Self link - link to this resource
        URI selfUri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(transaction.id))
                .build();
        links.put("self", selfUri.toString());

        // Update link - PUT to the same URI
        URI updateUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(String.valueOf(transaction.id))
                .build();
        links.put("update", updateUri.toString());

        // Delete link - DELETE to the same URI
        URI deleteUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(String.valueOf(transaction.id))
                .build();
        links.put("delete", deleteUri.toString());

        if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
            // Check if user has approver role
            if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                    !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                URI approveUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path(String.valueOf(transaction.id))
                        .path("approve")
                        .build();
                links.put("approve", approveUri.toString());

                URI rejectUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path(String.valueOf(transaction.id))
                        .path("reject")
                        .build();
                links.put("reject", rejectUri.toString());
            }
        }

        // Collection link - back to all banks
        URI collectionUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("collection", collectionUri.toString());

        // Batch Link
        if (transaction.getBatchId() != null) {
            URI batchUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path("batch")
                    .path(String.valueOf(transaction.getBatchId()))
                    .build();
            links.put("batch", batchUri.toString());
        }

        // Add the links to the response
        response.put("_links", links);

        return Response.created(selfUri)
                .entity(response)
                .build();

    }

    @POST
    @RequiresRole("TRANSACTION_CREATOR")
    @Path("/bulk")
    @Operation(summary = "Create multiple transactions in a batch",
            description = "Creates multiple transactions with a common batch ID and PENDING_APPROVAL status")
    @APIResponse(
            responseCode = "201",
            description = "Transactions created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BulkTransactionResponseDTO.class))
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
    public Response createBulkTransactions(@Valid BulkTransactionRequestDTO requestDTO) {
        // For now, using a mock user ID. In a real app, this would come from authentication
        String userId = "system";

        List<Transaction> transactions = transactionService.createBulkTransactions(requestDTO, userId);
        String batchId = transactions.get(0).getBatchId();
        BulkTransactionResponseDTO responseDTO = new BulkTransactionResponseDTO(batchId, transactions);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("batch", responseDTO);

        // Create links map
        Map<String, Object> links = new HashMap<>();

        // Self/batch link
        URI batchUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("batch")
                .path(batchId)
                .build();
        links.put("self", batchUri.toString());

        // Collection link
        URI transactionsUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("transactions", transactionsUri.toString());

        // Links for individual transactions
        Map<String, Object> transactionLinks = new HashMap<>();
        for (Transaction transaction : transactions) {
            URI transactionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();

            Map<String, String> txLinks = new HashMap<>();
            txLinks.put("self", transactionUri.toString());
            // Self link - link to this resource
            URI selfUri = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("self", selfUri.toString());

            // Update link - PUT to the same URI
            URI updateUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("update", updateUri.toString());

            // Delete link - DELETE to the same URI
            URI deleteUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("delete", deleteUri.toString());

            if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
                // Check if user has approver role
                if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                        !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                    URI approveUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("approve")
                            .build();
                    links.put("approve", approveUri.toString());

                    URI rejectUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("reject")
                            .build();
                    links.put("reject", rejectUri.toString());
                }
            }

            // Collection link - back to all banks
            URI collectionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .build();
            links.put("collection", collectionUri.toString());

            transactionLinks.put(String.valueOf(transaction.id), txLinks);
        }
        links.put("transactions", transactionLinks);

        // Add status filter link
        URI pendingUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("status")
                .path("PENDING_APPROVAL")
                .build();
        links.put("pending-transactions", pendingUri.toString());

        URI approvedUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("status")
                .path("APPROVED")
                .build();
        links.put("approved-transactions", approvedUri.toString());

        URI rejectedUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("status")
                .path("REJECTED")
                .build();
        links.put("rejected-transactions", rejectedUri.toString());

        response.put("_links", links);

        return Response.created(batchUri)
                .entity(response)
                .build();
    }

    @GET
    @RequiresRole({"TRANSACTION_CREATOR", "TRANSACTION_APPROVER", "TRANSACTION_VIEWER"})
    @Path("/{id}")
    @Operation(summary = "Get a transaction by ID",
            description = "Returns a transaction by its ID")
    @APIResponse(
            responseCode = "200",
            description = "Transaction found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TransactionResponseDTO.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "Transaction not found"
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
    public Response getTransaction(@PathParam("id") Long id) {
        Transaction transaction = transactionService.getTransaction(id);
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(transaction);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("transaction", responseDTO);

        // Create all the necessary links
        Map<String, String> links = new HashMap<>();

        // Self link - link to this resource
        URI selfUri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(transaction.id))
                .build();
        links.put("self", selfUri.toString());

        // Update link - PUT to the same URI
        URI updateUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(String.valueOf(transaction.id))
                .build();
        links.put("update", updateUri.toString());

        // Delete link - DELETE to the same URI
        URI deleteUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(String.valueOf(transaction.id))
                .build();
        links.put("delete", deleteUri.toString());

        if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
            // Check if user has approver role
            if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                    !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                URI approveUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path(String.valueOf(transaction.id))
                        .path("approve")
                        .build();
                links.put("approve", approveUri.toString());

                URI rejectUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path(String.valueOf(transaction.id))
                        .path("reject")
                        .build();
                links.put("reject", rejectUri.toString());
            }
        }

        // Collection link - back to all banks
        URI collectionUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("collection", collectionUri.toString());

        // Batch Link
        if (transaction.getBatchId() != null) {
            URI batchUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path("batch")
                    .path(String.valueOf(transaction.getBatchId()))
                    .build();
            links.put("batch", batchUri.toString());
        }

        // Add the links to the response
        response.put("_links", links);

        return Response.created(selfUri)
                .entity(response)
                .build();
    }

    @GET
    @RequiresRole({"TRANSACTION_CREATOR", "TRANSACTION_APPROVER", "TRANSACTION_VIEWER"})
    @Path("/batch/{batchId}")
    @Operation(summary = "Get transactions by batch ID",
            description = "Returns all transactions that belong to a specific batch")
    @APIResponse(
            responseCode = "200",
            description = "Transactions found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BulkTransactionResponseDTO.class))
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
    public Response getTransactionsByBatchId(@PathParam("batchId") String batchId) {
        List<Transaction> transactions = transactionService.getTransactionsByBatchId(batchId);
        BulkTransactionResponseDTO responseDTO = new BulkTransactionResponseDTO(batchId, transactions);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("batch", responseDTO);

        // Create links map
        Map<String, Object> links = new HashMap<>();

        // Self/batch link
        URI batchUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("batch")
                .path(batchId)
                .build();
        links.put("self", batchUri.toString());

        // Collection link
        URI transactionsUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("transactions", transactionsUri.toString());

        // Links for individual transactions
        Map<String, Object> transactionLinks = new HashMap<>();
        for (Transaction transaction : transactions) {
            URI transactionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();

            Map<String, String> txLinks = new HashMap<>();
            txLinks.put("self", transactionUri.toString());
            // Self link - link to this resource
            URI selfUri = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("self", selfUri.toString());

            // Update link - PUT to the same URI
            URI updateUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("update", updateUri.toString());

            // Delete link - DELETE to the same URI
            URI deleteUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("delete", deleteUri.toString());

            if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
                // Check if user has approver role
                if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                        !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                    URI approveUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("approve")
                            .build();
                    links.put("approve", approveUri.toString());

                    URI rejectUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("reject")
                            .build();
                    links.put("reject", rejectUri.toString());
                }
            }

            // Collection link - back to all banks
            URI collectionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .build();
            links.put("collection", collectionUri.toString());

            transactionLinks.put(String.valueOf(transaction.id), txLinks);
        }
        links.put("transactions", transactionLinks);

        // Add status filter link
        URI pendingUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("status")
                .path("PENDING_APPROVAL")
                .build();
        links.put("pending-transactions", pendingUri.toString());

        URI approvedUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("status")
                .path("APPROVED")
                .build();
        links.put("approved-transactions", approvedUri.toString());

        URI rejectedUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("status")
                .path("REJECTED")
                .build();
        links.put("rejected-transactions", rejectedUri.toString());

        response.put("_links", links);

        return Response.created(batchUri)
                .entity(response)
                .build();
    }

    @PUT
    @RequiresRole("TRANSACTION_APPROVER")
    @Path("/{id}/approve")
    @Operation(summary = "Approve a transaction",
            description = "Approves a pending transaction")
    @APIResponse(
            responseCode = "200",
            description = "Transaction approved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TransactionResponseDTO.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Transaction is not in pending state or other validation error"
    )
    @APIResponse(
            responseCode = "404",
            description = "Transaction not found"
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
    public Response approveTransaction(
            @PathParam("id") Long id,
            @Valid ApprovalRequestDTO approvalDTO) {

        // For now, using a mock user ID. In a real app, this would come from authentication
        String approverId = "approver";

        Transaction transaction = transactionService.approveTransaction(id, approvalDTO, approverId);
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(transaction);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("transaction", responseDTO);

        // Create all the necessary links
        Map<String, String> links = new HashMap<>();

        // Self link - link to this resource
        URI selfUri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(transaction.id))
                .build();
        links.put("self", selfUri.toString());

        // Update link - PUT to the same URI
        URI updateUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(String.valueOf(transaction.id))
                .build();
        links.put("update", updateUri.toString());

        // Delete link - DELETE to the same URI
        URI deleteUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(String.valueOf(transaction.id))
                .build();
        links.put("delete", deleteUri.toString());

        if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
            // Check if user has approver role
            if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                    !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                URI approveUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path(String.valueOf(transaction.id))
                        .path("approve")
                        .build();
                links.put("approve", approveUri.toString());

                URI rejectUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path(String.valueOf(transaction.id))
                        .path("reject")
                        .build();
                links.put("reject", rejectUri.toString());
            }
        }

        // Collection link - back to all banks
        URI collectionUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("collection", collectionUri.toString());

        // Batch Link
        if (transaction.getBatchId() != null) {
            URI batchUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path("batch")
                    .path(String.valueOf(transaction.getBatchId()))
                    .build();
            links.put("batch", batchUri.toString());
        }

        // Add the links to the response
        response.put("_links", links);

        return Response.created(selfUri)
                .entity(response)
                .build();
    }

    @PUT
    @RequiresRole("TRANSACTION_APPROVER")
    @Path("/{id}/reject")
    @Operation(summary = "Reject a transaction",
            description = "Rejects a pending transaction")
    @APIResponse(
            responseCode = "200",
            description = "Transaction rejected successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TransactionResponseDTO.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Transaction is not in pending state or other validation error"
    )
    @APIResponse(
            responseCode = "404",
            description = "Transaction not found"
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
    public Response rejectTransaction(
            @PathParam("id") Long id,
            @Valid RejectionRequestDTO rejectionDTO) {

        // For now, using a mock user ID. In a real app, this would come from authentication
        String rejecterId = "rejector";

        Transaction transaction = transactionService.rejectTransaction(id, rejectionDTO, rejecterId);
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(transaction);

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("transaction", responseDTO);

        // Create all the necessary links
        Map<String, String> links = new HashMap<>();

        // Self link - link to this resource
        URI selfUri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(transaction.id))
                .build();
        links.put("self", selfUri.toString());

        // Update link - PUT to the same URI
        URI updateUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(String.valueOf(transaction.id))
                .build();
        links.put("update", updateUri.toString());

        // Delete link - DELETE to the same URI
        URI deleteUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(String.valueOf(transaction.id))
                .build();
        links.put("delete", deleteUri.toString());

        if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
            // Check if user has approver role
            if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                    !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                URI approveUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path(String.valueOf(transaction.id))
                        .path("approve")
                        .build();
                links.put("approve", approveUri.toString());

                URI rejectUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path(String.valueOf(transaction.id))
                        .path("reject")
                        .build();
                links.put("reject", rejectUri.toString());
            }
        }

        // Collection link - back to all banks
        URI collectionUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("collection", collectionUri.toString());

        // Batch Link
        if (transaction.getBatchId() != null) {
            URI batchUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path("batch")
                    .path(String.valueOf(transaction.getBatchId()))
                    .build();
            links.put("batch", batchUri.toString());
        }

        // Add the links to the response
        response.put("_links", links);

        return Response.created(selfUri)
                .entity(response)
                .build();
    }

    @GET
    @RequiresRole({"TRANSACTION_CREATOR", "TRANSACTION_APPROVER", "TRANSACTION_VIEWER"})
    @Path("/status/{status}")
    @Operation(summary = "Get transactions by status",
            description = "Returns all transactions with the specified status")
    @APIResponse(
            responseCode = "200",
            description = "Transactions found",
            content = @Content(mediaType = "application/json")
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid status provided"
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
    public Response getTransactionsByStatus(@PathParam("status") String status) {
        List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(TransactionResponseDTO::new)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("count", transactions.size());
        response.put("status", status);
        response.put("transactions", transactions);

        // Create links map
        Map<String, Object> links = new HashMap<>();

        // Self link
        URI selfUri = uriInfo.getAbsolutePath();
        links.put("self", selfUri.toString());

        // Collection link
        URI transactionsUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("transactions", transactionsUri.toString());

        // Add other status links
        TransactionStatus[] allStatuses = TransactionStatus.values();
        for (TransactionStatus ts : allStatuses) {
            if (!ts.name().equalsIgnoreCase(status)) {
                URI statusUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path("status")
                        .path(ts.name())
                        .build();
                links.put(ts.name().toLowerCase() + "-transactions", statusUri.toString());
            }
        }

        // Links for individual transactions
        Map<String, Object> transactionLinks = new HashMap<>();
        for (Transaction transaction : transactions) {
            URI transactionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();

            Map<String, String> txLinks = new HashMap<>();
            txLinks.put("self", transactionUri.toString());
            // Update link - PUT to the same URI
            URI updateUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("update", updateUri.toString());

            // Delete link - DELETE to the same URI
            URI deleteUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("delete", deleteUri.toString());

            // Add batch link if part of a batch
            if (transaction.getBatchId() != null) {
                URI batchUri = uriInfo.getBaseUriBuilder()
                        .path(TransactionResource.class)
                        .path("batch")
                        .path(transaction.getBatchId())
                        .build();
                txLinks.put("batch", batchUri.toString());
            }

            if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
                // Check if user has approver role
                if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                        !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                    URI approveUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("approve")
                            .build();
                    links.put("approve", approveUri.toString());

                    URI rejectUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("reject")
                            .build();
                    links.put("reject", rejectUri.toString());
                }
            }

            // Collection link - back to all banks
            URI collectionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .build();
            links.put("collection", collectionUri.toString());

            transactionLinks.put(String.valueOf(transaction.id), txLinks);
        }
        links.put("transaction-details", transactionLinks);

        response.put("_links", links);

        return Response.ok(response).build();
    }

    @PUT
    @Path("/batch/{batchId}/approve")
    @Operation(summary = "Approve all pending transactions in a batch",
            description = "Approves all pending transactions belonging to a specific batch")
    @APIResponse(
            responseCode = "200",
            description = "Transactions approved successfully",
            content = @Content(mediaType = "application/json")
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid batch ID or no pending transactions in batch"
    )
    @APIResponse(
            responseCode = "403",
            description = "User not authorized to approve transactions"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-Tenant-ID header"
    )
    @RequiresRole("TRANSACTION_APPROVER")
    @Parameters({
            @Parameter(
                    name = "X-Tenant-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Tenant identifier",
                    schema = @Schema(type = SchemaType.STRING)
            )
    })
    public Response batchApproveTransactions(
            @PathParam("batchId") String batchId,
            @Valid ApprovalRequestDTO approvalDTO) {

        // Get user ID from security context
        String approverId = securityContext.getUserId();

        // Call service layer to perform batch approval
        List<Transaction> approvedTransactions = transactionService.batchApproveTransactions(batchId, approvalDTO, approverId);

        // Create response DTOs
        List<TransactionResponseDTO> responseDTOs = approvedTransactions.stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("batchId", batchId);
        response.put("approvedCount", approvedTransactions.size());
        response.put("transactions", responseDTOs);

        // Create links map
        Map<String, Object> links = new HashMap<>();

        // Self link
        URI selfUri = uriInfo.getAbsolutePath();
        links.put("self", selfUri.toString());

        // Batch link
        URI batchUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("batch")
                .path(batchId)
                .build();
        links.put("batch", batchUri.toString());

        // Transaction collection link
        URI transactionsUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("transactions", transactionsUri.toString());

        // Links for individual transactions
        Map<String, Object> transactionLinks = new HashMap<>();
        for (Transaction transaction : approvedTransactions) {
            URI transactionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();

            Map<String, String> txLinks = new HashMap<>();
            txLinks.put("self", transactionUri.toString());
            // Self link - link to this resource
            links.put("self", selfUri.toString());

            // Update link - PUT to the same URI
            URI updateUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("update", updateUri.toString());

            // Delete link - DELETE to the same URI
            URI deleteUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("delete", deleteUri.toString());

            if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
                // Check if user has approver role
                if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                        !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                    URI approveUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("approve")
                            .build();
                    links.put("approve", approveUri.toString());

                    URI rejectUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("reject")
                            .build();
                    links.put("reject", rejectUri.toString());
                }
            }

            // Collection link - back to all banks
            URI collectionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .build();
            links.put("collection", collectionUri.toString());

            transactionLinks.put(String.valueOf(transaction.id), txLinks);
        }
        links.put("transaction-details", transactionLinks);

        response.put("_links", links);

        return Response.ok(response).build();
    }

    @PUT
    @Path("/batch/{batchId}/reject")
    @Operation(summary = "Reject all pending transactions in a batch",
            description = "Rejects all pending transactions belonging to a specific batch")
    @APIResponse(
            responseCode = "200",
            description = "Transactions rejected successfully",
            content = @Content(mediaType = "application/json")
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid batch ID or no pending transactions in batch"
    )
    @APIResponse(
            responseCode = "403",
            description = "User not authorized to reject transactions"
    )
    @APIResponse(
            responseCode = "400",
            description = "Missing required X-Tenant-ID header"
    )
    @RequiresRole("TRANSACTION_APPROVER")
    @Parameters({
            @Parameter(
                    name = "X-Tenant-ID",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Tenant identifier",
                    schema = @Schema(type = SchemaType.STRING)
            )
    })
    public Response batchRejectTransactions(
            @PathParam("batchId") String batchId,
            @Valid RejectionRequestDTO rejectionDTO) {

        // Get user ID from security context
        String rejecterId = securityContext.getUserId();

        // Call service layer to perform batch rejection
        List<Transaction> rejectedTransactions = transactionService.batchRejectTransactions(batchId, rejectionDTO, rejecterId);

        // Create response DTOs
        List<TransactionResponseDTO> responseDTOs = rejectedTransactions.stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());

        // Create a wrapper object for the response with links
        Map<String, Object> response = new HashMap<>();
        response.put("batchId", batchId);
        response.put("rejectedCount", rejectedTransactions.size());
        response.put("transactions", responseDTOs);

        // Create links map
        Map<String, Object> links = new HashMap<>();

        // Self link
        URI selfUri = uriInfo.getAbsolutePath();
        links.put("self", selfUri.toString());

        // Batch link
        URI batchUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path("batch")
                .path(batchId)
                .build();
        links.put("batch", batchUri.toString());

        // Transaction collection link
        URI transactionsUri = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build();
        links.put("transactions", transactionsUri.toString());

        // Links for individual transactions
        Map<String, Object> transactionLinks = new HashMap<>();
        for (Transaction transaction : rejectedTransactions) {
            URI transactionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();

            Map<String, String> txLinks = new HashMap<>();
            txLinks.put("self", transactionUri.toString());
            // Self link - link to this resource
            links.put("self", selfUri.toString());

            // Update link - PUT to the same URI
            URI updateUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("update", updateUri.toString());

            // Delete link - DELETE to the same URI
            URI deleteUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(String.valueOf(transaction.id))
                    .build();
            links.put("delete", deleteUri.toString());

            if (transaction.getStatus() == TransactionStatus.PENDING_APPROVAL) {
                // Check if user has approver role
                if (securityContext.hasRole("TRANSACTION_APPROVER") &&
                        !transaction.getCreatedBy().equals(securityContext.getUserId())) {

                    URI approveUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("approve")
                            .build();
                    links.put("approve", approveUri.toString());

                    URI rejectUri = uriInfo.getBaseUriBuilder()
                            .path(TransactionResource.class)
                            .path(String.valueOf(transaction.id))
                            .path("reject")
                            .build();
                    links.put("reject", rejectUri.toString());
                }
            }

            // Collection link - back to all banks
            URI collectionUri = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .build();
            links.put("collection", collectionUri.toString());

            transactionLinks.put(String.valueOf(transaction.id), txLinks);
        }
        links.put("transaction-details", transactionLinks);

        response.put("_links", links);

        return Response.ok(response).build();
    }
}
