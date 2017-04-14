package kazmina.testapp.translator;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import kazmina.testapp.translator.history.HistoryFragment;
import kazmina.testapp.translator.interfaces.FragmentTags;
import kazmina.testapp.translator.translate.TranslateFragment;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


/**
 * тест нижней навигации
 */
@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class BottomNavigationTest implements FragmentTags{
    @Test
    public void clickingHistory_shouldStartHistoryFragment(){
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.findViewById(R.id.navigation_favorites).performClick();
        activity.getSupportFragmentManager().executePendingTransactions();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(SHOW_HISTORY_FRAGMENT_TAG);
        assertNotNull(fragment);
        assertTrue(fragment instanceof HistoryFragment);
        assertNotNull(fragment.getView());
        assertNotNull(fragment.getView().findViewById(R.id.imageButtonDelete));
    }

    @Test
    public void mainActivityOnStart_shouldStartTranslateFragment(){
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.getSupportFragmentManager().executePendingTransactions();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(TRANSLATE_FRAGMENT_TAG);
        Log.d("startTranslateFragment", fragment.toString());
        assertTrue(fragment instanceof TranslateFragment);
    }
}
