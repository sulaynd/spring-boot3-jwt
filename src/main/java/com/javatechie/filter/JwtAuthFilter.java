package com.javatechie.filter;


import com.javatechie.service.UserInfoUserDetailsService;
import com.javatechie.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserInfoUserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthFilter(JwtService jwtService, UserInfoUserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            //return;
//            //throw new MalformedJwtException("Invalid JWT token");
//
//        }
        try {
            //get JWT from http request
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);

        } catch (SignatureException | ExpiredJwtException | MalformedJwtException | UnsupportedJwtException |
                 IllegalArgumentException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

//        catch (SignatureException ex){
//            //response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//           // response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT signature");
//            throw  new SignatureException("Invalid JWT signature");
//
//        }
//        catch (MalformedJwtException ex){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT token");
//
//        }
//        catch (ExpiredJwtException ex){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expired JWT token");
//
//        }
//        catch (UnsupportedJwtException ex){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported JWT token");
//
//        }
//        catch (IllegalArgumentException ex){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT claims string is empty");
//
//        }
    }
}
