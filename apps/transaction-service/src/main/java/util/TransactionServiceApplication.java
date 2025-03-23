package util;

/**
 * Project: pay-stream
 * Module: util
 * File: TransactionServiceApplication
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

@OpenAPIDefinition(
        info = @Info(
                title = "Pay Stream API Documentation",
                version = "1.0.0",
                description = "A service for processing financial transactions",
                contact = @Contact(
                        name = "API Support",
                        email = "justicemacheyo@gmail.com"
                )
        ),
        security = {
                @SecurityRequirement(name = "tenant-header")
        }
)
@SecuritySchemes({
        @SecurityScheme(
                securitySchemeName = "tenant-header",
                type = SecuritySchemeType.APIKEY,
                apiKeyName = "X-Tenant-ID",
                in = org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn.HEADER,
                description = "Tenant identifier - required for all API calls"
        )
})
public class TransactionServiceApplication extends jakarta.ws.rs.core.Application {
}
