package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.world.storage.StorageIoWorker;

import java.nio.file.Path;

public class FirmamentStorageIoWorker extends StorageIoWorker {
    // StorageIoWorker's constructor is protected
    public FirmamentStorageIoWorker(Path directory, boolean dsync, String name) {
        super(directory, dsync, name);
    }
}
