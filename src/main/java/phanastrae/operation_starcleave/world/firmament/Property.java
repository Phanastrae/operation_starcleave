package phanastrae.operation_starcleave.world.firmament;

public class Property {

    public Property(int xSize, int zSize, float defaultValue) {
        this.xSize = xSize;
        this.zSize = zSize;
        this.defaultValue = defaultValue;
        this.property = new float[xSize][zSize];
        this.dProperty = new float[xSize][zSize];
        this.fill(defaultValue);
    }

    int xSize;
    int zSize;
    float defaultValue;

    float[][] property;
    float[][] dProperty;

    public void reset() {
        this.fill(defaultValue);
    }

    public void fill(float value) {
        for(int x = 0; x < xSize; x++) {
            for(int z = 0; z < zSize; z++) {
                property[x][z] = value;
                dProperty[x][z] = 0;
            }
        }
    }

    public void clamp(float min, float max) {
        for(int x = 0; x < xSize; x++) {
            for(int z = 0; z < zSize; z++) {
                clamp(x, z, min, max);
            }
        }
    }

    public void clamp(int x, int z, float min, float max) {
        if(x < 0 || z < 0 || x >= xSize || z >= zSize) return;

        float f = property[x][z];
        if(f < min) f = min;
        if(f > max) f = max;
        property[x][z] = f;
    }

    public void update() {
        for(int x = 0; x < xSize; x++) {
            for(int z = 0; z < zSize; z++) {
                property[x][z] += dProperty[x][z];
                dProperty[x][z] = 0;
            }
        }
    }

    public void multiply(float value) {
        for(int x = 0; x < xSize; x++) {
            for(int z = 0; z < zSize; z++) {
                property[x][z] *= value;
            }
        }
    }

    public float get(int x, int z) {
        if(x < 0 || z < 0 || x >= xSize || z >= zSize) {
            //return this.defaultValue;
            x = Math.floorMod(x, this.xSize);
            z = Math.floorMod(z, this.zSize);
        }

        return property[x][z];
    }

    public void set(int x, int z, float value) {
        if(x < 0 || z < 0 || x >= xSize || z >= zSize) return;

        property[x][z] = value;
    }

    public void add(int x, int z, float value) {
        if(x < 0 || z < 0 || x >= xSize || z >= zSize) return;

        dProperty[x][z] += value;
    }

    public void addNow(int x, int z, float value) {
        if(x < 0 || z < 0 || x >= xSize || z >= zSize) return;

        property[x][z] += value;
    }

    public float d2Fdx2(int x, int z) {
        return (get(x+1, z) + get(x-1, z) - 2 * get(x, z));
    }

    public float d2Fdz2(int x, int z) {
        return (get(x, z+1) + get(x, z-1) - 2 * get(x, z));
    }

    public float laplacian(int x, int z) {
        return (get(x+1, z) + get(x-1, z) + get(x, z+1) + get(x, z-1) - 4 * get(x, z));
    }

    public float dFdx(int x, int z) {
        return (get(x+1, z) - get(x-1, z)) / 2;
    }

    public float dFdz(int x, int z) {
        return (get(x, z+1) - get(x, z-1)) / 2;
    }

    public float dFdDim(int x, int z, int dim) {
        if(dim == 0) {
            return dFdx(x, z);
        } else if(dim == 2) {
            return dFdz(x, z);
        } else {
            return 0;
        }
    }

    public float gradFsqr(int x, int z) {
        float dFdx = dFdx(x, z);
        float dFdz = dFdz(x, z);
        return dFdx * dFdx + dFdz * dFdz;
    }
}
