package com.github.heisdanielade.pamietampsa.filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.heisdanielade.pamietampsa.response.BaseApiResponse;
import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomRateLimitingFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, Bandwidth> RATE_LIMITS = Map.of(
            "/v1/auth/login", Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))),
            "/v1/auth/signup", Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(1))),
            "/v1/auth/resend-verification-email", Bandwidth.classic(2, Refill.greedy(2, Duration.ofMinutes(1))),

            "/v1/user/edit", Bandwidth.classic(2, Refill.greedy(2, Duration.ofDays(7)))
    );
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();


    private Bucket resolveBucket(String ip, String path) {
        String key = ip + '+' + path;
        Bandwidth limit = RATE_LIMITS.getOrDefault(path,
                Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))));
        return cache.computeIfAbsent(key, k -> Bucket.builder().addLimit(limit).build());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("\n==== [RateLimitingFilter] Executing filter\n"); // Debug log

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String ip = httpReq.getRemoteAddr();
        String path = httpReq.getRequestURI();
        Bucket bucket = resolveBucket(ip, path);

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            System.out.println("\n==== [RateLimitingFilter] (Limit exceeded):\n" +
                               "\t== IP: " + ip +
                               "\n\t== PATH: " + path + "\n");

            httpResp.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResp.setContentType("application/json");

            BaseApiResponse<Object> errorResponse = new BaseApiResponse<>(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Too many requests. Try again later."
            );


            String json = objectMapper.writeValueAsString(errorResponse);
            httpResp.getWriter().write(json);
        }
    }
}

