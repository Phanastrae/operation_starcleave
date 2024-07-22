package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.world.storage.StorageIoWorker;
import net.minecraft.world.storage.StorageKey;

import java.nio.file.Path;

public class FirmamentStorageIoWorker extends StorageIoWorker {
    // StorageIoWorker's constructor is protected
    public FirmamentStorageIoWorker(StorageKey storageKey, Path directory, boolean dsync) {
        super(storageKey, directory, dsync);
    }
}
