package pt.teus.backend.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.teus.backend.service.UserInfoService;

import java.io.IOException;
import java.text.MessageFormat;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userEmail = null;

        logger.info("JWT Token: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userEmail = jwtService.extractUsername(token);
            logger.info(MessageFormat.format("Token: {0}", token));
        }
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userInfoService.loadUserByUsername(userEmail);
            logger.info(MessageFormat.format("User: {0}", userDetails.getUsername()));
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                logger.info(MessageFormat.format("Authenticated user: {0}", userDetails.getUsername()));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info(MessageFormat.format("User: {0}", userDetails.getUsername()));
            }
        }
        logger.info(MessageFormat.format("User: {0}", userEmail));
        logger.info("request is: " + request.getRequestURI());
        logger.info("response is: " + response.getStatus());

        filterChain.doFilter(request, response);
    }
}
