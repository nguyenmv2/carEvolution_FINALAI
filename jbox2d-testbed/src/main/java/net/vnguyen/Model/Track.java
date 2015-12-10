package net.vnguyen.Model;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Created by Test on 12/9/2015.
 */
public class Track {
    private static final int MAX_TRACK_TILES = 200 ;
    private World world;
    private Vec2 pos;
    private float angle;
    private Vec2[] coords;
    private static final float GroundHeight = 0.15f;
    private static final float GroundWidth = 1.5f;
    public Body track;
    public Body[] trackTiles;

    public Track(World world){
        this.world = world;
        this.trackTiles = initTrack();
    }

    public Track(World world, Vec2 pos, float angle ){
        this.pos = pos;
        this.angle = angle;
        this.world = world;
        coords = new Vec2[4];
        this.track = generateTrack(pos, angle);
    }

    public Body[] initTrack(){
        Vec2 tilePos = new Vec2(-5f, 0);
        Body[] trackTiles = new Body[MAX_TRACK_TILES];
        for (int i = 0; i < MAX_TRACK_TILES; i++) {
            Body trackTile = generateTrack(tilePos, (float) ((Math.random()*3 -1.5)*1.5*i/MAX_TRACK_TILES));
            trackTiles[i] = trackTile;
            Fixture prevFixture = trackTile.getFixtureList();
            PolygonShape prevShape = (PolygonShape)prevFixture.getShape();
            Vec2 prevCoords = trackTile.getWorldPoint(prevShape.m_vertices[0]);
            tilePos = prevCoords;
        }
        return trackTiles;
    }
    private Body generateTrack(Vec2 pos, float angle){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.x, pos.y);
        Body body = world.createBody(bodyDef);

        coords = new Vec2[]{
                new Vec2(0,0),
                new Vec2(0, -GroundHeight),
                new Vec2(GroundWidth, -GroundHeight),
                new Vec2(GroundWidth, 0)
        };
        Vec2 center = new Vec2(0,0);
        Vec2[] newCoords = rotateTrack(coords, center, angle);
//        Vec2[] newCoords = coords;
        FixtureDef fixDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(newCoords, newCoords.length);
        fixDef.shape = shape;
        fixDef.friction = 0.5f;
        body.createFixture(fixDef);
        return body;
    }

    private Vec2[] rotateTrack(Vec2[] coords, Vec2 center, float angle){
        Vec2[] newCoords = new Vec2[coords.length];
        for (int i = 0; i < coords.length  ; i++) {
            Vec2 vec = new Vec2();
            vec.x = (float) ( Math.cos(angle)*(coords[i].x - center.x) - Math.sin(angle)*(coords[i].y - center.y) + center.x);
            vec.y =(float)( Math.sin(angle)*(coords[i].x - center.x) + Math.cos(angle)*(coords[i].y - center.y) + center.y);
            newCoords[i] = vec;
        }
        return newCoords;
    }
}
