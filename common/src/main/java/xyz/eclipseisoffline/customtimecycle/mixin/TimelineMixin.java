package xyz.eclipseisoffline.customtimecycle.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.customtimecycle.clock.CustomTimeCycleClockMarkers;

import java.util.HashMap;
import java.util.Map;

@Mixin(Timeline.class)
public abstract class TimelineMixin {
    @Unique
    private static final Timeline TIMELINE_WITH_SUN_MARKERS = Timeline.builder(null)
            .addTimeMarker(CustomTimeCycleClockMarkers.SUNRISE, 0, true)
            .addTimeMarker(CustomTimeCycleClockMarkers.SUNSET, 12000, true)
            .build();

    @Mutable
    @Shadow
    @Final
    private Map<ResourceKey<ClockTimeMarker>, ?> timeMarkers;

    @Inject(method = "lambda$validateRegistry$0", at = @At("TAIL"))
    private static void addCustomTimeMarkers(Multimap<Holder<WorldClock>, ResourceKey<ClockTimeMarker>> timeMarkersByClock,
                                            Map<ResourceKey<?>, Exception> loadingErrors, Holder.Reference<Timeline> timeline,
                                            CallbackInfo callbackInfo) {
        if (timeline.is(Timelines.OVERWORLD_DAY) && timeline.value().clock().is(WorldClocks.OVERWORLD) && timeline.value().periodTicks().map(i -> i == 24000).orElse(false)) {
            // Add sunrise and sunset markers
            // This is a bit cursed because I don't want to deal with access wideners
            Map<ResourceKey<ClockTimeMarker>, ?> newMarkers = new HashMap<>(((TimelineMixin) (Object) timeline.value()).timeMarkers);
            //noinspection unchecked,rawtypes
            newMarkers.putAll((Map) ((TimelineMixin) (Object) TIMELINE_WITH_SUN_MARKERS).timeMarkers);
            ((TimelineMixin) (Object) timeline.value()).timeMarkers = newMarkers;
        }
    }
}
