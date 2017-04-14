package kazmina.testapp.translator.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kazmina.testapp.translator.R;

/**
 * @todo: header
 */

public class HistoryFragment extends Fragment implements View.OnClickListener{
    TabLayout mTabLayout;
    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    final String TAG = "HistoryFragment";

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPagerAdapter.addFragment(new HistoryListFragment(), getString(R.string.tab_history));
        mViewPagerAdapter.addFragment(new FavoritesListFragment(), getString(R.string.tab_favorites));
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    public static Fragment getInstance(){
        return new HistoryFragment();
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
