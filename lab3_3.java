import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class Lab3Activity extends AppCompatActivity {

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3);

        final EditText aEditText = (EditText) findViewById(R.id.aEditText);
        final EditText bEditText = (EditText) findViewById(R.id.bEditText);
        final EditText cEditText = (EditText) findViewById(R.id.cEditText);
        final EditText dEditText = (EditText) findViewById(R.id.dEditText);
        final EditText yEditText = (EditText) findViewById(R.id.yEditText);
        final EditText mutationEditText = (EditText) findViewById(R.id.mutationEditText);

        final TextView resTextView = (TextView) findViewById(R.id.resTextViewl3);
        Button calcButton = (Button) findViewById(R.id.calcButtonl3);

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                random = new Random();
                try {
                    int a = Integer.parseInt(aEditText.getText().toString());
                    int b = Integer.parseInt(bEditText.getText().toString());
                    int c = Integer.parseInt(cEditText.getText().toString());
                    int d = Integer.parseInt(dEditText.getText().toString());
                    int y = Integer.parseInt(yEditText.getText().toString());
                    double mutationParam = Double.parseDouble(mutationEditText.getText().toString());
                    long start = System.nanoTime();
                    int[] x1234 = findRoots(a, b, c, d, y, mutationParam);
                    long execTimeMls = (System.nanoTime() - start) / 1_000_000;
                    resTextView.setText(
                            String.format("x1 = %d\nx2 = %d\nx3 = %d\nx4 = %d\nExecution time: %d ms",
                                    x1234[0], x1234[1], x1234[2], x1234[3], execTimeMls
                            )
                    );
                }
                catch (NumberFormatException e) {
                    resTextView.setText("Wrong data was inputted!");
                }
            }
        });
    }

    private int[][] makePairs(double[] survProb, int[][] population) {
        int[][] pairs = new int[population.length][2];
        int parentsNum = survProb.length / 2;
        int[] parents = new int[parentsNum];
        int maxIndex;
        double max;
        for(int i = 0; i < parentsNum; i++) {
            max = survProb[0];
            maxIndex = 0;
            for (int j = 1; j < survProb.length; j++) {
                if (survProb[j] > max) {
                    max = survProb[j];
                    maxIndex = j;
                }
            }
            survProb[maxIndex] = -1;
            parents[i] = maxIndex;
        }

        for (int i = 0; i < pairs.length;) {
            pairs[i][0] = parents[random.nextInt(parents.length)];
            pairs[i][1] = parents[random.nextInt(parents.length)];
            if (pairs[i][0] != pairs[i][1]) {
                i++;
            }
        }
        return pairs;
    }

    private int[] crossover(int[] pair1, int[] pair2) {
        int bound = 1 + random.nextInt(3);
        int[] child = new int[pair1.length];
        for (int i = 0; i < child.length; i++) {
            if (i < bound) {
                child[i] = pair1[i];
            } else {
                child[i] = pair2[i];
            }
        }
        return child;
    }
    
    private int[][] newPopulation(int[][] parentPairs, int[][] population) {
        int[][] newPopulation = new int[population.length][4];
        for (int i = 0; i < population.length; i++) {
            newPopulation[i] = crossover(population[parentPairs[i][0]], population[parentPairs[i][1]]);
        }
        return newPopulation;
    }

    private int[][] firstPopulation(int y) {
        int[][] firstPopulation = new int[2 + random.nextInt(y - 1)][4];
        for (int i = 0; i < firstPopulation.length; i++) {
            for (int j = 0; j < firstPopulation[0].length; j++) {
                firstPopulation[i][j] = random.nextInt(y / 2);
            }
        }
        return firstPopulation;
    }

    private int[] fitness(int[][] population, int[] abcd, int y) {
        int[] dts = new int[population.length];
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[0].length; j++) {
                dts[i] += population[i][j] * abcd[j];
            }
            dts[i] = Math.abs(dts[i] - y);
        }
        return dts;
    }

    private int solution(int[] array) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    private double[] survivalProbability(int[] deltas) {
        double cummProb = 0;
        double[] surv = new double[deltas.length];
        for (int i = 0; i < deltas.length; i++) {
            cummProb += (double) 1 / deltas[i];
        }
        for (int i = 0; i < deltas.length; i++) {
            surv[i] = ((double) 1 / deltas[i]) / cummProb;
        }
        return surv;
    }

    private int[][] populate(int[] deltas, int[][] previousPopulation) {
        double[] survProb = survivalProbability(deltas);
        int[][] parentPairs = makePairs(survProb, previousPopulation);
        return newPopulation(parentPairs, previousPopulation);
    }

    private double mean(double[] array) {
        double mean = 0;
        for (int i = 0; i < array.length; i++) {
            mean += array[i];
        }
        mean /= array.length;
        return mean;
    }

    private void mutate(int[][] population, int y, double mutationParam) {
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[0].length; j++) {
                double coin = random.nextDouble();
                if (coin <= mutationParam) population[i][j] = random.nextInt(y + 1);
            }
        }
    }

    private int[] findRoots(int a, int b, int c, int d, int y, double mutationParam) {
        int[][] population = firstPopulation(y);
        int[] abcd = {a, b, c, d};
        int index;
        int[] dts;
        while (true) {
            dts = fitness(population, abcd, y);
            if ((index = solution(dts)) != -1) {
                break;
            } else {
                double avgSurvivalOld = mean(survivalProbability(dts));
                int[][] newPopulation = populate(dts, population);
                double avgSurvivalNew = mean(survivalProbability(fitness(newPopulation, abcd, y)));
                if (avgSurvivalOld < avgSurvivalNew) {
                    population = newPopulation;
                } else {
                    mutate(population, y, mutationParam);
                }
            }
        }
        return population[index];
    }
}
