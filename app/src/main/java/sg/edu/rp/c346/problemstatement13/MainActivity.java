package sg.edu.rp.c346.problemstatement13;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {


    Button btnsend;
    Button btnmessage;
    EditText etTo;
    EditText etContent;

    BroadcastReceiver br = new MessageReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnsend = findViewById(R.id.button);
        etTo = findViewById(R.id.editText);
        etContent = findViewById(R.id.editText2);
        btnmessage = findViewById(R.id.button2);


        checkPermission();

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = etTo.getText().toString();
                String message = etContent.getText().toString();
                StringTokenizer st = new StringTokenizer(phoneNo,",");

                while (st.hasMoreElements())
                {
                    String tempMobileNumber = (String)st.nextElement();
                    if(tempMobileNumber.length()>0 && message.trim().length()>0) {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(tempMobileNumber, null, message, null, null);
                    }
                }
            }
        });

        btnmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();

            }
        });

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);
    }
    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(br);
    }
    private void sendSMS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            //String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

            Uri sms_uri = Uri.parse("smsto:" + etTo.getText().toString());
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, sms_uri);
            sendIntent.putExtra("sms_body", etContent.getText().toString());

            startActivity(sendIntent);

        }
        else {
            String phoneNo = etTo.getText().toString();
            String message = etContent.getText().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("address", phoneNo);
            intent.putExtra("sms_body", message);
            startActivity(intent);
        }
    }
}
