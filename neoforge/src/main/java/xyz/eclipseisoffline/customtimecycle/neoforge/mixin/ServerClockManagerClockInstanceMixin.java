package xyz.eclipseisoffline.customtimecycle.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.eclipseisoffline.customtimecycle.clock.ClockInstanceUtil;

// Implemented in common module, this has a patch for NeoForge because it is special
@Mixin(targets = "net.minecraft.world.clock.ServerClockManager$ClockInstance")
public abstract class ServerClockManagerClockInstanceMixin implements ClockInstanceUtil {

    @ModifyExpressionValue(method = "Lnet/minecraft/world/clock/ServerClockManager$ClockInstance;tick(Z)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/clock/ServerClockManager$ClockInstance;rate:F", opcode = Opcodes.GETFIELD))
    public float multiplyRateByManagedValue(float original) {
        return original * customTimeCycle$getRateMultiplier();
    }
}
