package com.badlogic.ashley.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.gnomp.ConstructorManager;
import com.flatfisk.gnomp.components.abstracts.AbstractNode;

import java.util.Iterator;


/**
 * Created by Vemund Kvam on 16/01/16.
 */
public class GnompEngine extends PooledEngine {

    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ConstructorManager constructorManager;
    protected Array<Entity> entitiesToConstruct = new Array<Entity>();
    protected Array<Entity> entitiesConstructed = new Array<Entity>();

    public GnompEngine () {
        this(10, 100, 10, 100);
    }

    public GnompEngine (int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize) {
        super(entityPoolInitialSize,entityPoolMaxSize,componentPoolInitialSize,componentPoolMaxSize);
        constructorManager  = new ConstructorManager(this);
    }


    public ConstructorManager getConstructorManager() {
        return constructorManager;
    }


    public void addEntity(Entity entity){
        super.addEntity(entity);
    }

    /**
     * Constructs or reconstructs the entity or the nearest descendant entity with one or more constructors.
     * @param entity
     */
    public void constructEntity(Entity entity){
        if(constructorManager.rootFamily.matches(entity)) {
            constructorManager.constructEntity(entity);
        }else{
            entitiesToConstruct.add(entity);
        }
    }

    public void removeEntity(Entity entity){
        removeFromParents(entity);
        super.removeEntity(entity);
    }

    public <T extends Component> T addComponent (Class<T> componentType, Entity entity) {
        T component = createComponent(componentType);
        if( component instanceof AbstractNode){
            ((AbstractNode) component).setOwner(entity);
        }
        entity.add(component);
        return component;
    }

    public void update(float f){
        super.update(f);
        reconstructEntities();
    }

    private void removeFromParents(Entity entity){
        for(Component c : entity.getComponents()){
            if(c instanceof AbstractNode){
                AbstractNode node = (AbstractNode) c;
                Entity parent = node.parent;
                if(parent!=null){
                    removeChildren(parent,entity,node.getClass());
                }
            }
        }
    }

    private void removeChildren(Entity parent, Entity child, Class<? extends AbstractNode> nodeType){
        if(parent!=null) {

            AbstractNode node = parent.getComponent(nodeType);
            node.removeChild(child);

            super.removeEntity(child);

            AbstractNode childNode = child.getComponent(nodeType);
            if(childNode!=null && childNode.children!=null) {
                Entity[] children = childNode.children.toArray(Entity.class);

                for (Entity entity : children) {
                    removeChildren(child, entity,nodeType);
                }
            }
        }
    }

    private void reconstructEntities(){
        if(entitiesToConstruct.size>0) {
            Iterator<Entity> entitiesAddedIterator = entitiesToConstruct.iterator();
            while (entitiesAddedIterator.hasNext()) {
                Entity entity = entitiesAddedIterator.next();
                Entity constructor = constructorManager.getConstructor(entity,true);

                if(!entitiesConstructed.contains(constructor,false)){
                    constructorManager.dismantleEntity(constructor);
                    constructorManager.constructEntity(constructor);
                    entitiesConstructed.add(constructor);
                }

                entitiesAddedIterator.remove();
            }
            entitiesConstructed.clear();
        }
    }


}
