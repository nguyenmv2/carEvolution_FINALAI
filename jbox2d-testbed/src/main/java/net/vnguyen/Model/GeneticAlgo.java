package net.vnguyen.Model;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.*;

/**
 * Created by Test on 12/10/2015.
 */
public class GeneticAlgo
{
    public static final int generationSize = 20;
    public static final int ATTRIBUTE_COUNT = 15;
    public static final double MUTATION_RANGE = 1;
    public static final double MUTATION_PROB = 0.05;
    public Car[] currentGen;
    public HashMap<Float, Car> currentGenMap;
    public ArrayList<Float> currentGenScore;
    private World world;
    public int generationCount =0;
    public int carCount = 0, dead = 0;

    public GeneticAlgo(World world){
        this.world = world;
        currentGen = new Car[generationSize];
        currentGenMap = new HashMap<Float, Car>();
    }


    public void makeInitialGeneration(){
        for (int i = 0; i < generationSize; i++) {
            Car c = new Car(new CarShape(world));
            currentGen[i] = c;
        }
        carCount = 0;
        dead = 0;
    }

    public Car[] makeNextGen(){
        generationCount++;
        calculatePrevScore();
        dead = 0;
        Car[] newGen = new Car[generationSize];
        Car champion = currentGenMap.get(currentGenScore.get(0));
        CarShape champShape = champion.carShape;
        newGen[0] = new Car(champShape);
        for (int i = 1; i < generationSize; i++) {

            int mom = getParents();

            CarShape child = makeChild(currentGen[getParents()], currentGen[mom]);
            mutate(child);
            newGen[i] = new Car(child);
        }
        currentGen = newGen;
        currentGenMap.clear();
        dead = 0;
        return newGen;

    }
    public void makeNewGen(){
        generationCount++;
        Car[] newGen = new Car[generationSize];

        ArrayList<Car> matingPool = tournamentSelection();

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < generationSize; i++) {
            int dad = rand.nextInt(matingPool.size());
            int mom = rand.nextInt(matingPool.size());
            while(dad == mom) {mom = rand.nextInt(matingPool.size());}
            CarShape child = makeChild(matingPool.get(dad), matingPool.get(mom));
            mutate(child);
            newGen[i] = new Car(child);
        }
        currentGen = newGen;
        currentGenMap.clear();
        dead = 0;

    }
    public ArrayList<Car> tournamentSelection(){
        ArrayList<Car> matingPool = new ArrayList<Car>();

        for (Car car : currentGen){
            Random rand = new Random(System.currentTimeMillis());
            Car randomCar = currentGen[rand.nextInt(generationSize)];
            while (randomCar.equals(car)){
                randomCar = currentGen[rand.nextInt(generationSize)];
            }
            Car winner = null;
            if (car.score > randomCar.score){
                winner = car;
            } else {
                winner = (randomCar);
            }

            matingPool.add(winner);
        }
        return matingPool;
    }
    private CarShape makeChild(Car dad, Car mom) {

        int anchor1 = (int) Math.round(Math.random() * (ATTRIBUTE_COUNT-1));
        int anchor2 = (int) Math.round(Math.random() * (ATTRIBUTE_COUNT-1));
        while (anchor1 == anchor2){
            anchor2 = (int) Math.round(Math.random() * (ATTRIBUTE_COUNT-1));
        }

        Car[] parents = new Car[]{dad, mom};
        int curParent = 0;
        int wheelParent =0;

        boolean sameNumWheels = (parents[0].carShape.wheelCount == parents[1].carShape.wheelCount);
        if (!sameNumWheels){
            wheelParent = (int) Math.floor(Math.random() * 2);
        }
        int childWheelCount = parents[wheelParent].carShape.wheelCount;
        float[] childWheelRadius = new float[childWheelCount];
        for (int i = 0; i < childWheelCount; i++) {
            if (sameNumWheels) {
                curParent = pickParent(curParent, i, anchor1, anchor2);
            } else {
                curParent = wheelParent;
            }
            childWheelRadius[i] = parents[curParent].carShape.wheelRadius[i];
        }

        int[] childWheelVertex = new int[childWheelCount];
        for (int i = 0; i < childWheelCount; i++) {
            if (sameNumWheels){
                curParent = pickParent(curParent, i+2, anchor1, anchor2);
            } else {
                curParent = wheelParent;
            }
            childWheelVertex[i] = parents[curParent].carShape.wheelVertex[i];
        }

        float[] childWheelDensity = new float[childWheelCount];
        for (int i = 0; i < childWheelCount; i++) {
            if (sameNumWheels){
                curParent = pickParent(curParent, i+12, anchor1, anchor2);
            } else {
                curParent = wheelParent;
            }
            childWheelDensity[i] =  parents[curParent].carShape.wheelDensity[i];
        }

        Vec2[] childVertexList = new Vec2[8];
//        for (int i = 0; i < 8; i++) {
//            curParent = pickParent(curParent,4+i,anchor1,anchor2);
//            childVertexList[i] = parents[curParent].carShape.vertexList[i];
//        }
        curParent = pickParent(curParent, 4, anchor1, anchor2);
        childVertexList[0] = parents[curParent].carShape.vertexList[0];
        curParent = pickParent(curParent, 5, anchor1, anchor2);
        childVertexList[1] = parents[curParent].carShape.vertexList[1];
        curParent = pickParent(curParent, 6, anchor1, anchor2);
        childVertexList[2] = parents[curParent].carShape.vertexList[2];
        curParent = pickParent(curParent, 7, anchor1, anchor2);
        childVertexList[3] = parents[curParent].carShape.vertexList[3];
        curParent = pickParent(curParent, 8, anchor1, anchor2);
        childVertexList[4] = parents[curParent].carShape.vertexList[4];
        curParent = pickParent(curParent, 9, anchor1, anchor2);
        childVertexList[5] = parents[curParent].carShape.vertexList[5];
        curParent = pickParent(curParent, 10, anchor1, anchor2);
        childVertexList[6] = parents[curParent].carShape.vertexList[6];
        curParent = pickParent(curParent, 11, anchor1, anchor2);
        childVertexList[7] = parents[curParent].carShape.vertexList[7];


        curParent = pickParent(curParent, 14, anchor1, anchor2);
        float childChassisDensity = parents[curParent].carShape.chassisDensity;


        CarShape childShape = new CarShape(childVertexList,childChassisDensity,childWheelCount,childWheelVertex,childWheelRadius,childWheelDensity);
        childShape.setWorld(world);
        return childShape;
    }

    private int getParents() {
        double val = Math.random();
        if (val == 0) return 0;
        int result = (int) Math.floor(-Math.log(val) * generationSize) % generationSize;
        System.out.println("Parent idx = " + result);
        return result;
    }

    private void calculatePrevScore() {
        for (int i = 0; i < generationSize; i++) {
            currentGenMap.put(currentGen[i].score, currentGen[i]);

        }
        Set<Float> scoresAsCollection = currentGenMap.keySet();
        ArrayList<Float> scores = new ArrayList<Float>(scoresAsCollection);
        Collections.sort(scores);
        currentGenScore = scores;
    }

    public int pickParent(int cur, int attributeIdx, int anchor1, int anchor2){
        int result;
        if ( (anchor1 == attributeIdx) || (anchor2 == attributeIdx) ){
            result = (cur == 1 ? 1 : 0);
        } else {
            result = cur;
        }
        return result;
    }

    public float mutateValue(double old, double min, double range){
        double span = range * MUTATION_RANGE;
        double base  = old - 0.5 * span;
        if (base < min) base = min;
        if (base > min + range - span) base = min + range - span;
        return (float) (base + span * Math.random());
    }

    public void mutate(CarShape original){
        for (int i = 0; i < original.wheelCount; i++) {
            if (Math.random() < MUTATION_PROB){
                original.wheelRadius[i] = mutateValue(original.wheelRadius[i], CarShape.WheelMinRadius, CarShape.WheelMaxRadius);
            }
        }

        float WheelMutationProb = (float) (MUTATION_RANGE < MUTATION_PROB ? MUTATION_RANGE : MUTATION_PROB);

        for (int i = 0; i < original.wheelCount ; i++) {
            if ( Math.random () < MUTATION_PROB){
                original.wheelDensity[i] = mutateValue(original.wheelDensity[i], CarShape.WheelMinDensity, CarShape.WheelMinDensity);
            }
        }
        if (Math.random() < MUTATION_PROB)
            original.chassisDensity = mutateValue(original.chassisDensity, CarShape.ChassisMinDensity, CarShape.ChassisMaxDensity);
    }
}
