package com.example.user.simpleui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DrinkMenuActivity extends AppCompatActivity implements DrinkOrderDialog.OnDrinkOrderListener {

    ListView drinkMenuListView;
    TextView totalTextView;

    String[] drinkNames = new String[]{"White gourd tea", "Black tea", "Pearl black tea", "Milk black tea"};
    int[] lPrices = new int[]{25,35,35,25};
    int[] mPrices = new int[]{15,25,25,15};
    int[] images = new int[]{R.drawable.drink1, R.drawable.drink4, R.drawable.drink3, R.drawable.drink2};

    List<Drink> drinkList = new ArrayList<>();

    ArrayList<DrinkOrder> drinkOrderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);

        drinkMenuListView = (ListView)findViewById(R.id.drinkMenuListView);
        totalTextView = (TextView)findViewById(R.id.totalTextView);

        setData();

        drinkOrderList = getIntent().getParcelableArrayListExtra("drinkOrderList");
//        setupTotalTextView();

        setupDrinkMenuListView();

        Log.d("Debug", "DrinkMenuActivity OnCreate");
    }

    public void setData()
    {
//        for(int i = 0; i < 4; i++)
//        {
//            Drink drink = new Drink();
//            drink.name = drinkNames[i];
//            drink.lPrice = lPrices[i];
//            drink.mPrice = mPrices[i];
//            drink.imageId = images[i];
//            drinkList.add(drink);
//        }
        Drink.syncDrinksFromRemote(new FindCallback<Drink>() {
            @Override
            public void done(List<Drink> objects, ParseException e) {
                drinkList = objects;
                setupDrinkMenuListView();
            }
        });
    }

    public void setupDrinkMenuListView()
    {
        DrinkAdapter adapter = new DrinkAdapter(this, drinkList);
        drinkMenuListView.setAdapter(adapter);

        drinkMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Drink drink = (Drink) parent.getAdapter().getItem(position);
                showDrinkOrderDialog(drink);
                setupTotalTextView();
            }
        });
    }

    private void  showDrinkOrderDialog(Drink drink)
    {
        DrinkOrder order = new DrinkOrder(drink);

        for(DrinkOrder drinkOrder: drinkOrderList)
        {
            if(drinkOrder.getDrink().getName().equals(drink.getName()))
            {
                order = drinkOrder;
                break;
            }
        }

        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction ft = fragmentManager.beginTransaction();

        DrinkOrderDialog dialog = DrinkOrderDialog.newInstance(order);

        dialog.show(ft, "DrinkOrderDialog");

    }

    public void setupTotalTextView()
    {
        int total = 0;
        for (DrinkOrder drinkOrder :drinkOrderList)
        {
            total += drinkOrder.total();
        }

        totalTextView.setText(String.valueOf(total));
    }

    public void done(View view)
    {
        Intent intent = new Intent();
        intent.putExtra("results", drinkOrderList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "DrinkMenuActivity OnStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "DrinkMenuActivity OnResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "DrinkMenuActivity OnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "DrinkMenuActivity OnStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "DrinkMenuActivity onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "DrinkMenuActivity onDestroy");
    }

    @Override
    public void onDrinkOrderFinished(DrinkOrder drinkOrder) {
        for (int i = 0 ; i < drinkOrderList.size() ; i++)
        {
            if(drinkOrderList.get(i).getDrink().getName().equals(drinkOrder.getDrink().getName()))
            {
                drinkOrderList.set(i, drinkOrder);
                setupTotalTextView();
                return;
            }
        }

        drinkOrderList.add(drinkOrder);
        setupTotalTextView();
    }
}
