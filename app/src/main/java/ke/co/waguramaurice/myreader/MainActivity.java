package ke.co.waguramaurice.myreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    private Button one;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.one = (Button) findViewById(R.id.button1);
        this.one.setOnClickListener(this);


    }

    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.button1) {
            startActivity(new Intent(this, first.class));
            finish();
        }

    }
}
