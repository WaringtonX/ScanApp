package com.example.scanapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapters.BarcodeAdapter;
import Models.Barcode;
import Utils.HttpConnection;

public class MainActivity extends AppCompatActivity {

    private static final String get_all_barcode_url = "http://barcodeapi-001-site1.gtempurl.com/api/BarCode/GetAllBarCodes";
    private static final String update_item_count = "http://barcodeapi-001-site1.gtempurl.com/api/BarCode/UpdateItemEntry";
    private static final String create_barcode = "http://barcodeapi-001-site1.gtempurl.com/api/BarCode/AddBarcode";
    private static final String ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9]+$";
    public List<Barcode> barcodelistout = new ArrayList<>();

    private Toolbar toolbar;
    private EditText txtbarcode;
    private Button btnscanadd;
    private ListView listView;
    private BarcodeAdapter barcodeAdapter;
    EditText txt; // user input bar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.main_appbarss);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ScanApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logoo);

        if(getSupportActionBar()!=null) {
            Bitmap bitmap = getBitmapFromVectorDrawable(this, R.drawable.qrcode);
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 90, 90, false));
            toolbar.setLogo(newdrawable);
        }

        txtbarcode = (EditText) findViewById(R.id.txtbarcode);
        btnscanadd = (Button) findViewById(R.id.btnscanadd);
        listView   = (ListView) findViewById(R.id.barcodelistview);

        loadData();

        txtbarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                  if(s.length() != 0) {
                      btnscanadd.setText("Add");
                  }else {
                      btnscanadd.setText("Scan");
                  }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnscanadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ScanCode();
                String butontext = btnscanadd.getText().toString();
                if(butontext.equalsIgnoreCase("Scan")) {
                    ScanCode();
                }else if (butontext.equalsIgnoreCase("Add")){
                     String result = txtbarcode.getText().toString();

                    Barcode foundbarcode = new Barcode();
                    boolean isbarcoudefound = false;
                    for(Barcode br : barcodelistout) {
                        if(br.getBarCode1().equalsIgnoreCase(result)){
                            isbarcoudefound = true;
                            foundbarcode = br;
                        }
                    }

                    if(isbarcoudefound) {
                        int c = foundbarcode.getBarItemcount() +1;
                        new BarcodeImplemantations().UpdateItemCount(c,foundbarcode.getBarId());
                    }else {
                        processResults(result);
                    }
                }
            }
        });
    }

    public void loadData() {
        try {
            URL url = new URL(get_all_barcode_url);
            new BarcodeImplemantations().execute(url);
            // Toast.makeText(this,"Length : " + barimp.barcodelist.size(),Toast.LENGTH_LONG).show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public void ScanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActiviy.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {
        String resultcontent = result.getContents();
        Barcode foundbarcode = new Barcode();
        boolean isbarcoudefound = false;
        for(Barcode br : barcodelistout) {
            if(br.getBarCode1().equalsIgnoreCase(resultcontent)){
                isbarcoudefound = true;
                foundbarcode = br;
            }
        }

        if(isbarcoudefound) {
            int c = foundbarcode.getBarItemcount() +1;
            new BarcodeImplemantations().UpdateItemCount(c,foundbarcode.getBarId());
        }else {
            processResults(resultcontent);
        }

    });

    public void processResults(String resultcontent) {
        if(resultcontent.length() == 12) {
            boolean isalpha = isAlphanumeric(resultcontent);
            if(isalpha) {

                Barcode br = new Barcode();
                br.setBarCode1(resultcontent);
                br.setBarName("random");
                br.setBarItemcount(1);
                br.setBarCreatedate("2023-05-18T08:57:10.218Z");

                new BarcodeImplemantations().CreateBarCode(br);
            }else {
                Toast.makeText(this,"Incorrect Bar/Qr Code Format!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"Bar/Qr Code is Invalid!", Toast.LENGTH_SHORT).show();
        }
    }


    public static boolean isAlphanumeric(final String input) {
        return input.matches(ALPHANUMERIC_PATTERN);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public class BarcodeImplemantations extends AsyncTask<URL,Void,String> {

        public final List<Barcode> barcodelist = new ArrayList<>();
        public BarcodeImplemantations() {
        }


        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String data = null;
            try{
                data = HttpConnection.CreateHTTPRequest(url);
            }catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                parseJson(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public void CreateBarCode(Barcode code) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(create_barcode);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        conn.setRequestProperty("Accept", "*/*");
                        conn.setRequestProperty("Connection", "keep-alive");
                        conn.setDoOutput(true);
                       // conn.setDoInput(true);
                        conn.connect();

                        System.out.println("Post Started : " + code.toString());
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("barId", "0");
                        jsonParam.put("barCode1", code.getBarCode1());
                        jsonParam.put("barName", code.getBarName());
                        jsonParam.put("barItemcount", code.getBarItemcount());
                        jsonParam.put("barCreatedate", code.getBarCreatedate());

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(jsonParam.toString());
                        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                        for (int c; (c = in.read()) >= 0;)
                            System.out.print((char)c);
                        //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                       // System.out.println("Post json object : " + jsonParam.toString());
                       // byte[] postData  = jsonParam.toString().getBytes(StandardCharsets.UTF_8 );
                      //  os.writeBytes(jsonParam.toString());
                        System.out.println("Post Started DataOutputStream");

                       /* os.flush();
                        os.close();
                        conn.disconnect();
                        System.out.println("Post Started Done");*/

                        loadData();
                    }catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }

        public void UpdateItemCount(int count,int id) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(update_item_count + "?count="+count+"&id="+id);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        conn.setRequestProperty("Accept", "*/*");
                        conn.setRequestProperty("Connection", "keep-alive");
                        conn.setDoOutput(true);
                        // conn.setDoInput(true);
                        conn.connect();

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.flush();
                        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                        for (int c; (c = in.read()) >= 0;)
                            System.out.print((char)c);
                        System.out.println("Post Started DataOutputStream");

                        loadData();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }



        public void parseJson(String data) throws JSONException {
            JSONObject jsonObject = null;
            try{
                jsonObject  = new JSONObject(data);
            }catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray jsonarray = new JSONArray(data);

            for(int i = 0;i < jsonarray.length();i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject obj = jsonarray.getJSONObject(i);
                Barcode code = new Barcode();

                int barid = Integer.parseInt(obj.get("barId").toString());
                String barCode1 = obj.get("barCode1").toString();
                String barName = obj.get("barName").toString();
                int barItemcount = Integer.parseInt(obj.get("barItemcount").toString());
                String barCreatedate = obj.get("barCreatedate").toString();
                // System.out.println("Array Size name : " + barItemcount);
                code.setBarId(barid);
                code.setBarCode1(barCode1);
                code.setBarName(barName);
                code.setBarItemcount(barItemcount);
                code.setBarCreatedate(barCreatedate);

                barcodelist.add(code);
            }

            barcodelistout = barcodelist;
            System.out.println("Array Size  : " + barcodelist.size());
            barcodeAdapter = new BarcodeAdapter(getApplicationContext(), barcodelist);
            listView.setAdapter(barcodeAdapter);
        }

    }


}