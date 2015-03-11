package com.innervision.timtac.foodlidays;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import com.innervision.timtac.foodlidays.UtilitiesFunctions.*;


public class Disposer extends FragmentActivity {

    public static SectionsPagerAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;
    public static PagerTabStrip pagerTabStrip;

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

        /*mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                // When swiping between pages, select the
                // corresponding tab.
                getActionBar().setSelectedNavigationItem(position);
            }
        });*/



        pagerTabStrip = (PagerTabStrip)findViewById(R.id.pager_tabs_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.argb(255,154,111,12));
        pagerTabStrip.setTextColor(Color.WHITE);



        /*final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener(){

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.pizza).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.panier).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.profile).setTabListener(tabListener));*/

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
                    return UtilitiesFunctions.addIconToText(getResources().getDrawable(R.drawable.pizza),getResources().getString(R.string.menu));
                case 1:
                    return UtilitiesFunctions.addIconToText(getResources().getDrawable(R.drawable.panier),getResources().getString(R.string.card));
                case 2:
                    return UtilitiesFunctions.addIconToText(getResources().getDrawable(R.drawable.profile),getResources().getString(R.string.action_settings));
            }
            return null;
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pizza:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.panier:
                mViewPager.setCurrentItem(1);
                return true;
            case R.id.settings:
                mViewPager.setCurrentItem(2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
