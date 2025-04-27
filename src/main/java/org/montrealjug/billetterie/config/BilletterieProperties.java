// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the billetterie application.
 */
@ConfigurationProperties(prefix = "app")
public record BilletterieProperties(String rsaKey) {}
