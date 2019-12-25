package com.opengg.core.security;

import com.opengg.core.script.ScriptClassLoader;

import java.security.*;

public class OpenGGSecurityPolicy extends Policy {

    @Override
    public PermissionCollection getPermissions(ProtectionDomain domain) {
        if (isPlugin(domain)) {
            return scriptPermissions();
        } else {
            return enginePermissions();
        }
    }

    private boolean isPlugin(ProtectionDomain domain) {
        return domain.getClassLoader() instanceof ScriptClassLoader;
    }

    private PermissionCollection scriptPermissions() {
        Permissions permissions = new Permissions(); // No permissions
        return permissions;
    }

    private PermissionCollection enginePermissions() {
        Permissions permissions = new Permissions();
        permissions.add(new AllPermission());
        return permissions;
    }

}