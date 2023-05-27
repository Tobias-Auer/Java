package de.tobias.permissionsystem.listeners;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttachmentManager {
    private Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public PermissionAttachment addAttachment(Player player, Plugin plugin) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        UUID playerUUID = player.getUniqueId();
        removeAttachment(player); // Entferne vorhandenes Attachment, falls vorhanden
        attachments.put(playerUUID, attachment);
        return attachment;
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
}
