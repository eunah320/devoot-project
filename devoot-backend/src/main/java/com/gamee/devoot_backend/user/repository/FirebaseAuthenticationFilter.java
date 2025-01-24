package com.gamee.devoot_backend.user.repository;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class  FirebaseAuthenticationFilter extends OncePerRequestFilter {

	private final UserService userService;
	private final FirebaseAuth firebaseAuth;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String authorizationHeader = request.getHeader("Authorization");

			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				String token = authorizationHeader.substring(7);
				FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
				String uid = decodedToken.getUid();

				userService.findUserByUid(uid).ifPresentOrElse(
					user -> {
						CustomUserDetails userDetails = new CustomUserDetails(user);
						UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(authentication);
					},
					() -> {
						// 회원가입이 필요한 상태
						response.setStatus(419); // Custom status code
						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						String jsonResponse = "{\"message\": \"User not registered\"}";
						try {
							response.getOutputStream().write(jsonResponse.getBytes());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				);
			}

			if (!response.isCommitted()) {
				filterChain.doFilter(request, response);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			String jsonResponse = "{\"message\": \"Unauthorized access\"}";
			response.getOutputStream().write(jsonResponse.getBytes());
		}
	}
}
