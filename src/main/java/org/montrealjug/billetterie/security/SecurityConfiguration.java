// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.security;

import jakarta.servlet.SessionTrackingMode;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.Cookie;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableConfigurationProperties(SecurityConfiguration.AdminProperties.class)
public class SecurityConfiguration {

    private static final String ADMIN_ROLE = "admin";
    private static final String ACTUATOR_ROLE = "actuator";

    // we define the basic auth for actuator before
    // to avoid clash between our security chains, we have to use `securityMatcher`
    // to scope this security chain on the `actuator/**` path only.
    // as `actuator` is `read-only` in the config, we have a small risk of csrf:
    // an attacker could forge a link that, if clicked by a user with saved `actuator`
    // credentials in their browser, could leak some info.
    // as `threaddump` and `heapdump` are also disabled, I think we can live
    // with the threat of leaking our `beans` hierarchy or our `loggers` config
    @Bean
    @Order(0)
    SecurityFilterChain actuatorBasicAuth(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatchers(
                        securityMatcher -> securityMatcher.requestMatchers("/actuator/**"))
                .authorizeHttpRequests(
                        authorizationRequests ->
                                authorizationRequests.anyRequest().hasRole(ACTUATOR_ROLE))
                .httpBasic(httpBasic -> httpBasic.realmName("actuator realm"))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .build();
    }

    // quick note on csrf
    // we are safe because:
    // - we do set `SameSite: Strict` on our session cookie
    // - we do set a `path` to `/admin` on our session cookie
    // - we do not have mutations done via `safe` methods
    // - we do not use `javascript` outside of simple vanilla scripts inlined in our pages
    // we should enhance our solution by signing our session cookie in order to mitigate an attacker
    // trying to guess, by brute force a valid session-id... but this is kind of overkill for us
    // and could be done later in another `PR`
    // for more on CSRF:
    // https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html
    @Bean
    @Order(1)
    SecurityFilterChain appSecurityFilterChain(HttpSecurity http, AdminProperties adminProperties)
            throws Exception {
        return http.authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/admin/**")
                                        .hasRole(ADMIN_ROLE)
                                        // we can authorize everything else,
                                        // as `/actuator/**` is handled first
                                        .anyRequest()
                                        .permitAll())
                .formLogin(
                        loginForm ->
                                loginForm
                                        .loginPage("/admin/login")
                                        .defaultSuccessUrl("/admin/events")
                                        .failureForwardUrl("/login-failed")
                                        .permitAll())
                .logout(
                        logout ->
                                logout.clearAuthentication(true)
                                        .invalidateHttpSession(true)
                                        .logoutUrl("/admin/logout")
                                        .logoutSuccessUrl("/")
                                        .deleteCookies(adminProperties.sessionCookieName()))
                .sessionManagement(
                        session ->
                                session.sessionConcurrency(
                                        concurrency -> concurrency.expiredUrl("/")))
                .csrf(AbstractHttpConfigurer::disable)
                // no need for CORS for our `app`, so let's be sure its forbidden
                .cors(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder, AdminProperties adminProperties) {
        var adminPassword = passwordEncoder.encode(adminProperties.adminPassword());
        var adminUser =
                User.builder()
                        .username(adminProperties.adminUsername())
                        .password(adminPassword)
                        .roles(ADMIN_ROLE)
                        .build();
        var actuatorPassword = passwordEncoder.encode(adminProperties.actuatorPassword());
        var actuatorUser =
                User.builder()
                        .username(adminProperties.actuatorUsername())
                        .password(actuatorPassword)
                        .roles(ACTUATOR_ROLE)
                        .build();
        return new InMemoryUserDetailsManager(adminUser, actuatorUser);
    }

    // There are properties in server.servlet.session.cookie namespace that can (should?) be used,
    // but setting stuff by code makes it easier to test, as it's not in the `application.yml`
    // currently we don't have a dedicated profile for `test`,
    // so we can't test the values in our `application.yml` used for `prod`
    @Bean
    ServletContextInitializer securityInitializer(AdminProperties adminProperties) {
        return sc -> {
            // disable session identification by URL by forcing Cookie only
            sc.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));
            var sessionCookieConfig = sc.getSessionCookieConfig();
            sessionCookieConfig.setHttpOnly(true);
            sessionCookieConfig.setSecure(true);
            sessionCookieConfig.setName(adminProperties.sessionCookieName());
            // only set Cookie for `/admin` (our authenticated path)
            sessionCookieConfig.setPath("/admin");
            // enforce SameSite: Strict to avoid dealing with csrf
            sessionCookieConfig.setAttribute("SameSite", Cookie.SameSite.STRICT.attributeValue());
        };
    }

    @ConfigurationProperties(prefix = "app.admin")
    record AdminProperties(
            String adminUsername,
            String adminPassword,
            String sessionCookieName,
            String actuatorPassword) {

        String actuatorUsername() {
            return "actuator";
        }
    }
}
