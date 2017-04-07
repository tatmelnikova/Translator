package kazmina.testapp.translator;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kazmina.testapp.translator.navigation.BottomNavigationListener;

/**
 * история переводов
 */

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "HistoryActivity";

    TabLayout mTabLayout;
    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;

    private BottomNavigationListener mBottomNavigationListener = new BottomNavigationListener(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_favorites);
        navigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new HistoryListFragment(), getString(R.string.tab_history));
        mViewPagerAdapter.addFragment(new FavoritesListFragment(), getString(R.string.tab_favorites));
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, v.toString());
        switch (v.getId()){
            case R.id.imageButtonDelete:
                deleteHistoryList();
                break;
            default:
                break;
        }
    }

    public void deleteHistoryList(){
        Log.d(TAG, "currentItem=" +mViewPager.getCurrentItem());
        HistoryListFragment fragment = mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
        fragment.clearList();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<HistoryListFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public HistoryListFragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(HistoryListFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
