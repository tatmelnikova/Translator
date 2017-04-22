package kazmina.testapp.translator;

import org.junit.Assert;
import org.junit.Test;

import kazmina.testapp.translator.utils.CommonUtils;


/**
 *
 */

public class CommonUtilsTest {
    @Test
    public void trimString_shouldTrimOnlyBeginningAndClosingCharachters(){
        String beforeTrim = "\r\n\r\nQWER\r\n\r\n";
        String afterTrim = CommonUtils.trimString(beforeTrim);
        Assert.assertEquals(afterTrim, "QWER");

        beforeTrim = " \r\nQWER\r\n ";
        afterTrim = CommonUtils.trimString(beforeTrim);
        Assert.assertEquals(afterTrim, "QWER");

        beforeTrim = "\r\n QWER \r\n";
        afterTrim = CommonUtils.trimString(beforeTrim);
        Assert.assertEquals(afterTrim, "QWER");

        beforeTrim = "\r\nQWER\r\nQWER\r\n";
        afterTrim = CommonUtils.trimString(beforeTrim);
        Assert.assertEquals(afterTrim, "QWER\r\nQWER");

    }

    @Test
    public void isEmpty_shouldReturnEmptyIfStringContainsOnlyBlankCharacters(){
        String testString = "qwer";
        boolean isEmpty = CommonUtils.stringIsEmpty(testString);
        Assert.assertFalse(isEmpty);

        testString = " qwer";
        isEmpty = CommonUtils.stringIsEmpty(testString);
        Assert.assertFalse(isEmpty);

        testString = " ";
        isEmpty = CommonUtils.stringIsEmpty(testString);
        Assert.assertTrue(isEmpty);

        testString = "\r\n";
        isEmpty = CommonUtils.stringIsEmpty(testString);
        Assert.assertTrue(isEmpty);

        testString = "\r\n \r\n\t";
        isEmpty = CommonUtils.stringIsEmpty(testString);
        Assert.assertTrue(isEmpty);
    }

}
