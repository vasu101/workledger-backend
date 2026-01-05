package com.workledger.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration for JPA Auditing.
 * Enables automatic population of @CreatedDate, @LastModifiedDate, @CreatedBy
 * and @LastModifiedFieldBy fields in entities.
 *
 * To be implemented after spring security -----
 */

public class JpaAuditingConfig {
}
