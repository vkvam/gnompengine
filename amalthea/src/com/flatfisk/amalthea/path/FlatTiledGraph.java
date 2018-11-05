
package com.flatfisk.amalthea.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.flatfisk.gnomp.math.MathUtils;

/** A random generated graph representing a flat tiled map.
 * 
 * @author davebaol */
public class FlatTiledGraph implements TiledGraph<FlatTiledNode> {
	public final int sizeX;
	public final int sizeY;

	public final float worldScaleX;
	public final float worldScaleY;

	protected Array<FlatTiledNode> nodes;

	public boolean diagonal;
	public FlatTiledNode startNode;

	public FlatTiledGraph(int sizeX, int sizeY, float worldScaleX, float worldScaleY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.worldScaleX = worldScaleX;
		this.worldScaleY = worldScaleY;
		this.nodes = new Array<FlatTiledNode>(sizeX * sizeY);
		this.diagonal = false;
		this.startNode = null;
	}

	FlatTiledNode iterateSpiral (int x, int y)
	{
		x = Math.min(Math.max(x,0), sizeX-1);
		y = Math.min(Math.max(y,0), sizeY-1);

		int d = 0; // current direction; 0=RIGHT, 1=DOWN, 2=LEFT, 3=UP
		int c = 0; // counter
		int s = 1; // chain size

		// starting point
		//x = ((int)Math.floor(size/2.0))-1;
		//y = ((int)Math.floor(size/2.0))-1;

		for (int k=1; k<=(sizeX-1); k++)
		{
			for (int j=0; j<(k<(sizeY-1)?2:3); j++)
			{
				for (int i=0; i<s; i++)
				{
					try {
						FlatTiledNode node = getNode(x, y);
						if(node.type==1){
							return node;
						}
					}catch (java.lang.IndexOutOfBoundsException e){

					}
					c++;

					switch (d)
					{
						case 0: y = y + 1; break;
						case 1: x = x + 1; break;
						case 2: y = y - 1; break;
						case 3: x = x - 1; break;
					}
				}
				d = (d+1)%4;
			}
			s = s + 1;
		}
		return null;
	}


	public FlatTiledNode getClosestNode(Vector2 worldPosition){
		int x = (int) (worldPosition.x/worldScaleX+this.sizeX/2);
		int y = (int) (worldPosition.y/worldScaleY+this.sizeY/2);


		FlatTiledNode node = iterateSpiral(x,y);

		return node;
	}

	public void init (boolean[][] boolMap) {

		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				nodes.add(new FlatTiledNode(x, y, boolMap[x][y]?1:2, 4));
			}
		}

		// Each node has up to 4 neighbors, therefore no diagonal movement is possible
		for (int x = 0; x < sizeX; x++) {
			int idx = x * sizeY;
			for (int y = 0; y < sizeY; y++) {
				FlatTiledNode n = nodes.get(idx + y);
				if (x > 0) addConnection(n, -1, 0);
				if (y > 0) addConnection(n, 0, -1);
				if (x < sizeX - 1) addConnection(n, 1, 0);
				if (y < sizeY - 1) addConnection(n, 0, 1);
			}
		}
	}

	@Override
	public FlatTiledNode getNode (int x, int y) {
		return nodes.get(x * sizeY + y);
	}

	@Override
	public FlatTiledNode getNode (int index) {
		return nodes.get(index);
	}

	@Override
	public int getIndex (FlatTiledNode node) {
		return node.getIndex(sizeY);
	}

	@Override
	public int getNodeCount () {
		return nodes.size;
	}

	@Override
	public Array<Connection<FlatTiledNode>> getConnections (FlatTiledNode fromNode) {
		return fromNode.getConnections();
	}

	private void addConnection (FlatTiledNode n, int xOffset, int yOffset) {
		FlatTiledNode target = getNode(n.x + xOffset, n.y + yOffset);
		if (target.type == FlatTiledNode.TILE_FLOOR) {
			n.getConnections().add(
					new FlatTiledConnection(this, n, target)
			);
		}
	}

}
