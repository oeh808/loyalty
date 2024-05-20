package io.brightskies.loyalty.config;


import io.brightskies.loyalty.user.Entities.User;
import io.brightskies.loyalty.user.exception.ResourceNotFoundException;
import io.brightskies.loyalty.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String userName;
        final String jwtToken;

        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }
        jwtToken = authHeader.substring(7);
        userName = jwtUtil.extractUsername(jwtToken) ;

        if (userName!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            try {
                User user =  userRepository.findUserByEmail(userName).orElseThrow(()-> new ResourceNotFoundException("cutumear with email [%s] not found".formatted(userName)));
                if (user == null){
                    filterChain.doFilter(request,response);
                    return;
                }
                if(jwtUtil.validateToken(jwtToken, user)){

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, user.mapRolesToAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }catch (Exception ex){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(ex.getMessage());

                return;
            }

        }

        filterChain.doFilter(request,response);
    }
}
