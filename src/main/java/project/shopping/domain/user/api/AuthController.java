package project.shopping.domain.user.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.shopping.common.response.ApiResponse;
import project.shopping.common.util.TraceIdUtil;
import project.shopping.domain.user.dto.LoginRequest;
import project.shopping.domain.user.dto.SignupRequest;
import project.shopping.domain.user.dto.TokenResponse;
import project.shopping.domain.user.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Valid @RequestBody SignupRequest req) {
        TokenResponse res = authService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(res, TraceIdUtil.ensureTraceId()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest req) {
        TokenResponse res = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok(res, TraceIdUtil.ensureTraceId()));
    }
}