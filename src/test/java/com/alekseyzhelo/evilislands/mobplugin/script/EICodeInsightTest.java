package com.alekseyzhelo.evilislands.mobplugin.script;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class EICodeInsightTest extends BasePlatformTestCase {

    public void testAnnotator() {
        myFixture.configureByFile("IngosTownOkrest.eiscript");
//        int reps = 100;
//        for (int i = 0; i < reps; i++) {
//            List<HighlightInfo> highlightInfos = myFixture.doHighlighting();
//            System.out.println(highlightInfos.get(0).getDescription());
//        }
        long time = myFixture.checkHighlighting(false, false, true, true);
        System.out.println("Highlighting time: " + time);
    }


    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testScripts";
    }

}
