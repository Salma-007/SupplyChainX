package com.example.supplychainx.aspects;

import com.example.supplychainx.annotations.RoleRequired;
import com.example.supplychainx.aspects.AuthContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class SecurityAspect {
    @Around("@annotation(com.example.supplychainx.annotations.RoleRequired)")
    public Object checkRoleAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {

        String currentUserRole = AuthContext.getRole();

        if (currentUserRole == null) {
            throw new SecurityException("Accès refusé. Authentification non trouvée (Rôle manquant).");
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RoleRequired roleRequired = method.getAnnotation(RoleRequired.class);
        List<String> requiredRoles = Arrays.asList(roleRequired.value());

        if (requiredRoles.contains(currentUserRole)) {
            System.out.println(" OK: Rôle " + currentUserRole + " autorisé pour la méthode " + method.getName());
            return joinPoint.proceed();
        } else {
            System.err.println(" REFUSÉ: Rôle " + currentUserRole + " non autorisé. Rôles requis: " + requiredRoles);
            throw new SecurityException("Accès refusé. Rôle insuffisant pour exécuter cette action.");
        }
    }
}
