/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/24 21:40:48
 *
 * MiraiPlugins/MiraiBootstrap/PermissibleLink.java
 */

package cn.mcres.karlatemp.mirai.permission;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

public class PermissibleLink implements Permissible {
    protected final Collection<Permissible> ps;
    protected String name;

    public Collection<Permissible> getPs() {
        return ps;
    }

    public PermissibleLink() {
        this(new LinkedList<>());
    }

    public PermissibleLink(@NotNull Collection<Permissible> permissible) {
        this.ps = permissible;
    }

    @Override
    public Boolean hasPermission0(String permission) {
        for (Permissible p : ps) {
            final Boolean permission0 = p.hasPermission0(permission);
            if (permission0 != null) return permission0;
        }
        return null;
    }

    public void append(Permissible p) {
        if (p != null) ps.add(p);
    }

    @Override
    public void recalculatePermissions() {
        for (Permissible p : ps) p.recalculatePermissions();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Permissible setName(String name) {
        this.name = name;
        return this;
    }
}
