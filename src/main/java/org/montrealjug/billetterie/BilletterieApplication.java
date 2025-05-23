// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie;

import org.montrealjug.billetterie.config.BilletterieProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties(BilletterieProperties.class)
@EnableJpaRepositories(basePackages = "org.montrealjug.billetterie.repository")
public class BilletterieApplication {

    public static void main(String[] args) {
        SpringApplication.run(BilletterieApplication.class, args);
    }
}
