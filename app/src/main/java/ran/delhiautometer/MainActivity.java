package ran.delhiautometer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void CalculateKilo(View view) {
        double fare ;
        EditText kilometers = (EditText) findViewById(R.id.kilometers);
        try {
            if (kilometers.getText().toString().length() <= 0)
                fare = 0;
            else {
                float kilo = Float.valueOf(kilometers.getText().toString());
                if (kilo == 0)
                    fare = 0;
                else {
                    if (kilo <= 2) {
                        fare = 25;
                    } else
                        fare = (kilo - 2) * 8 + 25;
                }
            }

            fare = Math.floor(fare * 100) / 100;
            showfare(fare);
        } catch (Exception e) {
    //      Catches any exception here
        }
    }

    public void CalculateOld(View view) {
        double fare = 0;
        EditText OldFare = (EditText) findViewById(R.id.oldfare);
        try {
            if (OldFare.getText().toString().length() <= 0)
                fare = 0;
            else {
                float old = Float.valueOf(OldFare.getText().toString());
                if (old > 0) {
                    if (old <= 19)
                        fare = 25;
                    else {
                        fare = (old - 19) * (8 / 6.5) + 25;
                    }
                }
            }

            fare = Math.floor(fare * 100) / 100;
            showfare(fare);
        }catch (Exception e){
            //      Catches any exception here
        }
        }

    public void TrackReal(View view) {
        Intent intent = new Intent(this, Track.class);
        startActivity(intent);
    }

    public void showfare(double fare) {
        Context context = this;
        AlertDialog.Builder faredialog = new AlertDialog.Builder(context);
        faredialog.setTitle("Your Fare is:");
        faredialog.setMessage("Rs." + fare + "");
        faredialog.show();
    }
}
