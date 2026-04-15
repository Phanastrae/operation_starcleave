package phanastrae.operation_starcleave.world.firmament.storage;

import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

import java.nio.file.Path;

public class FirmamentStorageIoWorker extends IOWorker {
    // StorageIoWorker's constructor is protected
    public FirmamentStorageIoWorker(RegionStorageInfo storageKey, Path directory, boolean dsync) {
        super(storageKey, directory, dsync);
    }
}
