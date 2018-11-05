package com.flatfisk.amalthea.factories.procedural;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.flatfisk.amalthea.path.FlatTiledGraph;

public class IslandPopulator {


    public static DockGenerator.DockPositions getDockPositions(IslandGenerator.PolyPoint p, float width, float height){
        if (p.pos.x-Math.floor(p.pos.x)<0.5) {
            return null;
        }

        // 1. Get largest straight
        // 2. Find how many docks theres is room for
        // 3. Find direction out (guess, if always CCW or CW, we'll easily now)
        // 4. Build docks out from straight, with an inside astroid and outside astroid part.
        // 5. Make docks

        DockGenerator.LongestStraight longestStraight = DockGenerator.findLargestStraight(p.poly.getRenderPolygon());
        DockGenerator.DockPositions dps = DockGenerator.getDockPositions(longestStraight, width, height);

        return dps;

    }



}
