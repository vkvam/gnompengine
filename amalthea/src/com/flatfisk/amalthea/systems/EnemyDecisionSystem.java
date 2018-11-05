package com.flatfisk.amalthea.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.flatfisk.amalthea.components.*;
import com.flatfisk.amalthea.factories.WeaponBuilder;
import com.flatfisk.amalthea.factories.procedural.IslandGenerator;
import com.flatfisk.amalthea.message.HooverMessage;
import com.flatfisk.amalthea.path.*;
import com.flatfisk.gnomp.PhysicsConstants;
import com.flatfisk.gnomp.engine.GnompEngine;
import com.flatfisk.gnomp.engine.components.PhysicsBody;
import com.flatfisk.gnomp.engine.components.PhysicsSteerable;
import com.flatfisk.gnomp.engine.components.SoundComponent;
import com.flatfisk.gnomp.engine.components.Spatial;
import com.flatfisk.gnomp.math.Transform;

import java.util.Random;

public class EnemyDecisionSystem extends IteratingSystem implements EntityListener {

    private Logger LOG = new Logger(this.getClass().getName(), Logger.DEBUG);

    public ComponentMapper<Enemy> enemyMapper = ComponentMapper.getFor(Enemy.class);
    private ComponentMapper<HooverMessage> eventComponentMapper = ComponentMapper.getFor(HooverMessage.class);
    private ComponentMapper<DockVisitor> dockVisitorComponentMapper = ComponentMapper.getFor(DockVisitor.class);
    private ComponentMapper<Dock> dockComponentMapper = ComponentMapper.getFor(Dock.class);
    private final ComponentMapper<SoundComponent> soundComponentMapper = ComponentMapper.getFor(SoundComponent.class);

    public Family docksFamily = Family.all(HasDocks.class).get();
    FlatTiledGraph tiledGraph;


    public EnemyDecisionSystem(int priority) {
        super(Family.all(Enemy.class, PhysicsBody.Container.class, PhysicsSteerable.Container.class).get(), priority);
    }

    public void initPathFinding(IslandGenerator.WayPoints wayPoints) {
        boolean[][] booleanMap = wayPoints.booleanMap;
        int width = wayPoints.width;
        int height = wayPoints.height;
        float waypointScale = wayPoints.scale;

        tiledGraph = new FlatTiledGraph(width, height, waypointScale, waypointScale);
        tiledGraph.diagonal = true;
        tiledGraph.init(booleanMap);


    }

    private Array<Vector2> getPath(Vector2 start, Vector2 stop) {

        FlatTiledNode a = tiledGraph.getClosestNode(start);
        FlatTiledNode b = tiledGraph.getClosestNode(stop);
        tiledGraph.startNode = a;


        TiledSmoothableGraphPath<FlatTiledNode> path = new TiledSmoothableGraphPath<FlatTiledNode>();
        IndexedAStarPathFinder<FlatTiledNode> c = new IndexedAStarPathFinder<FlatTiledNode>(tiledGraph);
        c.searchNodePath(a, b, new ManhattanDistance<FlatTiledNode>(), path);

        PathSmoother pathSmoother = new PathSmoother<FlatTiledNode, Vector2>(new TiledRaycastCollisionDetector<FlatTiledNode>(tiledGraph));
        pathSmoother.smoothPath(path);


        // TODO: Use recycling with pool !!!!!
        Array<Vector2> path2 = new Array<Vector2>(path.nodes.size);
        for (FlatTiledNode n : path.nodes) {

            Vector2 v = com.flatfisk.gnomp.utils.Pools.obtainVector().set(
                    n.x * tiledGraph.worldScaleX - tiledGraph.worldScaleX * tiledGraph.sizeX / 2, n.y * tiledGraph.worldScaleY - tiledGraph.worldScaleY * tiledGraph.sizeY / 2
            );
            path2.add(new Vector2(v.x, v.y));
        }
        return path2;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        Family hooverEventFamily = Family.all(HooverMessage.class).get();
        engine.addEntityListener(hooverEventFamily, priority, this);
    }

    float c = 0;

    public void update(float f) {
        super.update(f);
        c += f;
        /*
        if (c > 1.25f) {
            GnompEngine world = (GnompEngine) getEngine();

            Transform t = getEngine().getEntitiesFor(player).get(0).getComponent(Spatial.Node.class).world;
            Entity e = TestFramebuffer.createEnemy(world, new Transform(t.vector.x + (Math.random() > 0.5 ? (200 + (float) Math.random() * 1000) : (-200 - (float) Math.random() * 1000)), -190, 0));
            world.constructEntity(e);

            c = 0;
        }
        */
    }


    protected Entity createBullet(Transform translation, Vector2 direction, Vector2 speed) {
        GnompEngine world = (GnompEngine) getEngine();
        Entity e = WeaponBuilder.enemyBullet(world, translation, direction, speed);
        world.constructEntity(e);
        return e;
    }

    private Random rand = new Random();
    private Transform temp = new Transform();

    private void removeEntityFromDock(DockVisitor dockVisitor, Entity entity){
        if(dockVisitor.dockToVisit != null) {
            Gdx.app.debug(getClass().getName(), "Current before:"+dockComponentMapper.get(dockVisitor.dockToVisit).visitorEntities.size);
            Array<Entity> visitorEntities = dockComponentMapper.get(dockVisitor.dockToVisit).visitorEntities;
            if (visitorEntities.contains(entity, true)) {
                visitorEntities.removeValue(entity, true);
            }
            Gdx.app.debug(getClass().getName(), "Current after:"+dockComponentMapper.get(dockVisitor.dockToVisit).visitorEntities.size);
        }
        dockVisitor.reset();
    }

    private void removeEntityFromPreviousDock(DockVisitor dockVisitor, Entity entity){
        if(dockVisitor.previousVisitedDock != null) {
            Gdx.app.debug(getClass().getName(), "Previous before:"+dockComponentMapper.get(dockVisitor.previousVisitedDock).visitorEntities.size);
            Array<Entity> visitorEntities = dockComponentMapper.get(dockVisitor.previousVisitedDock).visitorEntities;
            if (visitorEntities.contains(entity, true)) {
                visitorEntities.removeValue(entity, true);
            }
            Gdx.app.debug(getClass().getName(), "Previous after:"+dockComponentMapper.get(dockVisitor.previousVisitedDock).visitorEntities.size);
            dockVisitor.previousVisitedDock = null;
        }
    }

    public void processEntity(Entity enemyEntity, float f) {
        Enemy enemy = enemyMapper.get(enemyEntity);

        DockVisitor dockVisitor = dockVisitorComponentMapper.get(enemyEntity);


        if(enemy.movingStatus == Enemy.MovingStatus.AT_TARGET){
            if((enemy.keepStatusFor -= f) > 0){
                return;
            }else {
                Dock dock = dockComponentMapper.get(dockVisitor.dockToVisit);
                dock.closedDesired = false;

                enemy.approachingLastTarget = false;
                dockVisitor.previousVisitedDock = dockVisitor.dockToVisit;
                //dockVisitor.dockToVisit = null;
                dockVisitor.approachingDock = false;

                enemy.movingStatus = Enemy.MovingStatus.IDLE;
                enemyEntity.getComponent(PhysicsSteerable.Container.class).setActiveSteering("Arrive");
            }
        }

        if(dockVisitor.approachingDock){
            dockVisitor.timeUsedToVisitDock+=f;
            if(dockVisitor.timeUsedToVisitDock>dockVisitor.timeUsedToVisitDockLimit){
                // We failed to dock, just set a new target
                enemy.movingStatus = Enemy.MovingStatus.IDLE;
                Gdx.app.log(getClass().getName(), "Used too much time");
                removeEntityFromDock(dockVisitor, enemyEntity);
                removeEntityFromPreviousDock(dockVisitor, enemyEntity);

                dockVisitor.approachingDock = false;
                enemy.targetReachedLimit = 4;
                enemy.approachingLastTarget = false;
                enemy.movingStatus = Enemy.MovingStatus.IDLE;

            }
        }

        if (enemy.movingStatus == Enemy.MovingStatus.IDLE) {
            if ((enemy.keepStatusFor -= f) > 0) {
                // TODO: Complete unnecessary
                // e.getComponent(PhysicsBody.Container.class).body.setLinearVelocity(Vector2.Zero.cpy());
                return;
            }

            GnompEngine world = (GnompEngine) getEngine();
            // Get a tempTarget
            ImmutableArray<Entity> entities = world.getEntitiesFor(docksFamily);

            Vector2 enemyPosition = enemyEntity.getComponent(Spatial.Node.class).world.vector.cpy();

            Vector2 lastTarget = enemy.tempTarget;
            while (enemy.tempTarget == null || enemy.tempTarget == lastTarget) {
                Entity target = entities.get(rand.nextInt(entities.size()));

                // TO get out of dock!
                Array<Vector2> outOfDock = new Array<Vector2>();
                if(enemy.pathToTarget != null){
                    for(int i=1;i<Math.min(3, enemy.pathToTarget.size); i++){
                        outOfDock.add(enemy.pathToTarget.get(enemy.pathToTarget.size - i).cpy());
                    }
                }

                Gdx.app.debug(getClass().getName(), "Outofdock length:"+outOfDock.size);

                for (Entity e2 : target.getComponent(Spatial.Node.class).children) {
                    if (e2.getComponent(Dock.class) != null) {
                        Gdx.app.debug(getClass().getName(), "Set new target");

                        dockVisitor.dockToVisit = e2;
                        dockVisitor.approachingDock = false;
                        dockVisitor.timeUsedToVisitDock = 0;
                        enemy.targetReachedLimit = 3f;

                        Transform finalTarget = e2.getComponent(Spatial.Node.class).world.cpy();
                        Transform stepTarget = finalTarget.cpy().add(new Vector2(0, -160).rotate(e2.getComponent(Spatial.Node.class).world.rotation), 0);
                        Transform stepTarget2 = finalTarget.cpy().add(new Vector2(0, -110).rotate(e2.getComponent(Spatial.Node.class).world.rotation), 0);

                        enemy.pathToTarget = getPath(enemyPosition, stepTarget.vector);

                        for (Vector2 v : enemy.pathToTarget) {
                            v.scl(PhysicsConstants.METERS_PER_PIXEL);
                        }
                        if (enemy.pathToTarget.size < 1) {
                            // TODO: This needs to be resolved
                            break;
                        }
                        // Add steps to get out of current dock (if any)
                        for(Vector2 v: outOfDock){
                            enemy.pathToTarget.insert(0, v);
                        }

                        // Add the step target to the list
                        enemy.pathToTarget.add(stepTarget.toBox2D().vector);
                        enemy.pathToTarget.add(stepTarget2.toBox2D().vector);

                        // Add the final target (inside dock)
                        enemy.target = finalTarget.toBox2D().vector;
                        enemy.tempTarget = enemy.pathToTarget.get(0);
                        enemy.pathIndex = 0;

                        enemyEntity.getComponent(PhysicsSteerable.Container.class).target.set(enemy.tempTarget);
                        enemyEntity.getComponent(PhysicsSteerable.Container.class).setActiveSteering("Arrive");
                        enemy.movingStatus = Enemy.MovingStatus.APPROACHING_TARGET;
                        break;
                    }
                }
            }

        }


        //Vector2 enemyPos = physicsBody.getPosition();
        // TODO: Probably not a good replacement.
        Vector2 enemyPos = temp.set(enemyEntity.getComponent(Spatial.Node.class).world).toBox2D().vector;

        Vector2 lookAt = enemy.tempTarget.cpy().sub(enemyPos);
        if (lookAt.len() < enemy.targetReachedLimit) {
            //
            //
            enemy.pathIndex += 1;

            if (!enemy.approachingLastTarget) {


                // We are out of docks
                if (enemy.pathIndex == 3) {
                    if(dockVisitor.previousVisitedDock!=null) {
                        dockComponentMapper.get(dockVisitor.previousVisitedDock).closedDesired = true;
                        removeEntityFromPreviousDock(dockVisitor, enemyEntity);
                    }
                    enemy.targetReachedLimit = 4;
                }

                if (enemy.pathIndex == enemy.pathToTarget.size - 2) {
                    // If there is capacity, add this entity to the dock
                    Dock dock = dockComponentMapper.get(dockVisitor.dockToVisit);

                    if(dock.visitorEntities.size<dock.visitor_capacity) {
                        dock.closedDesired = false;
                        dockVisitor.approachingDock = true;
                        Gdx.app.debug(getClass().getName(), "Added entity as visitor");
                        dock.visitorEntities.add(enemyEntity);
                    }else{
                        Gdx.app.debug(getClass().getName(), "No visitor capacity");
                        // Circle back a bit to avoid congestion
                        enemy.pathIndex-=2;
                        return;
                    }


                    Gdx.app.debug(getClass().getName(), "Approaching final path segment (right outside dock)");
                    enemy.targetReachedLimit = 0.7f;
                    enemyEntity.getComponent(PhysicsSteerable.Container.class).setActiveSteering("Arrive2");
                    enemy.tempTarget.set(enemy.pathToTarget.get(enemy.pathIndex));
                    enemyEntity.getComponent(PhysicsSteerable.Container.class).target.set(enemy.tempTarget);
                    return;
                }
                else if (enemy.pathIndex == enemy.pathToTarget.size) {
                    Gdx.app.debug(getClass().getName(), "Approaching final path (inside dock)");
                    enemy.targetReachedLimit = 0.5f;
                    enemy.approachingLastTarget = true;
                    enemy.tempTarget.set(enemy.target);
                    enemyEntity.getComponent(PhysicsSteerable.Container.class).target.set(enemy.target);
                    enemyEntity.getComponent(PhysicsSteerable.Container.class).setActiveSteering("Arrive3");

                    enemy.movingStatus = Enemy.MovingStatus.APPROACHING_TARGET;
                    return;
                }

                enemy.tempTarget.set(enemy.pathToTarget.get(enemy.pathIndex));
                enemy.movingStatus = Enemy.MovingStatus.APPROACHING_TARGET;
                enemyEntity.getComponent(PhysicsSteerable.Container.class).target.set(enemy.tempTarget);

                Gdx.app.debug(getClass().getName(), "Reached" + enemy.pathIndex + " path tempTarget");
            } else {
                Dock dock = dockComponentMapper.get(dockVisitor.dockToVisit);
                dock.closedDesired = true;
                enemy.approachingLastTarget = false;
                enemy.movingStatus = Enemy.MovingStatus.AT_TARGET;
                enemy.keepStatusFor = 6;
                Gdx.app.debug(getClass().getName(), "Approached final target");
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        HooverMessage event = eventComponentMapper.get(entity);
        if (event.isPressed) {
            getEngine().removeEntity(event.entityHoovered);
        } else {
            event.entityHoovered.getComponent(PhysicsBody.Container.class).body.applyLinearImpulse(0, 1f, 0, 0, true);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
