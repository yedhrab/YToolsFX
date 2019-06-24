package services;

import javafx.scene.image.Image;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public abstract class Utility {

    public static void putClipboard(String clipboardString) {
        StringSelection stringSelection = new StringSelection(clipboardString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static String getClipboard() throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (String) clipboard.getData(DataFlavor.stringFlavor);
    }

    public static String makeLinkDirect(String link) {
        return link.replace("open?", "uc?export=download&");
    }

    public static ArrayList<String> executeCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

        final ArrayList<String> lines = new ArrayList<>();

        String line;
        while ((line = stdInput.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    public static void openInDefaultBrowser(String url) throws URISyntaxException, IOException {
        if (url != null && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        }
    }

    public static Image createImage(String filePath) {
        return new Image(filePath, true);
    }

    public static Image getImageFromFile(String filePath) {
        File file = new File(filePath);
        return new Image(file.toURI().toString());
    }

    public static boolean deleteFile(String filePath) {
        return new File(filePath).delete();
    }
}
