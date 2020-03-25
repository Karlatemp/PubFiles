/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/24 20:48:43
 *
 * MiraiPlugins/MiraiBootstrap/Permissible.java
 */

package cn.mcres.karlatemp.mirai.permission;

public interface Permissible {
    default boolean hasPermission(String permission) {
        final Boolean permission0 = hasPermission0(permission);
        if (permission0 == null) return false;
        return permission0;
    }

    Boolean hasPermission0(String permission);

    void recalculatePermissions();

    String getName();

    Permissible setName(String name);
}
