package utils;

import helpers.utils.Tile;

public enum Spots {
    EAST1(
            new Tile[]{
                    new Tile(771, 864),
                    new Tile(758, 869)
            }, //path to bank
            new Tile(774, 862), //crab tile
            new Tile[] {
                    new Tile(776, 860),
                    new Tile(778, 856),
                    new Tile(780, 853),
                    new Tile(782, 848),
                    new Tile(782, 845),
                    new Tile(784, 840),
                    new Tile(784, 836),
                    new Tile(784, 830)
            } // reset path
    ),
    EAST2(
            new Tile[]{
                    new Tile(792, 860),
                    new Tile(786, 860),
                    new Tile(781, 860),
                    new Tile(774, 864),
                    new Tile(768, 866),
                    new Tile(757, 868)
            },
            new Tile(796, 863),
            new Tile[] {
                    new Tile(796, 860),
                    new Tile(796, 855),
                    new Tile(795, 851),
                    new Tile(793, 844),
                    new Tile(788, 840),
                    new Tile(783, 835),
                    new Tile(784, 830)
            }
    ),
    EAST3(
            new Tile[]{
                    new Tile(814, 862),
                    new Tile(806, 860),
                    new Tile(796, 859),
                    new Tile(785, 858),
                    new Tile(778, 862),
                    new Tile(770, 866),
                    new Tile(758, 869)
            },
            new Tile(818, 863),
            new Tile[] {
                    new Tile(817, 857),
                    new Tile(818, 850),
                    new Tile(816, 844),
                    new Tile(815, 839),
                    new Tile(814, 835),
                    new Tile(811, 829)
            }
    ),
    EAST4(
            new Tile[]{
                    new Tile(828, 862),
                    new Tile(818, 860),
                    new Tile(808, 858),
                    new Tile(795, 858),
                    new Tile(784, 859),
                    new Tile(775, 863),
                    new Tile(768, 867),
                    new Tile(757, 869)
            },
            new Tile(832, 863),
            new Tile[] {
                    new Tile(836, 862),
                    new Tile(839, 857),
                    new Tile(843, 852),
                    new Tile(849, 848),
                    new Tile(852, 845),
                    new Tile(855, 838),
                    new Tile(855, 831)
            }
    ),
    WEST1(
            new Tile[]{
                    new Tile(727, 862),
                    new Tile(732, 866),
                    new Tile(742, 866),
                    new Tile(751, 868)
            },
            new Tile(724, 859),
            new Tile[] {
                    new Tile(728, 857),
                    new Tile(732, 854),
                    new Tile(738, 850),
                    new Tile(742, 847),
                    new Tile(751, 844),
                    new Tile(754, 839),
                    new Tile(756, 833)
            }
    ),
    WEST2(
            new Tile[] {
                    new Tile(701, 852),
                    new Tile(709, 852),
                    new Tile(716, 856),
                    new Tile(723, 860),
                    new Tile(732, 863),
                    new Tile(739, 866),
                    new Tile(747, 869),
                    new Tile(754, 870)
            },
            new Tile(698, 850),
            new Tile[] {
                    new Tile(702, 850),
                    new Tile(710, 847),
                    new Tile(719, 844),
                    new Tile(729, 843),
                    new Tile(738, 842),
                    new Tile(745, 839)
            }
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
