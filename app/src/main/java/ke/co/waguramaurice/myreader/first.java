package ke.co.waguramaurice.myreader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class first extends Activity implements OnClickListener {
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    public static String mess = "";
    private Button back;
    private Button sendsms;
    private NfcAdapter mNfcAdapter;
    private TextView mTextView;
    EditText ACID, ADMIN;
    String activity_id, admission_no;

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
        private NdefReaderTask() {
        }

        protected String doInBackground(Tag... params) {
            String str = null;
            int i = 0;
            Ndef ndef = Ndef.get(params[0]);
            if (ndef != null) {
                NdefRecord[] records = ndef.getCachedNdefMessage().getRecords();
                int length = records.length;
                while (i < length) {
                    NdefRecord ndefRecord = records[i];
                    if (ndefRecord.getTnf() == (short) 1 && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        try {
                            str = readText(ndefRecord);
                            break;
                        } catch (UnsupportedEncodingException e) {
                            Log.e("NfcDemo", "Unsupported Encoding", e);
                        }
                    }
                    i++;
                }
            }
            return str;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            int languageCodeLength = payload[0] & 51;
            return new String(payload, languageCodeLength + 1, (payload.length - languageCodeLength) - 1, (payload[0] & 128) == 0 ? "UTF-8" : "UTF-16");
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                ADMIN.setText(result);
                first.this.mTextView.setText("Student Registration Number: " + result);
                first.this.mTextView.setTextColor(-65536);
                first.mess = result;

            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        this.ACID = (EditText) findViewById(R.id.activity_id);
        this.ADMIN = (EditText) findViewById(R.id.admission_no);
        this.mTextView = (TextView) findViewById(R.id.textView1);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.back = (Button) findViewById(R.id.back);
        this.back.setOnClickListener(this);


        if (this.mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (this.mNfcAdapter.isEnabled()) {
            this.mTextView.setText(R.string.explanation);
        } else {
            this.mTextView.setText("ALERT!! NFC is Disabled!");
            this.mTextView.setTextColor(-65536);
            Toast.makeText(this, "Enable NFC in Settings.", Toast.LENGTH_LONG).show();


        }
        handleIntent(getIntent());
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back) {
            startActivity(new Intent(this, MainActivity.class));
            finish();

        }

    }

    public void push(View view) {
        activity_id = ACID.getText().toString();
        admission_no = ADMIN.getText().toString();
        String method = "addl";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(method, activity_id, admission_no);
        startActivity(new Intent(this, first.class));
        finish();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Internet Connection Required!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 10);
            toast.show();

        }


    }


    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, this.mNfcAdapter);
    }

    protected void onPause() {
        stopForegroundDispatch(this, this.mNfcAdapter);
        super.onPause();
    }

    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Tag tag;
        if ("android.nfc.action.NDEF_DISCOVERED".equals(action)) {
            String type = intent.getType();
            if ("text/plain".equals(type)) {
                tag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
                new NdefReaderTask().execute(tag);
                return;
            }
            Log.d("NfcDemo", "Wrong mime type: " + type);

        }
    }

    public static void setupForegroundDispatch(Activity activity, NfcAdapter adapter) {
        Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[0][];
        filters[0] = new IntentFilter();
        filters[0].addAction("android.nfc.action.NDEF_DISCOVERED");
        filters[0].addCategory("android.intent.category.DEFAULT");
        try {
            filters[0].addDataType("text/plain");
            adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
    }

    public static void stopForegroundDispatch(Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}
