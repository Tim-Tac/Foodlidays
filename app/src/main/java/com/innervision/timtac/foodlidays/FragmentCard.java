package com.innervision.timtac.foodlidays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import com.innervision.timtac.foodlidays.UtilitiesClass.*;
import java.util.ArrayList;

public class FragmentCard extends Fragment {

    public static ArrayList<Order_Article> myOrderArticles = new ArrayList<>();

    //UI declaration
    private TextView empty;
    private Button command;
    private TextView order_total;
    private static ListView orderList;


    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {

        View v = inflater.inflate(R.layout.activity_card, group, false);

        empty = (TextView)v.findViewById(R.id.card_empty);
        orderList = (ListView)v.findViewById(R.id.order_list);
        command = (Button)v.findViewById(R.id.order_button);
        order_total = (TextView)v.findViewById(R.id.order_total);

        if(myOrderArticles.isEmpty()) ShowEmptyCard();
        else FillCard();


        command.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MakeCommand();
            }
        });


        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                ShowDialogQuantity(position);
            }
        });

        return v;
    }


    public void ShowEmptyCard()
    {
        orderList.setVisibility(View.GONE);
        command.setVisibility(View.GONE);
        order_total.setVisibility(View.GONE);
    }


    public void FillCard()
    {
        empty.setVisibility(View.GONE);

        AdapterOrderArticleToList ad = new AdapterOrderArticleToList();
        orderList.setAdapter(ad);

        float Total = 0;
        for(int i = 0; i < myOrderArticles.size() ; i++)
        {
            Total = Total + (Float.parseFloat(myOrderArticles.get(i).prix)*myOrderArticles.get(i).quantity);
        }
        order_total.setText("Total : " + UtilitiesFunctions.round(Total, 3) + " €");
    }


    public void MakeCommand()
    {

    }


    public void ShowDialogQuantity(int p)
    {
        final int position = p;
        final Order_Article article = (Order_Article)orderList.getItemAtPosition(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View quantity_view = inflater.inflate(R.layout.dialog_quantity, null);

        final NumberPicker pick = (NumberPicker)quantity_view.findViewById(R.id.numberPicker);
        pick.setMaxValue(25);
        pick.setMinValue(1);
        pick.setValue(article.quantity);
        UtilitiesFunctions.setNumberPickerTextColor(pick, 0xff000000);


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
                Disposer.mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        builder.setNeutralButton("Supprimer l'article", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                myOrderArticles.remove(position);
                Disposer.mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public class AdapterOrderArticleToList extends BaseAdapter {

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

            final Order_Article art = myOrderArticles.get(position);

            Picasso.with(getActivity()).load(UtilitiesConfig.url_base + "/uploads/" + art.image).into(order_pic);
            nom_plat.setText(art.name);
            prix_pc.setText(art.prix + " €");
            quantite.setText(String.valueOf(art.quantity));

            prix_st.setText(String.valueOf(UtilitiesFunctions.round(art.quantity * (Float.parseFloat(art.prix)), 2)));

            return convertView;

        }
    }
}
