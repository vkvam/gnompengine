package com.flatfisk.amalthea.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.components.Dock;
import com.flatfisk.gnomp.engine.components.Spatial;

public class DockSystem extends IteratingSystem implements EntityListener {

    private Logger LOG = new Logger(this.getClass().getName(), Logger.DEBUG);

    private ComponentMapper<Dock> dockComponentMapper = ComponentMapper.getFor(Dock.class);
    private ComponentMapper<Spatial.Node> physicsMapper = ComponentMapper.getFor(Spatial.Node.class);


    public DockSystem(int priority) {
        super(Family.all(Dock.class).get(), priority);
    }

    public void processEntity(Entity enemyEntity, float f) {
        Dock dock = dockComponentMapper.get(enemyEntity);

        if (dock.closedDesired)
            if(dock.doorPosition>0)
                dock.doorPosition-=f*2;
            else
                dock.closed = true;
        else
            if(dock.doorPosition<1)
                dock.doorPosition+=f;
            else
                dock.closed = false;

            Spatial.Node left = physicsMapper.get(dockComponentMapper.get(enemyEntity).leftDoor);
            Spatial.Node right = physicsMapper.get(dockComponentMapper.get(enemyEntity).rightDoor);
            left.local.vector.x = dock.doorWidth+2*dock.doorWidth*dock.doorPosition*0.3f;
            left.local.rotation = dock.doorPosition*40;

            right.local.vector.x = -dock.doorWidth-2*dock.doorWidth*dock.doorPosition*0.3f;
            right.local.rotation = -dock.doorPosition*40;




    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
