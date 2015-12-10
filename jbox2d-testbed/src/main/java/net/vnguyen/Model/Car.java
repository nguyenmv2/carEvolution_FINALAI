package net.vnguyen.Model;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.*;

import javax.sound.midi.Soundbank;

/**
 * Created by Test on 12/9/2015.
 */
public class Car {
    private static final float MAX_HEALTH = 200;

    private Body chassis;
    public CarShape carShape;
    public Body[] wheels;
    public float carMass;
    public float[] torque;
    public World world;

    public float maxX = 0, maxY = 0, maxPos= 0 ;
    public float health = MAX_HEALTH;
    public int stepCount = 0;
    public boolean isAlive = true;
    public float score;

    public Car(CarShape carShape) {

        generateCar(carShape);
    }



    public void generateCar(CarShape carShape) {
        this.carShape = carShape;
        this.world = carShape.world;
        this.chassis = carShape.createChassis(carShape.vertexList, carShape.chassisDensity);
        this.wheels = new Body[carShape.wheelCount];
        this.torque = new float[carShape.wheelCount];
        initCar();
    }

    public void initCar() {
        /*
        ** Making wheels
         */
        for (int i = 0; i < carShape.wheelCount; i++) {

            this.wheels[i] = createWheel(carShape.wheelRadius[i], carShape.wheelDensity[i], chassis.getWorldPoint(carShape.vertexList[carShape.wheelVertex[i]]));
        }
        calculateCarMass();
        calculateTorque();
        createJoint();

    }

    public void calculateCarMass() {
        carMass = chassis.getMass();
        for (int i = 0; i < carShape.wheelCount; i++) {
            carMass += wheels[i].getMass();
        }
    }

    public void calculateTorque() {
        for (int i = 0; i < carShape.wheelCount; i++) {
            torque[i] = carMass * (-world.getGravity().y) / carShape.wheelRadius[i];
        }
    }

    public void createJoint() {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        for (int i = 0; i < carShape.wheelCount; i++) {
            jointDef.bodyA = this.chassis;
            jointDef.bodyB = this.wheels[i];
            jointDef.collideConnected = false;
            Vec2 wheelPlacement = carShape.vertexList[carShape.wheelVertex[i]];
            jointDef.localAnchorA.set((wheelPlacement).x, (wheelPlacement).y);
            jointDef.maxMotorTorque = torque[i];
            jointDef.motorSpeed = -carShape.MotorSpeed;
            jointDef.enableMotor = true;
            RevoluteJoint joint = (RevoluteJoint) world.createJoint(jointDef);
        }
    }

    public Vec2 getPosition() {
        return this.chassis.getPosition();
    }

    public Body createWheel(float radius, float density, Vec2 axelPos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(axelPos);
        Body body = world.createBody(bodyDef);
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = new CircleShape();
        fixDef.shape.m_radius = radius;
        fixDef.friction = 1;
        fixDef.restitution = 0.2f;
        fixDef.filter.groupIndex = -1;
        fixDef.density = density;

        body.createFixture(fixDef);
        return body;
    }

    public boolean isDead() {

        Vec2 curPos = this.getPosition();
        if (curPos.y > maxY){
            maxY = curPos.y;
        }
        if (curPos.x > maxPos +0.02f){
            this.health = MAX_HEALTH;
            this.maxPos = curPos.x;
        } else {
            if (curPos.x > this.maxPos){
                maxPos = curPos.x;
            }
//            if (Math.abs(this.chassis.getLinearVelocity().x) < 0.0001){
//                this.health -= 5;
//            }
            this.health --;
            if (this.health <= 0){

                return true;
            }
        }
        return false;
    }

    public float kill(){

        float speed =(float) (this.maxPos / this.stepCount) * 1/60;
        float pos = maxPos;
        this.score = pos + speed;
        world.destroyBody(this.chassis);
        for (int i = 0; i < this.wheels.length; i++) {
            world.destroyBody(wheels[i]);
        }
        this.isAlive = false;
        return score;
    }

    public boolean equals(Car other){
        if (this.carMass == other.carMass){
            return true;
        }
        return false;
    }

}
