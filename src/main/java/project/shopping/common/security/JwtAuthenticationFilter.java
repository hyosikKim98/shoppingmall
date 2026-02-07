package project.shopping.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import project.shopping.common.util.TraceIdUtil;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // traceId는 공통으로 세팅(응답/에러에 같이 쓰기 좋음)
        TraceIdUtil.ensureTraceId();

        String token = resolveBearerToken(request);
        if (token != null) {
            AuthPrincipal principal = jwtTokenProvider.parse(token);
            CustomUserDetails userDetails = new CustomUserDetails(
                    principal.userId(),
                    principal.role()
            );

            // 오른쪽 구현체가 명확할때 var 사용 가능 (가독성)
            var auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TraceIdUtil.MDC_KEY);
        }
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String value = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(value)) return null;
        if (!value.startsWith("Bearer ")) return null;
        String token = value.substring(7);
        return StringUtils.hasText(token) ? token : null;
    }
}
