package com.api.app.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("URI solicitada: " + uri);

        // Swagger
        if (uri.matches(".*/usuario/login.*") ||
                uri.matches(".*/usuario/cadastrar.*") ||
                uri.contains("/swagger-ui") ||
                uri.contains("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Rotas públicas
        if (uri.startsWith("/usuario/login") || uri.startsWith("/usuario/cadastrar")) {
            System.out.println("Rota pública, ignorando token.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extrai token do header
        String bearer = request.getHeader("Authorization");
        String token = (bearer != null && bearer.startsWith("Bearer "))
                ? bearer.substring(7)
                : null;
        System.out.println("Token recebido: " + token);

        try {
            if (token != null && jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                System.out.println("Usuário: " + username + " | Role: " + role);

                // Monta o Authentication para o Spring Security
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                // Seta no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("Authentication setado no SecurityContext");
            }
        } catch (JwtException ex) {
            // Token inválido ou expirado
            System.out.println("JWTException: " + ex.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido ou expirado");
            return;
        }

        // Prossegue para o controller (e para o AuthorizationFilter do Spring Security)
        filterChain.doFilter(request, response);
    }
}
