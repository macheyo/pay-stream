package util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Project: pay-stream
 * Module: util
 * File: TenantIdentifierResolver
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@ApplicationScoped
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Inject
    TenantContext tenantContext;

    @Override
    public String resolveCurrentTenantIdentifier() {
        return tenantContext.getTenantId();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
