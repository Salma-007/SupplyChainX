package com.example.supplychainx.aspects;

import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthAspect {

    @Autowired
    private UserRepository userRepository;

    @Around("execution(public * com.example.supplychainx.service_approvisionnement.controller.*.*(..)) || " +
            "execution(public * com.example.supplychainx.service_production.controller.*.*(..)) || "+
            "execution(public * com.example.supplychainx.service_delivery.controller.*.*(..))"
    )
    public Object verifyAuthAndSetRole(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String email = request.getHeader("email");
            String password = request.getHeader("password");

            String determinedRole = authenticateAndDetermineRole(email, password);

            AuthContext.setRole(determinedRole);

            System.out.println("Authentification réussie. Rôle stocké dans AuthContext: " + determinedRole);

            return joinPoint.proceed();

        } catch (SecurityException e) {
            System.err.println("Échec de l'authentification ou de l'autorisation: " + e.getMessage());
            throw e;

        } finally {
            AuthContext.clear();
        }
    }

    private String authenticateAndDetermineRole(String email, String password) throws SecurityException {
        if (email == null || password == null) {
            throw new SecurityException("Authentification requise. Headers email et password manquants.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SecurityException("Email ou mot de passe invalide."));
        if (user.getPassword().equals(password)) {
            return user.getRole().name();
        } else {
            throw new SecurityException("Email ou mot de passe invalide.");
        }
    }
}
