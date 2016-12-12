package tw.edu.ntust.stockoraclet;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by henrychong on 2016/9/19.
 */
public class testMain extends AppCompatActivity {
    private TabLayout mTabLayout;

    private int[] mTabsIcons = {
            R.drawable.info,
            R.drawable.graph,
            R.drawable.question2,
            R.drawable.comment,
            R.drawable.subscribe
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oraclet_page_activity_new);

        ViewPager viewPager = (ViewPager)findViewById(R.id.tabView_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        if(viewPager != null){
            viewPager.setAdapter(pagerAdapter);
        }

        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        if(mTabLayout != null){
            mTabLayout.setupWithViewPager(viewPager);

            for(int i= 0; i <mTabLayout.getTabCount(); i++){
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if(tab != null){
                    tab.setCustomView(pagerAdapter.getTabView(i));
                }
            }
            mTabLayout.getTabAt(0).getCustomView().setSelected(true);
        }


    }

    private class MyPagerAdapter extends FragmentPagerAdapter{
        public final int PAGE_COUNT = 4;
        private final String[] mTabsTitle = {"Info","Graph","Contradiction","Comment","Subscribe"};
        public MyPagerAdapter(android.support.v4.app.FragmentManager fm){
            super(fm);
        }



        public View getTabView(int position){
            View view = LayoutInflater.from(testMain.this).inflate(R.layout.custom_tab, null);
            TextView title = (TextView) view.findViewById(R.id.tabTitle);
            title.setText((mTabsTitle[position]));
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(mTabsIcons[position]);
            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos){
                case 0:
                    return PageFragment.newInstance(1);
                case 1:
                    return PageFragment.newInstance(2);
                case 2:
                    return PageFragment.newInstance(3);
                case 3:
                    return PageFragment.newInstance(4);
            }
            return null;
        }


        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }
}
