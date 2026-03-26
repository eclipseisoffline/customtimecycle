package xyz.eclipseisoffline.customtimecycle.filefix;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.filefix.access.CompressedNbt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class TimeManagerNbt extends CompressedNbt {
    private final int targetVersion;

    public TimeManagerNbt(Path path, int targetVersion) {
        super(path, MissingSeverity.NEUTRAL);
        this.targetVersion = targetVersion;
    }

    @Override
    public Optional<Dynamic<Tag>> read() throws IOException {
        return readFile().map(tag -> tag.get("data").orElseEmptyMap());
    }

    @Override
    public <T> void write(Dynamic<T> data) {
        Dynamic<T> dataTag = data.emptyMap().set("data", data);
        Dynamic<T> wrappedAndWithDataVersion = NbtUtils.addDataVersion(dataTag, targetVersion);
        writeFile(wrappedAndWithDataVersion);
    }
}
