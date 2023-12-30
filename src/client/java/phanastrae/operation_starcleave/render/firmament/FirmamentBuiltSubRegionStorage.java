package phanastrae.operation_starcleave.render.firmament;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.BufferBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FirmamentBuiltSubRegionStorage {

    private static final FirmamentBuiltSubRegionStorage INSTANCE = new FirmamentBuiltSubRegionStorage();

    private FirmamentBuiltSubRegionStorage() {
    }
    public static FirmamentBuiltSubRegionStorage getInstance() {
        return INSTANCE;
    }

    private final Long2ObjectLinkedOpenHashMap<FirmamentBuiltSubRegionHolder> builtSubRegionHolders = new Long2ObjectLinkedOpenHashMap<>();
    public final BufferBuilder bufferBuilder = new BufferBuilder(256);

    @Nullable
    public FirmamentBuiltSubRegionHolder get(long id) {
        return builtSubRegionHolders.get(id);
    }

    public void add(FirmamentBuiltSubRegionHolder firmamentBuiltSubRegionHolder) {
        this.builtSubRegionHolders.put(firmamentBuiltSubRegionHolder.id, firmamentBuiltSubRegionHolder);
    }

    public void remove(long id) {
        FirmamentBuiltSubRegionHolder firmamentBuiltSubRegionHolder = this.builtSubRegionHolders.get(id);
        firmamentBuiltSubRegionHolder.close();
        this.builtSubRegionHolders.remove(id);
    }

    public void forEach(Consumer<FirmamentBuiltSubRegionHolder> consumer) {
        this.builtSubRegionHolders.forEach((id, builtSubRegionHolder) -> consumer.accept(builtSubRegionHolder));
    }

    public void clear() {
        this.builtSubRegionHolders.forEach((id, builtSubRegionHolder) -> {
            builtSubRegionHolder.close();
        });
        this.builtSubRegionHolders.clear();
    }

    public void close() {
        clear();
        this.bufferBuilder.close();
    }
}
