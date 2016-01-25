package net.vnguyen.Model;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Test on 12/9/2015.
 */
public class CarShape
{

    private static final int TRIANGLE_COUNT = 3;
    private static final int MAX_VERTEX = 8;

    public static final float WheelMaxRadius = 0.5f;
    public static final float WheelMinRadius = 0.2f;

    public static final float WheelMaxDensity = 50;
    public static final float WheelMinDensity= 10;

    public static final float ChassisMaxDensity = 100f;
    public static final float ChassisMinDensity = 10f;

    public static final float ChassisMaxAxis = 1.1f;
    public static final float ChassisMinAxis = 0.1f;

    public static final float MotorSpeed = 20f;

    public float chassisDensity;
    public int wheelCount = 2;
    public float[] wheelRadius;
    public float[] wheelDensity;
    public int[] wheelVertex;
    public Vec2[] vertexList;

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    World world;


    public CarShape(World world){
        this.world = world;
        vertexList = new Vec2[MAX_VERTEX];
        wheelVertex = new int[wheelCount];
        wheelRadius = new float[wheelCount];
        wheelDensity = new float[wheelCount];
        generate();
    }

    public CarShape(Vec2[] childVertexList, float childChassisDensity, int childWheelCount, int[] childWheelVertex, float[] childWheelRadius, float[] childWheelDensity) {
        vertexList = childVertexList;
        wheelVertex = childWheelVertex;
        wheelRadius = childWheelRadius;
        wheelDensity = childWheelDensity;
        wheelCount = childWheelCount;
        chassisDensity = childChassisDensity;

    }


    public void generate(){
        /*
            PICK A RANDOM VALUE FOR DENSITY AND RADIUS OF THE WHEELS
         */
        for (int i = 0; i < wheelCount; i++) {
            wheelRadius[i] = (float) (Math.random()*WheelMaxRadius+WheelMinRadius);
            wheelDensity[i] = (float) Math.random()*WheelMaxDensity + WheelMinDensity;
        }
        chassisDensity = (float)Math.random()*ChassisMaxDensity + ChassisMinDensity;

        vertexList[0] = new Vec2( (float)Math.random()*ChassisMaxAxis + ChassisMinAxis, 0);
        vertexList[1]=new Vec2((float) Math.random()*ChassisMaxAxis + ChassisMinAxis, (float) Math.random()*ChassisMaxAxis + ChassisMinAxis);
        vertexList[2]=new Vec2(0,(float)Math.random()*ChassisMaxAxis + ChassisMinAxis);
        vertexList[3]=new Vec2(-(float)Math.random()*ChassisMaxAxis - ChassisMinAxis,(float)Math.random()*ChassisMaxAxis + ChassisMinAxis);
        vertexList[4]=new Vec2(-(float)Math.random()*ChassisMaxAxis - ChassisMinAxis,0);
        vertexList[5]=new Vec2((float)-Math.random()*ChassisMaxAxis - ChassisMinAxis,(float)-Math.random()*ChassisMaxAxis - ChassisMinAxis);
        vertexList[6]=new Vec2(0,(float)-Math.random()*ChassisMaxAxis - ChassisMinAxis);
        vertexList[7]=new Vec2((float)Math.random()*ChassisMaxAxis + ChassisMinAxis,(float)-Math.random()*ChassisMaxAxis - ChassisMinAxis);

        /*
        ** Determine which axel to put the wheels on
         */
        ArrayList<Integer> randomVertex = new ArrayList<Integer>();
        for (int i = 0; i < 8; i++) {
            randomVertex.add(i);
        }

        for (int i = 0; i < wheelCount; i++) {
            int wheelIdx = (int) Math.floor(Math.random() * randomVertex.size());
            wheelVertex[i] = randomVertex.get(wheelIdx);
            randomVertex.remove(wheelIdx);
        }
    }

    public void createChassisPart(Body body, Vec2 vertex1,Vec2 vertex2, float density){
        Vec2[] vertexList = new Vec2[]{
            vertex1,
            vertex2,
            new Vec2(0,0)
        };
        FixtureDef fixDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(vertexList, TRIANGLE_COUNT);
        fixDef.density = density;
        fixDef.friction = 10;
        fixDef.restitution = 0.2f;
        fixDef.filter.groupIndex = -1;
        fixDef.shape = shape;
        body.createFixture(fixDef);
    }

    public Body createChassis(Vec2[] vertexList, float density){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(0.0f, 1f);
        Body body = world.createBody(bodyDef);
        // Creating the triangles from 8 vectors
        for (int i = 0; i < MAX_VERTEX; i++) {
            createChassisPart(body, vertexList[i], vertexList[ i+1==MAX_VERTEX ? 0:(i+1)], density);
        }

        return body;
    }



}
