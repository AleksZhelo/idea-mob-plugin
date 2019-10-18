package com.alekseyzhelo.evilislands.mobplugin.script;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

// TODO: PairedBraceMatcher
// TODO: ParameterInfoHandler?
// TODO: LineMarkerProvider?
// TODO: something like https://github.com/sylvanaar/IDLua/blob/idea16/src/lang/psi/LuaPsiManager.java?
// TODO: thread-safety everywhere, Read/Write-Actions?
public class EIScriptLanguage extends Language {
    public static final EIScriptLanguage INSTANCE = new EIScriptLanguage();

    private EIScriptLanguage() {
        super("EIScriptLanguage");
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "EIScript";
    }
}

//    static String readFile(String path, Charset encoding)
//            throws IOException {
//        byte[] encoded = Files.readAllBytes(Paths.get(path));
//        return new String(encoded, encoding);
//    }
//
//    /**
//     * Runs the scanner on input files.
//     *
//     * This main method is the debugging routine for the scanner.
//     * It prints debugging information about each returned token to
//     * System.out until the end of file is reached, or an error occurred.
//     *
//     * @param argv   the command line, contains the filenames to run
//     *               the scanner on.
//     */
//    public static void main(String argv[]) {
//        if (argv.length == 0) {
//            System.out.println("Usage : java EIScriptLexer <inputfile>");
//        }
//        else {
//            for (int i = 0; i < argv.length; i++) {
//                try {
//                    FlexAdapter scanner = new FlexAdapter(new EIScriptLexer((Reader) null));
//                    String file = readFile(argv[i], Charset.forName("UTF-8"));
//                    scanner.start(file);
//                    Object token = null;
//                    do {
//                        token = scanner.getTokenType();
//                        System.out.println(token);
//                        scanner.advance();
//                    } while (token != null);
//
//                }
//                catch (java.io.FileNotFoundException e) {
//                    System.out.println("File not found : \""+argv[i]+"\"");
//                }
//                catch (java.io.IOException e) {
//                    System.out.println("IO error scanning file \""+argv[i]+"\"");
//                    System.out.println(e);
//                }
//                catch (Exception e) {
//                    System.out.println("Unexpected exception:");
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
