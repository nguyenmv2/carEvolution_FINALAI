package org.jbox2d.testbed.tests;

import net.vnguyen.Model.Car;
import net.vnguyen.Model.CarShape;
import net.vnguyen.Model.Track;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

/**
 * Created by Test on 12/8/2015.
 */
public class CarEvolution extends TestbedTest {
    private static int CAR_COUNT = 10;
    static Color3f color = new Color3f(.9f, .9f, .9f);
    static Color3f color2 = new Color3f(.9f, .5f, .5f);

    private Body[] trackTiles;
    private Car[] cars;
    @Override
    public void initTest(boolean deserialized) {
        Track t = new Track(m_world);
        for (int i = 0; i < CAR_COUNT; i++) {
            CarShape shape = new CarShape(m_world);
            Car car = new Car(shape);
        }
    }

    @Override
    public String getTestName() {
        return "Car Evolution";
    }

    @Override
    public synchronized void step(TestbedSettings settings) {
        super.step(settings);
        addTextLine("Press g to generate a new random convex hull");

    }

    public void drawCar(){
        for (int i = cars.length -1; i >= 0; i--) {
            Car c = cars[i];
            Vec2 carPos = c.getPosition();

            for (int j = 0; j < c.wheels.length; j++) {
                Body b = c.wheels[j];
                Fixture f = b.getFixtureList();
                CircleShape s = (CircleShape)f.getShape();
                float colorCode = ((Math.round(255 - (255 * (f.m_density - c.carShape.WheelMinDensity)) / c.carShape.WheelMaxDensity)));
                Color3f color = new Color3f(colorCode,colorCode,colorCode);
                getDebugDraw().drawCircle(s.m_p, s.m_radius, color);
            }

        }
    }

}


