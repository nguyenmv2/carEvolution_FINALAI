package org.jbox2d.testbed.tests;

import net.vnguyen.Model.Car;
import net.vnguyen.Model.CarShape;
import net.vnguyen.Model.GeneticAlgo;
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
   public GeneticAlgo gene;

    @Override
    public void initTest(boolean deserialized) {
        Track t = new Track(m_world);
        this.gene = new GeneticAlgo(m_world);
        gene.makeInitialGeneration();

    }

    @Override
    public String getTestName() {
        return "Car Evolution";
    }

    @Override
    public synchronized void step(TestbedSettings settings) {
        super.step(settings);
        addTextLine("Number of Generation " + (gene.generationCount+1) );
        for (int i = 0; i < this.gene.generationSize; i++) {

            if (!gene.currentGen[i].isAlive){
                continue;
            }

            gene.currentGen[i].stepCount++;
            if (gene.currentGen[i].isDead()){
                float score = gene.currentGen[i].kill();
                gene.dead++;

                if(gene.dead >= gene.generationSize ){
                    gene.makeNewGen();

                }
            }
        }

    }



}


