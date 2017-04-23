package kazmina.testapp.translator;

/**
 * интерфейс для взаимодействия фрагментов с активити
 */

public interface FragmentCommunicator {
    void onSelectLangButtonClicked(int viewId, String langValue);
    void onLangSelected(int viewId, String langValue, String langTitle);
}
