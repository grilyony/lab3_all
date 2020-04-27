import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Lab2Activity extends AppCompatActivity {

    private final double P = 4.0;
    private final int[][] points =  {
            {0, 6},
            {1, 5},
            {3, 3},
            {2, 4}
    };
    private double w1, w2;
    private TextView resTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2);

        final EditText deadlineEditText = (EditText) findViewById(R.id.dedlineEditText);
        final EditText iterationsEditText = (EditText) findViewById(R.id.iterationsEditText);
        final EditText rateEditText = (EditText) findViewById(R.id.rateEditText);
        Button calcButton = (Button) findViewById(R.id.calcButtonl2);
        resTextView = (TextView) findViewById(R.id.resTextViewl2);

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int iterationsNum = Integer.parseInt(iterationsEditText.getText().toString());
                    double learningRate = Double.parseDouble(rateEditText.getText().toString());
                    long timeDeadline = (long) (Double.parseDouble(deadlineEditText.getText().toString()) * 1_000_000_000);
                    train(learningRate, iterationsNum, timeDeadline);

                } catch (NumberFormatException e) {
                    resTextView.setText("Wrong data was inputted!");
                }
            }
        });
    }

    private void train(double rate, int iterationsNum, long deadline) {
        double y;
        double dt;
        int iterations = 0 ;
        boolean done = false;
        w1 = 0;
        w2 = 0;
        long start = System.nanoTime();

        int index = 0;
        while (iterations++ < iterationsNum && (System.nanoTime() - start) < deadline) {

            index %= 4;

            y = points[index][0] * w1 + points[index][1] * w2;

            if (isDone()) {
                done = true;
                break;
            }

            dt = P - y;
            w1 += dt * points[index][0] * rate;
            w2 += dt * points[index][1] * rate;
            index++;
        }

        if (done) {
            long execTimeMcs = (System.nanoTime() - start) / 1_000;
            resTextView.setText(
                    String.format(
                            "Trained successfully!\n" +
                                    "w1 = %-6.3f w2 = %-6.3f\n" +
                                    "Execution time: %d mcs" +
                                    "\nIterations: %d", w1, w2, execTimeMcs, iterations
                    )
            );

        } else {
            String reason = "Training failed!";
            if (iterations >= iterationsNum) {
                reason += "\nMore iterations needed!";
            } else {
                reason += "\nMore time is required!";
            }
            resTextView.setText(reason);
        }

    }

    private boolean isDone() {
        return P < points[0][0] * w1 + points[0][1] * w2
                && P < points[1][0] * w1 + points[1][1] * w2
                && P > points[2][0] * w1 + points[2][1] * w2
                && P > points[3][0] * w1 + points[3][1] * w2;
    }
}
