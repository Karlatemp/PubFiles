/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/24 20:35:54
 *
 * MiraiPlugins/MiraiBootstrap/PermissionBase.java
 */

package cn.mcres.karlatemp.mirai.permission;

import org.jetbrains.annotations.NotNull;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PermissionBase implements Permissible {
    protected Permissible parent;
    protected final Deque<PermissionAttach> attaches;
    protected final Map<String, Boolean> permissions;

    public PermissionBase copyPointer() {
        return new PermissionBase(attaches, permissions);
    }

    @NotNull
    public Deque<PermissionAttach> getAttaches() {
        return attaches;
    }

    public Permissible getParent() {
        return parent;
    }

    public PermissionBase setParent(Permissible parent) {
        this.parent = parent;
        return this;
    }

    public PermissionBase(@NotNull Deque<PermissionAttach> attaches,
                          @NotNull Map<String, Boolean> permissions) {
        this.attaches = attaches;
        this.permissions = permissions;
    }

    public PermissionBase() {
        this.attaches = new ConcurrentLinkedDeque<>();
        permissions = new HashMap<>();
    }

    @NotNull
    public PermissionAttach registerAttach() {
        PermissionAttach attach = new PermissionAttach();
        attaches.add(attach);
        return attach;
    }

    public void unregisterAttach(PermissionAttach attach) {
        attaches.remove(attach);
    }

    public void recalculatePermissions() {
        clearPermissions();
        for (PermissionAttach attach : attaches) {
            permissions.putAll(attach.getPermissions());
        }
    }

    public Boolean hasPermission0(String perm) {
        final Boolean status = permissions.get(perm);
        if (status != null) return status;
        if (parent != null) return parent.hasPermission(perm);
        return null;
    }

    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Permissible setName(String name) {
        this.name = name;
        return this;
    }

    public void clearPermissions() {
        permissions.clear();
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode() + "[" + getName() + "]";
    }
}
