package utils;

import java.awt.*;

public enum PathsToBanks {
    VARROCK_EAST_BANKPATHS(
            new java.awt.Point[]{new java.awt.Point(8544, 2965), new java.awt.Point(8557, 2951), new java.awt.Point(8564, 2935), new java.awt.Point(8562, 2912), new java.awt.Point(8559, 2890), new java.awt.Point(8558, 2864), new java.awt.Point(8557, 2851), new java.awt.Point(8559, 2830), new java.awt.Point(8549, 2805), new java.awt.Point(8540, 2786), new java.awt.Point(8530, 2757), new java.awt.Point(8515, 2754), new java.awt.Point(8501, 2743), new java.awt.Point(8479, 2741), new java.awt.Point(8453, 2742), new java.awt.Point(8428, 2737), new java.awt.Point(8410, 2745)},
            new java.awt.Point[]{new java.awt.Point(8541, 2963), new java.awt.Point(8552, 2954), new java.awt.Point(8555, 2939), new java.awt.Point(8556, 2920), new java.awt.Point(8553, 2896), new java.awt.Point(8551, 2875), new java.awt.Point(8549, 2852), new java.awt.Point(8551, 2834), new java.awt.Point(8549, 2809), new java.awt.Point(8529, 2794), new java.awt.Point(8516, 2780), new java.awt.Point(8505, 2763), new java.awt.Point(8503, 2749), new java.awt.Point(8485, 2739), new java.awt.Point(8468, 2741), new java.awt.Point(8444, 2741), new java.awt.Point(8419, 2735), new java.awt.Point(8408, 2743)},
            new java.awt.Point[]{new java.awt.Point(8553, 2963), new java.awt.Point(8558, 2945), new java.awt.Point(8558, 2936), new java.awt.Point(8561, 2918), new java.awt.Point(8559, 2892), new java.awt.Point(8558, 2866), new java.awt.Point(8558, 2853), new java.awt.Point(8556, 2836), new java.awt.Point(8552, 2813), new java.awt.Point(8541, 2795), new java.awt.Point(8521, 2782), new java.awt.Point(8509, 2765), new java.awt.Point(8505, 2750), new java.awt.Point(8493, 2737), new java.awt.Point(8468, 2738), new java.awt.Point(8429, 2738), new java.awt.Point(8411, 2739), new java.awt.Point(8408, 2766)}
    ),
    VARROCK_WEST_BANKPATHS(
            new java.awt.Point[]{new java.awt.Point(8125, 2953), new java.awt.Point(8122, 2936), new java.awt.Point(8109, 2918), new java.awt.Point(8086, 2879), new java.awt.Point(8079, 2852), new java.awt.Point(8077, 2820), new java.awt.Point(8080, 2786), new java.awt.Point(8082, 2769), new java.awt.Point(8083, 2747), new java.awt.Point(8101, 2739), new java.awt.Point(8122, 2718)},
            new java.awt.Point[]{new java.awt.Point(8124, 2952), new java.awt.Point(8124, 2935), new java.awt.Point(8115, 2923), new java.awt.Point(8104, 2906), new java.awt.Point(8093, 2891), new java.awt.Point(8088, 2869), new java.awt.Point(8079, 2846), new java.awt.Point(8073, 2825), new java.awt.Point(8071, 2809), new java.awt.Point(8075, 2790), new java.awt.Point(8079, 2769), new java.awt.Point(8081, 2756), new java.awt.Point(8094, 2743), new java.awt.Point(8109, 2732), new java.awt.Point(8123, 2727), new java.awt.Point(8125, 2708)},
            new java.awt.Point[]{new java.awt.Point(8119, 2959), new java.awt.Point(8125, 2952), new java.awt.Point(8128, 2940), new java.awt.Point(8122, 2930), new java.awt.Point(8115, 2918), new java.awt.Point(8110, 2900), new java.awt.Point(8105, 2882), new java.awt.Point(8083, 2869), new java.awt.Point(8080, 2856), new java.awt.Point(8079, 2830), new java.awt.Point(8081, 2817), new java.awt.Point(8078, 2791), new java.awt.Point(8079, 2777), new java.awt.Point(8081, 2759), new java.awt.Point(8088, 2743), new java.awt.Point(8106, 2738), new java.awt.Point(8121, 2723)}
    );


    // Enum setup
    private final Point[] Path1;
    private final Point[] Path2;
    private final Point[] Path3;

    PathsToBanks(Point[] Path1, Point[] Path2, Point[] Path3) {
        this.Path1 = Path1;
        this.Path2 = Path2;
        this.Path3 = Path3;
    }

    public Point[] Path1() {
        return Path1;
    }
    public Point[] Path2() {
        return Path2;
    }
    public Point[] Path3() {
        return Path3;
    }
}
