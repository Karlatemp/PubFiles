/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/24 20:52:32
 *
 * MiraiPlugins/MiraiBootstrap/PermissionManager.java
 */

package cn.mcres.karlatemp.mirai.permission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PermissionManager {
    private static final Gson g = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    public static final Map<String, PermissionBase> groups = new ConcurrentHashMap<>();
    public static final Map<Long, PermissionBase> users = new ConcurrentHashMap<>();
    public static final Map<Long, PermissionBase> qq_groups = new ConcurrentHashMap<>();
    public static final Map<Long, PermissionBase> qq_groups_admin = new ConcurrentHashMap<>();
    public static PermissionBase default_;
    public static final File permissions = new File("perm.json");
    public static final ThreadLocal<Permissible> PERMISSIBLE_THREAD_LOCAL = ThreadLocal.withInitial(PermissionBase::new);

    public static class FileDesc {
        public static class GroupDesc {
            public long id;
            public String name, parent;
            public Map<String, Boolean> permissions;
        }

        public List<GroupDesc> groups, users, qq_groups, qq_groups_admin;

        public void to(Map<String, PermissionBase> groups,
                       Map<Long, PermissionBase> users,
                       Map<Long, PermissionBase> qq_groups,
                       Map<Long, PermissionBase> qq_groups_admin) {
            groups.clear();
            users.clear();
            qq_groups.clear();
            qq_groups_admin.clear();
            fix();
            this.groups.forEach(group -> {
                PermissionBase pb = new PermissionBase();
                pb.registerAttach().getPermissions().putAll(group.permissions);
                pb.setName(group.name);
                pb.recalculatePermissions();
                groups.put(group.name, pb);
            });
            this.groups.forEach(group -> {
                if (group.parent != null) {
                    groups.get(group.name).setParent(groups.get(group.parent));
                }
            });
            this.users.forEach(group -> {
                PermissionBase pb = new PermissionBase();
                pb.registerAttach().getPermissions().putAll(group.permissions);
                pb.setName(group.name);
                pb.recalculatePermissions();
                users.put(group.id, pb);
            });
            this.qq_groups.forEach(group -> {
                PermissionBase pb = new PermissionBase();
                pb.registerAttach().getPermissions().putAll(group.permissions);
                pb.setName(group.name);
                pb.recalculatePermissions();
                qq_groups.put(group.id, pb);
            });
            this.qq_groups_admin.forEach(group -> {
                PermissionBase pb = new PermissionBase();
                pb.registerAttach().getPermissions().putAll(group.permissions);
                pb.setName(group.name);
                pb.recalculatePermissions();
                qq_groups_admin.put(group.id, pb);
            });
        }

        private void fix() {
            if (users == null) users = new LinkedList<>();
            if (qq_groups_admin == null) qq_groups_admin = new LinkedList<>();
            if (qq_groups == null) qq_groups = new LinkedList<>();
            if (groups == null) groups = new LinkedList<>();
        }

        public FileDesc from(Map<String, PermissionBase> groups,
                             Map<Long, PermissionBase> users,
                             Map<Long, PermissionBase> qq_groups,
                             Map<Long, PermissionBase> qq_groups_admin) {
            fix();
            this.groups = groups.entrySet().stream().collect(LinkedList::new, (l, e) -> {
                GroupDesc desc = new GroupDesc();
                desc.name = e.getKey();
                desc.permissions = e.getValue().permissions;
                final Permissible parent = e.getValue().getParent();
                if (parent != null) {
                    desc.parent = parent.getName();
                }
                l.add(desc);
            }, Collection::addAll);
            this.users = users.entrySet().stream().collect(LinkedList::new, (l, e) -> {
                GroupDesc desc = new GroupDesc();
                desc.id = e.getKey();
                desc.permissions = e.getValue().permissions;
                desc.name = e.getValue().getName();
                l.add(desc);
            }, Collection::addAll);
            this.qq_groups = qq_groups.entrySet().stream().collect(LinkedList::new, (l, e) -> {
                GroupDesc desc = new GroupDesc();
                desc.id = e.getKey();
                desc.permissions = e.getValue().permissions;
                l.add(desc);
            }, Collection::addAll);
            this.qq_groups_admin = qq_groups_admin.entrySet().stream().collect(LinkedList::new, (l, e) -> {
                GroupDesc desc = new GroupDesc();
                desc.id = e.getKey();
                desc.permissions = e.getValue().permissions;
                l.add(desc);
            }, Collection::addAll);
            return this;
        }
    }

    // qq -> qq_admin_group -> qq_group -> group -> default
    public static Permissible getPermission(long qq, long group, boolean isAdmin) {
        PermissibleLink link = new PermissibleLink();
        final PermissionBase base = users.get(qq);
        link.append(base);
        if (isAdmin)
            link.append(qq_groups_admin.get(group));
        link.append(qq_groups.get(group));
        if (base != null) {
            final String name = base.getName();
            if (name != null)
                link.append(groups.get(name));
        }
        link.append(default_);
        return link;
    }

    public static Permissible getPermission(long qq) {
        PermissibleLink link = new PermissibleLink();
        final PermissionBase base = users.get(qq);
        link.append(base);
        if (base != null) {
            final String name = base.getName();
            if (name != null)
                link.append(groups.get(name));
        }
        link.append(default_);
        return link;
    }

    public static void save() throws IOException {
        try (OutputStream stream = new FileOutputStream(permissions)) {
            try (Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
                g.toJson(new FileDesc().from(
                        groups, users, qq_groups, qq_groups_admin
                ), writer);
            }
        }
    }

    public static void reload() throws IOException {
        if (permissions.isFile())
            try (InputStream stream = new FileInputStream(permissions)) {
                try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    g.fromJson(reader, FileDesc.class).to(
                            groups, users, qq_groups, qq_groups_admin
                    );
                    default_ = groups.get("default");
                }
            }
    }

    static {
        try {
            reload();
        } catch (IOException e) {
            Logger.getLogger("PermissionManager").log(Level.SEVERE, "Failed to load perm in initialize.", e);
        }
    }
}
