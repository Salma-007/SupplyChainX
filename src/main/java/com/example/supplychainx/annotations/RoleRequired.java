package com.example.supplychainx.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RoleRequired {
    // les roles dédiés pour la methode ou pour le controlleur
    String[] value();
}