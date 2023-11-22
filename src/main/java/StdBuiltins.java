import runtime.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StdBuiltins {
    public static String string_trim(String value) {
        return value.trim();
    }
    public static String string_replace(String original, String from, String to) { return original.replace(from, to); }
    public static String string_length(String value) { return value.length(); }
    public static String fs_readFile(String name) {
        try {
            return String.join("\n", Files.readAllLines(Path.of(name)));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void fs_writeFile(String name, String contents) {
        try {
            Files.write(Path.of(name), contents.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
