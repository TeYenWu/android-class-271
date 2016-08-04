package com.example.user.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static int REQUEST_CODE_DRINK_MENU_ACTIVITY = 0;

    TextView textView;
    EditText editText;
    RadioGroup radioGroup;
    ListView listView;
    Spinner spinner;

    String drink = "black tea";

    List<Order> orders = new ArrayList<>();

    ArrayList<DrinkOrder> drinkOrders = new ArrayList<>();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        listView = (ListView)findViewById(R.id.listView);
        spinner = (Spinner)findViewById(R.id.spinner);

        sharedPreferences = getSharedPreferences("UIState", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                drink = radioButton.getText().toString();
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                editor.putString("editText", editText.getText().toString());
                editor.apply();

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    submit(v);
                    return true;
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = (Order) parent.getAdapter().getItem(position);
                goToOrderDetail(order);
//                Toast.makeText(MainActivity.this, "You click on" + order.note, Toast.LENGTH_SHORT).show();
//                Snackbar.make(parent, "You click on" + order.getNote(), Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                }).show();
            }
        });

        setupOrderHistory();
//        setupListView();
        setupSpinner();

        restoreUIState();

//        ParseObject parseObject = new ParseObject("TestObject");
//        parseObject.put("foo", "bar");
//        parseObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null)
//                    Toast.makeText(MainActivity.this, "'Success", Toast.LENGTH_LONG).show();
//                else {
//                    e.printStackTrace();
//                    Toast.makeText(MainActivity.this, "'Failed", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TestObject");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                for (ParseObject parseObject1 : objects)
//                {
//                    Toast.makeText(MainActivity.this, parseObject1.getString("foo"), Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        Log.d("debug", "MainActivity OnCreate");
    }

    private void restoreUIState()
    {
        editText.setText(sharedPreferences.getString("editText", ""));
    }

    private void setupOrderHistory()
    {
//        String orderDatas = Utils.readFile(this, "history");
//        String[] orderData = orderDatas.split("\n");
//        Gson gson = new Gson();
//
//        for (String data : orderData)
//        {
//            try {
//                Order order = gson.fromJson(data, Order.class);
//                if (order != null)
//                    orders.add(order);
//            }
//            catch (JsonSyntaxException e)
//            {
//                e.printStackTrace();
//            }
//        }
        Order.getOrdersFromLocalThenRemote(new FindCallback<Order>() {
            @Override
            public void done(List<Order> objects, ParseException e) {
                if(e == null)
                {
                    orders = objects;
                    setupListView();
                }
            }
        });
    }

    private void setupListView()
    {
//        String[] data = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
//        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, orders);

        OrderAdapter adapter = new OrderAdapter(this, orders);
        listView.setAdapter(adapter);
    }

    private void setupSpinner()
    {
        String[] data = getResources().getStringArray(R.array.storeInfos);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);
    }

    public void submit(View view)
    {
        String text = editText.getText().toString();
        String result = text;
        textView.setText(result);
        editText.setText("");

        Order order = new Order();
        order.setNote(text);
        order.setDrinkOrders(drinkOrders);
        order.setStoreInfo((String) spinner.getSelectedItem());

        orders.add(order);

//        Gson gson = new Gson();
//        String orderData = gson.toJson(order);
//        Utils.writeFile(this, "history", orderData + "\n");
        order.saveEventually();
        order.pinInBackground("Order");

        drinkOrders = new ArrayList<>();
        setupListView();
    }

    public void goToMenu(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);
        intent.putExtra("drinkOrderList", drinkOrders);
        startActivityForResult(intent, REQUEST_CODE_DRINK_MENU_ACTIVITY);
    }

    public void goToOrderDetail(Order order)
    {
        Intent intent = new Intent();
        intent.setClass(this, OrderDetailActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_DRINK_MENU_ACTIVITY)
        {
            if(resultCode == RESULT_OK)
            {
                drinkOrders = data.getParcelableArrayListExtra("results");
                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "MainActivity OnStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "MainActivity OnResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "MainActivity OnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "MainActivity OnStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "MainActivity onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "MainActivity onDestroy");
    }
}
