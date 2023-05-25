package de.tobias.permissionsystem.listeners;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttachmentManager {
    private static final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public static void addPermission(Player player, String permission) {
        PermissionAttachment pperms = attachments.get(player.getUniqueId());
        pperms.setPermission(permission, true);
        player.recalculatePermissions();
    }
    public static void removePermission(Player player, String permission) {
        attachments.get(player.getUniqueId()).unsetPermission(permission);

        player.recalculatePermissions();
    }

    public PermissionAttachment getAttachment(UUID uuid) {
        return attachments.get(uuid);
    }


    public void removeAttachment(Player player) {
        UUID playerUUID = player.getUniqueId();
        PermissionAttachment attachment = attachments.remove(playerUUID);
        if (attachment != null) {
            player.removeAttachment(attachment);
            player.recalculatePermissions();
        }
    }

    public void init(Player player, PermissionAttachment attachment) {
        attachments.put(player.getUniqueId(), attachment);
    }
}
