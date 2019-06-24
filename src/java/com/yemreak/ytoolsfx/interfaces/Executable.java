package interfaces;

import java.io.IOException;
import java.util.ArrayList;

public interface Executable {
    ArrayList<String> executeCommand(String[] commands) throws IOException;
}
