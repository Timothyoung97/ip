package bot.assembly.error;

import java.io.IOException;

public class InvalidFileException extends IOException {
    public InvalidFileException(String msg) {
        super(msg);
    }
}
