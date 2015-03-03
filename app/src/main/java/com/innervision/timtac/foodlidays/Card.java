package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class Card extends Activity {

    public static ArrayList<Order_Articles> myOrderArticles = new ArrayList<>();
    public static ListView orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        TextView empty = (TextView)findViewById(R.id.card_empty);
        orderList = (ListView)findViewById(R.id.order_list);
        Button command = (Button)findViewById(R.id.order_button);

        if(myOrderArticles.size() == 0)
        {
            orderList.setVisibility(View.GONE);
            command.setVisibility(View.GONE);
        }
        else
        {
            /*** Si on il y a des articles dans le panier ***/

            empty.setVisibility(View.GONE);



        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(Card.this, Settings.class);
                startActivity(intent);
                return true;
            case R.id.pizza:
                Intent intent2 = new Intent(Card.this, FoodCard.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
