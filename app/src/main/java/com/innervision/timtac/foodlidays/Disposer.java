package com.innervision.timtac.foodlidays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;


public class Disposer extends FragmentActivity {

    public static SectionsPagerAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.disposer_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!prefs.getBoolean("logged",false))
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentMenu();
                case 1:
                    return new FragmentCard();
                case 2:
                    return new FragmentSettings();
            }
            return null;
        }


        @Override
        public int getCount() {
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    //return addIconToText(getResources().getDrawable(R.drawable.pizza),getResources().getString(R.string.menu));
                    return getString(R.string.menu);
                case 1:
                    //return addIconToText(getResources().getDrawable(R.drawable.panier),getResources().getString(R.string.card));
                    return getString(R.string.card);
                case 2:
                    //return addIconToText(getResources().getDrawable(R.drawable.profile),getResources().getString(R.string.action_settings));
                    return  getString(R.string.profil);
            }
            return null;
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public static CharSequence addIconToText(Drawable d, String s){

        SpannableStringBuilder sb = new SpannableStringBuilder(" " /*+s*/ );
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;

    }
}
