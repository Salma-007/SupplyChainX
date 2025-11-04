package com.example.supplychainx.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoleRequired {
    /**
     * Spécifie la liste des rôles autorisés à exécuter cette méthode.
     */
    String[] value();
}