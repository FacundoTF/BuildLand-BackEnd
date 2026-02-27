package com.uai.BuildLand.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFromCookieFilter jwtFromCookieFilter) throws Exception {
        return http
                .addFilterBefore(jwtFromCookieFilter, BasicAuthenticationFilter.class)
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/token").permitAll();
                    auth.requestMatchers("/api/auth/restorePassword").permitAll();
                    auth.requestMatchers("/api/auth/verifyPasswordCode").permitAll();
                    auth.requestMatchers("/api/auth/changePassword").permitAll();
                    auth.requestMatchers("api/auth/logout").authenticated();
                    auth.requestMatchers("/api/categorias").hasAnyAuthority("Z-Pr", "A-Di", "P-X");
                    auth.requestMatchers(HttpMethod.GET, "/api/distribuidoras/{cuit}").hasAnyAuthority("EMPLEADO", "ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/distribuidoras").hasAnyAuthority("A-Di", "ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH, "/api/distribuidoras").hasAnyAuthority("M-Di", "ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/distribuidoras/{cuit}").hasAnyAuthority("R-Di", "ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/personas-fisicas").hasAnyAuthority("A-Pf", "ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH, "/api/personas-fisicas").hasAnyAuthority("M-Pf", "ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/personas-fisicas/{cuit}").hasAnyAuthority("R-Pf", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/personas-fisicas/{cuit}").hasAnyAuthority("ADMIN", "EMPLEADO");
                    auth.requestMatchers(HttpMethod.GET, "/api/productos/enlistar").hasAnyAuthority("PERSONA FISICA", "Z-Pr");
                    auth.requestMatchers(HttpMethod.GET, "/api/productos/categorias/{nombreCategoria}").hasAnyAuthority("PERSONA FISICA", "Z-Pr", "DISTRIBUIDORAS");
                    auth.requestMatchers(HttpMethod.GET, "/api/marcas/categorias/{nombreCategoria}").hasAnyAuthority("PERSONA FISICA", "Z-Pr", "DISTRIBUIDORAS");
                    auth.requestMatchers(HttpMethod.POST, "/api/presupuestos").hasAuthority("PERSONA FISICA");
                    auth.requestMatchers(HttpMethod.GET, "/api/presupuestos/enlistar").hasAnyAuthority("Y-Pr", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/presupuestos/detalles/**").hasAnyAuthority("PERSONA FISICA", "DISTRIBUIDORAS", "Y-Pr");
                    auth.requestMatchers(HttpMethod.GET, "/api/presupuestos/verificarFechaLimite/{id}").hasAnyAuthority("PERSONA FISICA", "Y-Pr", "ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/presupuestos/rechazar/{id}").hasAnyAuthority("DISTRIBUIDORAS");
                    auth.requestMatchers(HttpMethod.POST, "/api/usuarios").hasAnyAuthority("A-Us", "ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH, "/api/usuarios").hasAnyAuthority("M-Us", "ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/usuarios").hasAnyAuthority("R-Us", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/usuarios").hasAnyAuthority("PERSONA FISICA", "DISTRIBUIDORAS", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/usuarios/cuit/{cuit}").hasAnyAuthority("PERSONA FISICA", "DISTRIBUIDORAS", "ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/usuarios").hasAnyAuthority("A-Us", "ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH, "/api/usuarios").hasAnyAuthority("M-Us", "ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/usuarios/{cuit}").hasAnyAuthority("R-Us", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/usuarios/email").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/usuarios/email/{email}").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH, "/api/usuarios/resetear").hasAnyAuthority("ADMIN", "Z-Us");
                    auth.requestMatchers(HttpMethod.GET, "/api/usuarios/login").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/grupos").hasAnyAuthority("A-Gr", "M-Gr", "R-Gr", "ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/grupos").hasAnyAuthority("A-Gr", "ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH, "/api/grupos").hasAnyAuthority("M-Gr", "ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/grupos/{id}").hasAnyAuthority("R-Gr", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/grupos/enlistar").hasAnyAuthority("M-Gr", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/permisos/enlistar").hasAnyAuthority("M-Gr", "ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/reporte/cotizaciones-ganadas-perdidas").hasAnyAuthority("DISTRIBUIDORAS", "C-X");
                    auth.requestMatchers(HttpMethod.POST, "/api/reporte/cotizaciones-rechazadas-mensajes").hasAnyAuthority("DISTRIBUIDORAS", "C-X");
                    auth.requestMatchers(HttpMethod.POST, "/api/reporte/cotizaciones-rechazadas-mensajes-general").hasAnyAuthority("DISTRIBUIDORAS", "C-X");
                    auth.requestMatchers(HttpMethod.POST, "/api/reporte/cotizaciones-rechazadas-mensajes-general").hasAnyAuthority("DISTRIBUIDORAS", "C-X");
                    auth.requestMatchers(HttpMethod.POST, "/api/reporte/productos-solicitados").hasAnyAuthority("DISTRIBUIDORAS", "P-X");
                    auth.requestMatchers(HttpMethod.POST, "/api/reporte/productos-solicitados/producto").hasAnyAuthority("DISTRIBUIDORAS", "P-X");
                    auth.requestMatchers(HttpMethod.POST, "/api/reporte/productos-solicitados/marca").hasAnyAuthority("DISTRIBUIDORAS", "P-X");
                    auth.requestMatchers(HttpMethod.GET, "/api/reporte/trazabilidad-login-logout/{cuit}").hasAnyAuthority("ADMIN", "T-X");
                    auth.requestMatchers(HttpMethod.GET, "/api/reporte/trazabilidad-persona-fisica/{cuit}").hasAnyAuthority("ADMIN", "A-X");
                    auth.requestMatchers(HttpMethod.POST, "/api/cotizaciones/aceptar/{idCotizacion}").hasAuthority("PERSONA FISICA");
                    auth.requestMatchers(HttpMethod.POST, "/api/cotizaciones/rechazar/{idCotizacion}").hasAuthority("PERSONA FISICA");
                    auth.requestMatchers(HttpMethod.GET, "/api/cotizaciones/mensajes-rechazo").hasAuthority("PERSONA FISICA");
                    auth.requestMatchers(HttpMethod.GET, "/api/cotizaciones/enlistar/{IdPresupuesto}").hasAnyAuthority("PERSONA FISICA", "Y-Pr", "ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/cotizaciones/{idPresupuesto}").hasAuthority("DISTRIBUIDORAS");
                    auth.requestMatchers(HttpMethod.GET, "/api/categorias/por-distribuidora").hasAnyAuthority("Z-Pr", "DISTRIBUIDORAS");
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(withDefaults());
                })
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
//        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        var authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}