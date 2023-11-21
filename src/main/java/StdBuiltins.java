import runtime.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StdBuiltins {
    public static Value.String string_trim(Value.String value) {
        return new Value.String(value.getValue().trim());
    }
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
