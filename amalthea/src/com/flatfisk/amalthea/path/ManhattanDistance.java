package com.flatfisk.amalthea.path;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class ManhattanDistance<N extends TiledNode<N>> implements Heuristic<N> {

    public ManhattanDistance () {
    }

    @Override
    public float estimate (N node, N endNode) {
        return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
    }
}
