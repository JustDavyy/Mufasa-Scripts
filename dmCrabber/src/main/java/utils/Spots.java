package utils;

import helpers.utils.Tile;

public enum Spots {
    EAST1(
            new Tile[]{
                    new Tile(771, 864),
                    new Tile(758, 869)
            }, //path to bank
            new Tile(774, 862) //crab tile
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
            new Tile(796, 863)
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
            new Tile(818, 863)
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
            new Tile(832, 863)
    ),
    WEST1(
            new Tile[]{
                    new Tile(727, 862),
                    new Tile(732, 866),
                    new Tile(742, 866),
                    new Tile(751, 868)
            },
            new Tile(724, 859)
    ),
    WEST2(
            new Tile[]{
                    new Tile(713, 849),
                    new Tile(716, 853),
                    new Tile(722, 858),
                    new Tile(730, 861),
                    new Tile(739, 864),
                    new Tile(746, 867),
                    new Tile(755, 868)
            },
            new Tile(710, 848)
    );
    private final Tile[] path;
    private final Tile spot;

    Spots(Tile[] path, Tile spot) {
        this.path = path;
        this.spot = spot;
    }

    public Tile[] getPathFromBank() {
        return path;
    }

    public Tile getSpotTile() {
        return spot;
    }
}
