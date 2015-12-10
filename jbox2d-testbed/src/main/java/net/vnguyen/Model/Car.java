package net.vnguyen.Model;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.*;

/**
 * Created by Test on 12/9/2015.
 */
public class Car {
    
    private Body chassis;
    public CarShape carShape;
    public Body[] wheels;
    public float carMass;
    public float[] torque;
    public World world;

    public Car(CarShape carShape){
        this.carShape = carShape;
        this.world = carShape.world;
        this.chassis = carShape.createChassis(carShape.vertexList, carShape.chassisDensity);
        this.wheels = new Body[carShape.wheelCount];
        this.torque = new float[carShape.wheelCount];
        initCar();
    }
    
    public void initCar(){
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
    public void calculateCarMass(){
        carMass = chassis.getMass();
        for (int i = 0; i < carShape.wheelCount; i++) {
            carMass += wheels[i].getMass();
        }
        System.out.println(carMass);
    }
    public void calculateTorque(){
        for (int i = 0; i < carShape.wheelCount; i++) {
            torque[i] = carMass * (- world.getGravity().y) / carShape.wheelRadius[i];
        }
    }
    public void createJoint(){
        RevoluteJointDef jointDef = new RevoluteJointDef();
        for (int i = 0; i < carShape.wheelCount; i++) {

            jointDef.bodyA = this.chassis;
            jointDef.bodyB = this.wheels[i];
            jointDef.collideConnected = false;
            Vec2 wheelPlacement = carShape.vertexList[carShape.wheelVertex[i]];
            jointDef.localAnchorA.set((wheelPlacement).x,(wheelPlacement).y);
            jointDef.localAnchorB.set(0,0);
//            jointDef.initialize(this.chassis, this.wheels[i], this.wheels[i].getPosition());
            jointDef.maxMotorTorque = torque[i];
            jointDef.motorSpeed = - carShape.MotorSpeed;
            jointDef.enableMotor = true;


            RevoluteJoint joint =(RevoluteJoint) world.createJoint(jointDef);

//            joint.enableMotor(true);
//            joint.enableLimit(false);


        }
    }

    public Vec2 getPosition(){
        return this.chassis.getPosition();
    }
    public Body createWheel(float radius, float density,Vec2 axelPos){
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
}
