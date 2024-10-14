package utils;

import helpers.utils.Tile;

public enum Spots {
    EAST1(
            new Tile[]{
                    new Tile(6927, 13616, 0),
                    new Tile(6897, 13605, 0)
            }, //path to bank
            new Tile(6935, 13629, 0), //crab tile
            new Tile[] {
                    new Tile(6946, 13640, 0),
                    new Tile(6953, 13654, 0),
                    new Tile(6957, 13679, 0),
                    new Tile(6959, 13703, 0),
                    new Tile(6960, 13725, 0),
                    new Tile(6956, 13747, 0)
            } // reset path
    ),
    EAST2(
            new Tile[]{
                    new Tile(6990, 13626, 0),
                    new Tile(6965, 13631, 0),
                    new Tile(6940, 13621, 0),
                    new Tile(6920, 13607, 0),
                    new Tile(6890, 13604, 0)
            }, //path to bank
            new Tile(6996, 13626, 0), //crab tile
            new Tile[] {
                    new Tile(6994, 13630, 0),
                    new Tile(6983, 13647, 0),
                    new Tile(6963, 13680, 0),
                    new Tile(6956, 13710, 0),
                    new Tile(6959, 13740, 0),
                    new Tile(6949, 13765, 0)
            } // reset path
    ),
    EAST3(
            new Tile[]{
                    new Tile(7055, 13619, 0),
                    new Tile(7027, 13630, 0),
                    new Tile(6997, 13634, 0),
                    new Tile(6962, 13631, 0),
                    new Tile(6929, 13620, 0),
                    new Tile(6909, 13608, 0),
                    new Tile(6886, 13600, 0)
            }, //path to bank
            new Tile(7059, 13617, 0), //crab tile
            new Tile[] {
                    new Tile(7058, 13626, 0),
                    new Tile(7057, 13651, 0),
                    new Tile(7050, 13687, 0),
                    new Tile(7046, 13710, 0),
                    new Tile(7058, 13739, 0),
                    new Tile(7054, 13764, 0),
                    new Tile(7049, 13786, 0)
            } // reset path
    ),
    EAST4(
            new Tile[]{
                    new Tile(7088, 13631, 0),
                    new Tile(7049, 13643, 0),
                    new Tile(7017, 13651, 0),
                    new Tile(6979, 13658, 0),
                    new Tile(6949, 13658, 0),
                    new Tile(6933, 13633, 0),
                    new Tile(6913, 13614, 0),
                    new Tile(6889, 13602, 0)
            }, //path to bank
            new Tile(7101, 13621, 0), //crab tile
            new Tile[] {
                    new Tile(7115, 13640, 0),
                    new Tile(7128, 13653, 0),
                    new Tile(7154, 13671, 0),
                    new Tile(7165, 13696, 0),
                    new Tile(7166, 13722, 0),
                    new Tile(7168, 13741, 0),
                    new Tile(7164, 13773, 0)
            } // reset path
    ),
    WEST1(
            new Tile[]{
                    new Tile(6784, 13630, 0),
                    new Tile(6813, 13611, 0),
                    new Tile(6834, 13608, 0),
                    new Tile(6859, 13604, 0),
                    new Tile(6887, 13601, 0)
            }, //path to bank
            new Tile(6775, 13633, 0), //crab tile
            new Tile[] {
                    new Tile(6786, 13632, 0),
                    new Tile(6815, 13645, 0),
                    new Tile(6847, 13674, 0),
                    new Tile(6860, 13705, 0),
                    new Tile(6876, 13723, 0),
                    new Tile(6883, 13741, 0)
            } // reset path
    ),
    WEST2(
            new Tile[] {
                    new Tile(6719, 13655, 0),
                    new Tile(6749, 13652, 0),
                    new Tile(6765, 13633, 0),
                    new Tile(6797, 13614, 0),
                    new Tile(6824, 13609, 0),
                    new Tile(6848, 13605, 0),
                    new Tile(6885, 13600, 0)
            }, //path to bank
            new Tile(6699, 13661, 0), //crab tile
            new Tile[] {
                    new Tile(6715, 13653, 0),
                    new Tile(6748, 13652, 0),
                    new Tile(6775, 13654, 0),
                    new Tile(6809, 13667, 0),
                    new Tile(6829, 13681, 0),
                    new Tile(6849, 13697, 0),
                    new Tile(6877, 13725, 0)
            } // reset path
    );
    private final Tile[] path;
    private final Tile spot;
    private final Tile[] resetPath;

    Spots(Tile[] path, Tile spot, Tile[] resetPath) {
        this.path = path;
        this.spot = spot;
        this.resetPath = resetPath;
    }

    public Tile[] getPathToBank() {
        return path;
    }

    public Tile getSpotTile() {
        return spot;
    }

    public Tile[] getResetPath() {
        return resetPath;
    }

}
