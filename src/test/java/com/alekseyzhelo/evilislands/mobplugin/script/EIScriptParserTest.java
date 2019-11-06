package com.alekseyzhelo.evilislands.mobplugin.script;

import com.google.common.base.Stopwatch;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.*;

public class EIScriptParserTest extends ParsingTestCase {

    private static final Logger LOG = LoggerFactory.getLogger(EIScriptParserTest.class);

    public EIScriptParserTest() {
        super("", "eiscript", new EIScriptParserDefinition());
    }

    public void testIngosTownOkrest() {
        doTest(false);
    }

    public void testIngosTownOkrestTime() {
        final int reps = 20;
        int ms = 0;
        for (int i = 0; i < reps; i++) {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            doTestFast();
            ms += stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        }
        LOG.info("Time of execution: {} ms", ms / reps);
    }

    private void doTestFast() {
        String name = getTestName();
        try {
            String text = loadFile(name + "." + myFileExt);
            myFile = createPsiFile(name, text);
            ensureParsed(myFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}