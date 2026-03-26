package xyz.eclipseisoffline.customtimecycle.filefix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.filefix.FileFix;
import net.minecraft.util.filefix.access.FileAccess;
import net.minecraft.util.filefix.access.FileRelation;
import net.minecraft.util.filefix.access.FileResourceType;
import net.minecraft.util.worldupdate.UpgradeProgress;
import xyz.eclipseisoffline.filefixutils.api.FileFixHelpers;

import java.util.List;
import java.util.Optional;

public class TimeManagerFileFix extends FileFix {
    private static final FileResourceType<TimeManagerNbt> TIME_MANAGER_DATA_TYPE = new FileResourceType<>(TimeManagerNbt::new);
    private static final List<String> DAY_TIME_MARKERS = List.of("customtimecycle:sunrise", "minecraft:day", "minecraft:noon");
    private static final List<String> NIGHT_TIME_MARKERS = List.of("customtimecycle:sunset", "minecraft:night", "minecraft:midnight");
    private static final float DAY_TIME = 12000.0F;
    private static final float NIGHT_TIME = 12000.0F;

    public TimeManagerFileFix(Schema schema) {
        super(schema);
    }

    @Override
    public void makeFixer() {
        addFileContentFix(files -> {
            FileAccess<TimeManagerNbt> timeManagerNbtFiles = files.getFileAccess(TIME_MANAGER_DATA_TYPE, FileRelation.DATA.forFile("timemanager.dat"));
            return progress -> {
                progress.setType(UpgradeProgress.Type.FILES);

                // TODO other dimensions
                TimeManagerNbt managerFile = timeManagerNbtFiles.getOnlyFile();
                Optional<Dynamic<Tag>> optionalNbt = managerFile.read();
                if (optionalNbt.isPresent()) {
                    Dynamic<Tag> nbt = optionalNbt.get();
                    long dayTime = nbt.get("daytime").asLong((long) DAY_TIME);
                    long nightTime = nbt.get("nighttime").asLong((long) NIGHT_TIME);
                    float dayRate = DAY_TIME / dayTime;
                    float nightRate = NIGHT_TIME / nightTime;

                    Dynamic<Tag> overworldRateMap = nbt.emptyMap();
                    if (dayRate != 1.0F) {
                        for (String marker : DAY_TIME_MARKERS) {
                            overworldRateMap = overworldRateMap.set(marker, nbt.createFloat(dayRate));
                        }
                    }
                    if (nightRate != 1.0F) {
                        for (String marker : NIGHT_TIME_MARKERS) {
                            overworldRateMap = overworldRateMap.set(marker, nbt.createFloat(nightRate));
                        }
                    }

                    Dynamic<Tag> clockMap = nbt.emptyMap();
                    clockMap = clockMap.set("minecraft:overworld", overworldRateMap);
                    managerFile.write(clockMap);
                }
            };
        });
        addFileFixOperation(FileFixHelpers.createGlobalDataMoveOperation("timemanager", Identifier.fromNamespaceAndPath("customtimecycle", "clock_rates")));
    }
}
