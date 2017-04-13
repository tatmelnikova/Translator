package kazmina.testapp.translator.navigation;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


import kazmina.testapp.translator.MainActivity;
import kazmina.testapp.translator.R;

/**
 * обработчик нижней навигации
 */

public class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
    private AppCompatActivity mContext;
    public BottomNavigationListener(AppCompatActivity context) {
        super();
        mContext = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                intent = new Intent(mContext.getBaseContext(), MainActivity.class);
                mContext.startActivity(intent);
                return true;
            case R.id.navigation_favorites:



                return true;
            case R.id.navigation_settings:

                return true;
        }
        return false;
    }
}
