package net.vnguyen.Model;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

/**
 * Created by Test on 12/9/2015.
 */
public class CarBody extends Body{

    public Vec2[] getVertexList() {
        return vertexList;
    }

    public Vec2[] vertexList;
    public CarBody(BodyDef bodyDef, World world) {
        super(bodyDef, world);
    }
}
