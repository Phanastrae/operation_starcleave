package phanastrae.operation_starcleave.world.firmament;

import java.nio.file.Path;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

public class FirmamentStorageIoWorker extends IOWorker {
    // StorageIoWorker's constructor is protected
    public FirmamentStorageIoWorker(RegionStorageInfo storageKey, Path directory, boolean dsync) {
        super(storageKey, directory, dsync);
    }
}
