package com.badlogic.ashley.core;

import com.badlogic.gdx.utils.*;
import com.flatfisk.gnomp.ConstructorManager;
import com.flatfisk.gnomp.components.Node;
import com.flatfisk.gnomp.components.relatives.SpatialRelative;
import com.flatfisk.gnomp.constructors.Constructor;

import java.util.Iterator;


/**
 * Created by Vemund Kvam on 16/01/16.
 */
public class GnompEngine extends Engine {
    private Logger LOG = new Logger(this.getClass().getName(),Logger.DEBUG);
    private ConstructorManager constructorManager;
    public Array<EntityConstructor> reconstructList;
    public Array<GnompEntity> removeEntities = new Array<GnompEntity>();
    private static long nextEntityId = 0;

    private GnompEntityPool gnompEntityPool;
    private ComponentPools componentPools;
    private Array<Class<? extends Constructor>> classes;

    /**
     * Creates a new PooledEngine with a maximum of 100 entities and 100 components of each type. Use
     * {@link #GnompEngine(int, int, int, int)} to configure the entity and component pools.
     */
    public GnompEngine () {
        this(10, 100, 10, 100);
    }

    /**
     * Creates new PooledEngine with the specified pools size configurations.
     * @param entityPoolInitialSize initial number of pre-allocated entities.
     * @param entityPoolMaxSize maximum number of pooled entities.
     * @param componentPoolInitialSize initial size for each component type pool.
     * @param componentPoolMaxSize maximum size for each component type pool.
     */
    public GnompEngine (int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize) {
        super();
        reconstructList = new Array<EntityConstructor>(true,2);
        gnompEntityPool = new GnompEntityPool(entityPoolInitialSize, entityPoolMaxSize);
        componentPools = new ComponentPools(componentPoolInitialSize, componentPoolMaxSize);
        constructorManager  = new ConstructorManager(this);
    }


    public ConstructorManager getConstructorManager() {
        return constructorManager;
    }

    public GnompEntity getEntity(Long id){

        for(Entity entity:getEntities()){
            GnompEntity gnompEntity = ((GnompEntity) entity);
            if(id == gnompEntity.id){
                return gnompEntity;
            }
        }
        return null;
    }

    public void addEntity(Entity entity){
        constructorManager.addEntity(entity);
        super.addEntity(entity);
    }

    public void removeEntity(Entity entity){
        removeEntities.add((GnompEntity) entity);
    }

    private void removeChildren(GnompEngine.GnompEntity parent, GnompEngine.GnompEntity child){
        if(parent!=null) {

            for(Component c:parent.getComponents()){
                if(c instanceof Node){
                    ((Node) c).removeChild(child,this);
                }
            }

            super.removeEntity(child);

            SpatialRelative childSpatial = child.getComponent(SpatialRelative.class);
            if(childSpatial!=null && childSpatial.children!=null) {
                Node.EntityWrapper[] children = childSpatial.children.toArray(Node.EntityWrapper.class);

                for (Node.EntityWrapper entityWrapper : children) {
                    GnompEngine.GnompEntity entity = entityWrapper.getEntity(this);
                    removeChildren(child, entity);
                }
            }
        }
    }


    public void update(float f){
        super.update(f);
        Iterator<GnompEntity> entityIterator = removeEntities.iterator();
        while(entityIterator.hasNext()) {
            GnompEntity entity = entityIterator.next();

            GnompEntity entityParent = entity.getComponent(SpatialRelative.class).parent.getEntity(this);

            // Removes all constructed components.
            GnompEntity constructor = constructorManager.removeEntity(entity);
            entityIterator.remove();

            // Remove all children of removed entity.
            removeChildren(entityParent,entity);
            super.removeEntity(entity);

            constructorManager.reconstructEntity(constructor);
        }
    }


    public <T extends Component> T addComponent (Class<T> componentType, Entity entity) {
        T component = createComponent(componentType);
        if( component instanceof Node){
            ((Node) component).setOwner(entity);
        }
        entity.add(component);
        return component;
    }





    /** @return Clean {@link Entity} from the Engine pool. In order to add it to the {@link com.badlogic.ashley.core.Engine}, use {@link #addEntity(Entity)}. */
    public GnompEntity createEntity () {
        return gnompEntityPool.obtain();
    }

    /**
     * Retrieves a new {@link Component} from the {@link com.badlogic.ashley.core.Engine} pool. It will be placed back in the pool whenever it's removed
     * from an {@link Entity} or the {@link Entity} itself it's removed.
     */
    public <T extends Component> T createComponent (Class<T> componentType) {
        return componentPools.obtain(componentType);
    }

    /**
     * Removes all free entities and components from their pools. Although this will likely result in garbage collection, it will
     * free up memory.
     */
    public void clearPools () {
        gnompEntityPool.clear();
        componentPools.clear();
    }

    @Override
    protected void removeEntityInternal (Entity entity) {
        super.removeEntityInternal(entity);

        if (entity instanceof GnompEntity) {
            gnompEntityPool.free((GnompEntity)entity);
        }
    }

    public class GnompEntity extends Entity implements Pool.Poolable {
        public long id = 0;

        @Override
        public Component remove (Class<? extends Component> componentClass) {
            Component component = super.remove(componentClass);

            if (component != null) {
                componentPools.free(component);
            }

            return component;
        }

        @Override
        public void reset () {
            removeAll();
            flags = 0;
            componentAdded.removeAllListeners();
            componentRemoved.removeAllListeners();
            scheduledForRemoval = false;
        }
    }

    private class GnompEntityPool extends Pool<GnompEntity> {

        public GnompEntityPool(int initialSize, int maxSize) {
            super(initialSize, maxSize);
        }

        @Override
        public GnompEntity obtain () {
            GnompEntity gnompEntity = super.obtain();
            gnompEntity.id = nextEntityId++;
            return gnompEntity;
        }

        @Override
        protected GnompEntity newObject () {
            GnompEntity entity = new GnompEntity();
            entity.id = nextEntityId++;
            return new GnompEntity();
        }
    }

    public static class EntityConstructor{
        public EntityConstructor(GnompEntity entity, Array<Class<? extends Constructor>> classes) {
            this.entity = entity;
            this.classes = classes;
        }

        GnompEntity entity;
        Array<Class<? extends Constructor>> classes;
    }

    private class ComponentPools {
        private ObjectMap<Class<?>, ReflectionPool> pools;
        private int initialSize;
        private int maxSize;

        public ComponentPools (int initialSize, int maxSize) {
            this.pools = new ObjectMap<Class<?>, ReflectionPool>();
            this.initialSize = initialSize;
            this.maxSize = maxSize;
        }

        public <T> T obtain (Class<T> type) {
            ReflectionPool pool = pools.get(type);

            if (pool == null) {
                pool = new ReflectionPool(type, initialSize, maxSize);
                pools.put(type, pool);
            }

            return (T)pool.obtain();
        }

        public void free (Object object) {
            if (object == null) {
                throw new IllegalArgumentException("object cannot be null.");
            }

            ReflectionPool pool = pools.get(object.getClass());

            if (pool == null) {
                return; // Ignore freeing an object that was never retained.
            }

            pool.free(object);
        }

        public void freeAll (Array objects) {
            if (objects == null) throw new IllegalArgumentException("objects cannot be null.");

            for (int i = 0, n = objects.size; i < n; i++) {
                Object object = objects.get(i);
                if (object == null) continue;
                free(object);
            }
        }

        public void clear () {
            for (Pool pool : pools.values()) {
                pool.clear();
            }
        }
    }

}
