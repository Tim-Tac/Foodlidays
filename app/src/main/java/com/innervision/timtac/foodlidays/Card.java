package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Card extends Activity {

    public static ArrayList<Order_Articles> myOrderArticles = new ArrayList<>();
    public static ListView orderList;
    private float Total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        TextView empty = (TextView)findViewById(R.id.card_empty);
        orderList = (ListView)findViewById(R.id.order_list);
        Button command = (Button)findViewById(R.id.order_button);
        TextView order_total = (TextView)findViewById(R.id.order_total);

        if(myOrderArticles.size() == 0)
        {
            orderList.setVisibility(View.GONE);
            command.setVisibility(View.GONE);
            order_total.setVisibility(View.GONE);

        }
        else
        {
            /*** Si il y a des articles dans le panier ***/

            empty.setVisibility(View.GONE);

            CustomArrayAdapter ad = new CustomArrayAdapter();
            orderList.setAdapter(ad);

            for(int i = 0; i < myOrderArticles.size() ; i++)
            {
                Total = Total + (Float.parseFloat(myOrderArticles.get(i).prix)*myOrderArticles.get(i).quantity);
            }
            order_total.setText("Total : " + round(Total,3) + " €");

            command.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });


            orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    final Order_Articles article = (Order_Articles)orderList.getItemAtPosition(position);

                    /******* Dialog pour supprimer l'item ou modifier la quantité *******/

                    AlertDialog.Builder builder = new AlertDialog.Builder(Card.this);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View quantity_view = inflater.inflate(R.layout.option_order, null);

                    final NumberPicker pick = (NumberPicker)quantity_view.findViewById(R.id.numberPicker);
                    pick.setMaxValue(25);
                    pick.setMinValue(1);
                    pick.setValue(article.quantity);
                    FoodCard.setNumberPickerTextColor(pick,0xff000000);


                    builder.setTitle("Quantité " + article.name);
                    builder.setView(quantity_view);

                    builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //ne rien faire
                        }
                    });

                    builder.setPositiveButton("Modifier la quantité", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            article.quantity = pick.getValue();
                            finish();
                            startActivity(getIntent());
                        }
                    });

                    builder.setNeutralButton("Supprimer l'article", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            myOrderArticles.remove(position);
                            finish();
                            startActivity(getIntent());
                        }
                    });




                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


        }
    }


    public class CustomArrayAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return myOrderArticles.size();
        }

        @Override
        public Object getItem(int position) {
            return myOrderArticles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.order_list,parent,false);

            TextView nom_plat = (TextView)convertView.findViewById(R.id.order_nom_plat);
            TextView prix_pc = (TextView)convertView.findViewById(R.id.order_prix_pc);
            TextView quantite = (TextView)convertView.findViewById(R.id.order_quantity);
            TextView prix_st = (TextView)convertView.findViewById(R.id.order_prix_st);
            ImageView order_pic = (ImageView)convertView.findViewById(R.id.order_img);

            final Order_Articles art = myOrderArticles.get(position);

            Picasso.with(getApplicationContext()).load("http://foodlidays.dev.innervisiongroup.com/uploads/" + art.image).into(order_pic);
            nom_plat.setText(art.name);
            prix_pc.setText(art.prix + " €");
            quantite.setText(String.valueOf(art.quantity));

            prix_st.setText(String.valueOf(round(art.quantity*(Float.parseFloat(art.prix)),2)));

            return convertView;

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

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}
