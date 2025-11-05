package in.vikramaditya.MudrikaVyavastha.security;

import in.vikramaditya.MudrikaVyavastha.utils.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.springframework.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JWTUtil jwtUtil;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/v1.0/register",
            "/api/v1.0/login",
            "/api/v1.0/activate",
            "/api/v1.0/status",
            "/api/v1.0/health"
    );


    @Override
    protected void doFilterInternal( HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        if (EXCLUDED_PATHS.contains(path)) {
            System.out.println("⏩ Skipping JWT for: " + path);
            filterChain.doFilter(request, response);
            System.out.println("➡️ Path: " + request.getServletPath());
            return;
        }


        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        if(authHeader!=null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            email = jwtUtil.extractEmail(jwt);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            if(jwtUtil.validateToken(jwt, email)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
