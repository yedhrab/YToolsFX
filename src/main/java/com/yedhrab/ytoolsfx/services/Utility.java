package com.yedhrab.ytoolsfx.services;

import com.yedhrab.ytoolsfx.interfaces.ProcessEvent;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

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

    /**
     * Verilen komutları terminalde çalıştırır.
     *
     * @param pe       Çıktı değiştiğinde yapılackalar
     * @param commands Terminale aktarılacak komutlar
     * @throws IOException Okuma sırasında meydana gelen hata
     */
    public static void executeCommand(ProcessEvent pe, String... commands) throws IOException {
        Process process = new ProcessBuilder(commands).start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output;
        while ((output = stdInput.readLine()) != null) {
            pe.onOutputChanged(output);
        }
    }

    public static String[] createCommand(String prefix, String... commands) {
        ArrayList<String> commandList = new ArrayList<>(Arrays.asList(commands));
        commandList.add(0, prefix);
        return commandList.toArray(String[]::new);
    }

    public static void openInDefaultBrowser(String url) throws URISyntaxException, IOException {
        if (url != null && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        }
    }

    public static String formatDecimal(Double decimal, int digitNum) {
        if (decimal == 0) return "0." + "0".repeat(digitNum - 1);

        int lvl = (int) Math.log10(decimal);
        String format = lvl >= 0 ? "#".repeat(lvl + 1) : "";
        format += lvl - digitNum >= -1 ? "" : "." + "#".repeat(digitNum - lvl - 1);

        return new DecimalFormat(format).format(decimal);
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

    public static String pathJoin(String... paths) {
        return String.join("\\", paths);
    }

    /**
     * Dosya yolu oluşturur
     *
     * @param paths Yol verileri
     * @return Dosyanın yolu (path/to/file.ext)
     */
    public static String createFilePath(String... paths) {
        StringBuilder filePath = new StringBuilder();
        for (int i = 0; i < paths.length - 1; i++) {
            filePath.append(paths[i]);
        }
        filePath.append(".").append(paths[paths.length - 1]);
        return filePath.toString();
    }

    public static String safeUrl(String url) {
        return "\"" + url + "\"";
    }

    public static ArrayList<String> stringSplit(String string, String delimiter) {
        ArrayList<String> split = new ArrayList<>();
        int pos = 0, end;

        while ((end = string.indexOf(delimiter, pos)) >= 0) {
            split.add(string.substring(pos, end));
            pos = end + 1;
        }

        return split;
    }
}
