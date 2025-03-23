package repository;

/**
 * Project: pay-stream
 * Module: repository
 * File: TenantAwareRepository
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public interface TenantAwareRepository<T> {
    void setTenantFilter(T entity);
}
