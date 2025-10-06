package xyz.eclipseisoffline.customtimecycle;

import net.minecraft.commands.CommandSourceStack;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.function.Predicate;

@Mod(CustomTimeCycle.MOD_ID)
public class CustomTimeCycleNeoForge extends CustomTimeCycleNeoForgeBase {
    private final PermissionNode<Boolean> commandPermission;

    public CustomTimeCycleNeoForge() {
        initialise();

        NeoForge.EVENT_BUS.addListener(this::registerPermissionNodesEvent);
        NeoForge.EVENT_BUS.addListener(this::registerCommandsEvent);
        commandPermission = new PermissionNode<>(MOD_ID, COMMAND_PERMISSION, PermissionTypes.BOOLEAN,
                (player, playerUUID, context) -> false);
    }

    private void registerPermissionNodesEvent(PermissionGatherEvent.Nodes event) {
        event.addNodes(commandPermission);
    }

    private void registerCommandsEvent(RegisterCommandsEvent event) {
        registerCommand(event.getDispatcher());
    }

    @Override
    public Predicate<CommandSourceStack> checkPermission(String permission, int operatorLevel) {
        return source -> source.hasPermission(operatorLevel)
                || (source.getPlayer() != null && PermissionAPI.getPermission(source.getPlayer(), commandPermission));
    }
}
