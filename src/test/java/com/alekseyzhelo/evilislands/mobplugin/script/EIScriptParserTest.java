package com.alekseyzhelo.evilislands.mobplugin.script;

import com.google.common.base.Stopwatch;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.testFramework.ParsingTestCase;
import com.intellij.testFramework.TestDataFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class EIScriptParserTest extends ParsingTestCase {

    private static final Logger LOG = LoggerFactory.getLogger(EIScriptParserTest.class);

    public EIScriptParserTest() {
        super("", "eiscript", new EIScriptParserDefinition());
    }

    public void testIngosTownOkrest() {
        doTest(false);
    }

    public void testIngosTownOkrestTime() {
        String name = getTestName();
        try {
            String text = loadFile(name + "." + myFileExt);
            final int reps = 500;
            int ms = 0;
            final Stopwatch stopwatch = Stopwatch.createUnstarted();
            for (int i = 0; i < reps; i++) {
                stopwatch.reset();
                stopwatch.start();
                doParse(name, text);
                ms += stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
                System.out.println(myFile.getChildren()[0]);
                myFile = null;
            }
            LOG.info("Time per repetition: {} ms", ms / reps);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doParse(String name, String text) {
        myFile = createPsiFile(name, text);
        ensureParsed(myFile);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testScripts";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

    @Override
    protected String loadFile(@NotNull @TestDataFile String name) throws IOException {
        return FileUtil.loadFile(new File(myFullDataPath, name), "windows-1251", true).trim();
    }
}