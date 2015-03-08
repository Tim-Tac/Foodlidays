package com.innervision.timtac.foodlidays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class Disposer extends FragmentActivity {

    public static SectionsPagerAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.disposer_main);

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
            //return "OBJECT " + (position + 1);
            switch (position) {
                case 0:
                    return getString(R.string.menu);
                case 1:
                    return getString(R.string.card);
                case 2:
                    return getString(R.string.action_settings);
            }
            return null;
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
