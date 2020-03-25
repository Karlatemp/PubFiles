/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/24 20:36:19
 *
 * MiraiPlugins/MiraiBootstrap/PermissionAttach.java
 */

package cn.mcres.karlatemp.mirai.permission;

import java.util.HashMap;

public class PermissionAttach {
    private final HashMap<String, Boolean> permissions;

    public PermissionAttach() {
        this.permissions = new HashMap<>();
    }

    public HashMap<String, Boolean> getPermissions() {
        return permissions;
    }

    public PermissionAttach setPermission(String perm, boolean status) {
        permissions.put(perm, status);
        return this;
    }
}
