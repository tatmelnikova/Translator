package kazmina.testapp.translator;

/**
 * @todo: header
 */

public interface FragmentCommunicator {
    void onSelectLangButtonClicked(int viewId, String langValue);
    void onLangSelected(int viewId, String langValue, String langTitle);
}
