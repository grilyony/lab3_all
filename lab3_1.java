
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Lab1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1);

        Button calcButton = (Button) findViewById(R.id.calcButtonl1);
        final EditText numbEditText = (EditText) findViewById(R.id.numbEditText);
        final TextView resTextView = (TextView) findViewById(R.id.resTextViewl1);

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int num = Integer.parseInt(numbEditText.getText().toString());
                    if (isPrimeNumb(num)) resTextView.setText("The number is prime!");
                    else if (num % 2 == 0) resTextView.setText("The number is even!");
                    else {
                        int results[] = doFerma(num);
                        resTextView.setText(String.format("A = %d, B = %d\nA * B = %d", results[0], results[1], num));
                    }
                } catch (NumberFormatException e) {
                    resTextView.setText("You must input unsigned integer!");
                }
            }
        });
    }

    private boolean isPrimeNumb(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    private int[] doFerma(int num) {
        int[] result = new int[2];
        int k = 0;
        int x;
        double y;
        do {
            x = (int) (Math.sqrt(num) + k);
            y = Math.sqrt(Math.pow(x, 2) - num);
            k++;
        } while (y % 1 != 0);
        result[0] = (int) (x + y);
        result[1] = (int) (x - y);
        return result;
    }
}
