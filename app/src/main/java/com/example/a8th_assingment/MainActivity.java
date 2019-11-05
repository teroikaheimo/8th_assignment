package com.example.a8th_assingment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.a8th_assingment.room.EntityListItem;
import com.example.a8th_assingment.room.ModelListItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/*
 * API data example
 * JSON
 * {
 *  "file":"https:\/\/loremflickr.com\/cache\/resized\/65535_48691638487_3584a7fca6_320_240_g.jpg",
 *  "license":"cc-nc-sa",
 *  "owner":"Carbon Arc",
 *  "width":320,"height":240,
 *  "filter":"g",
 *  "tags":"cat",
 *  "tagMode":"all"
 * }
 * */

public class MainActivity extends AppCompatActivity {
    final String url = "https://loremflickr.com/json/g/320/240/";
    // Layouts
    LinearLayout mainLayout;
    LinearLayout toolbar;
    // Views
    Button buttonFind;
    TextView editTextSearch;
    ListView listView;
    // List
    ArrayList<EntityListItem> list;
    CustomAdapter customAdapter;
    Toast loading;
    // Database
    private ModelListItem modelListItem;
    // Request HTTP
    private RequestQueue requestQueue;

    // Internet connection status
    private ConnectivityManager connMan;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG);

        // Database
        modelListItem = new ViewModelProvider(this).get(ModelListItem.class);

        // Layouts
        mainLayout = findViewById(R.id.mainLayout);
        toolbar = findViewById(R.id.toolbar);

        // Views
        listView = findViewById(R.id.listView);
        editTextSearch = new EditText(this);
        editTextSearch.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 80));
        buttonFind = new Button(this);
        buttonFind.setText(getString(R.string.buttonFind));
        buttonFind.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 20));

        // Add button and editText to toolbar
        toolbar.addView(editTextSearch);
        toolbar.addView(buttonFind);

        // Button functionality
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRequestToQueue(jsonRequestBuilder(editTextSearch.getText(), url));
            }
        });

        // Setup ListView
        list = new ArrayList<>();
        customAdapter = new CustomAdapter(this, list);
        listView.setAdapter(customAdapter);

        // Get connection manager for internet connection status check.
        connMan = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        requestQueue = Volley.newRequestQueue(this);

        // Get data from database to listView
        modelListItem.showData(customAdapter);
    }

    private JsonObjectRequest jsonRequestBuilder(CharSequence searchWord, String baseUrl) {
        // Exits the program IF search is empty
        if (searchWord.length() < 1) {
            this.finish();
        }
        String requestUrl = baseUrl + searchWord + "/all";
        clearSearch();
        loading.show();

        return new JsonObjectRequest
                (Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
                    String imgUrl;

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            imgUrl = response.getString("file");
                            addRequestToQueue(imageRequestBuilder(imgUrl, response));

                        } catch (JSONException err) {
                            err.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Query failed...", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private ImageRequest imageRequestBuilder(final String url, final JSONObject response) {

        return new ImageRequest
                (url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        if (bitmap != null && response != null) {
                            try {
                                // Create new item to be added to the database
                                EntityListItem responseItem = new EntityListItem();
                                responseItem.file = response.getString("file");
                                responseItem.height = response.getInt("height");
                                responseItem.width = response.getInt("width");
                                responseItem.owner = response.getString("owner");
                                responseItem.license = response.getString("license");
                                responseItem.tagMode = response.getString("tagMode");
                                responseItem.tags = response.getString("tags");

                                // Convert bitmap to byte[] because sqlite stores the image as a Blob
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                                responseItem.image = bos.toByteArray();

                                // Insert item to database
                                modelListItem.insert(responseItem);

                                // Add the latest item to the view
                                modelListItem.showLastAddition(customAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            loading.cancel();
                        }
                    }
                }, 320, 240, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Query failed...", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addRequestToQueue(Request request) {
        // IF there is no internet connection. Prompt user and don't add query to queue.
        if (!this.checkInternetConnection()) {
            Toast.makeText(getApplicationContext(), "Internet connection not available!", Toast.LENGTH_LONG).show();
            return;
        }
        requestQueue.add(request);
    }

    private void clearSearch() {
        editTextSearch.setText("");
        editTextSearch.clearFocus();
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean checkInternetConnection() {
        // Return true IF there is a active network with internet connection.
        NetworkInfo network = connMan.getActiveNetworkInfo();
        return network != null && network.isConnectedOrConnecting();
    }

}
