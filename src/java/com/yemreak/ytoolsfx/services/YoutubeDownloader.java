package services;

import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public abstract class YoutubeDownloader {

    private static final String YOUTUBE_DL_PATH = "/tools/youtube-dl.exe";

    public static final class Mode {
        private static final String FormatList = "-F";
        private static final String Format = "-f";
        private static final String Quite = "-q";
        private static final String Output = "-o";
        private static final String Thumbnail = "--write-thumbnail";
        private static final String SkipDownload = "--skip-download";
        private static final String Update = "-U";
    }

    private static final ArrayList<YData> dataList = new ArrayList<>();
    private static Image thumbnail = null;
    private static String url = null;

    /**
     * Saklanan verileri temizleme
     * TODO: Her yeni veri eklendiğinde buraya da eklenmeli
     */
    public static void flushData() {
        dataList.clear();
        thumbnail = null;
        url = null;
    }

    public static ArrayList<String> getDatas(String type) {
        final ArrayList<String> wantedDataList = new ArrayList<>();
        dataList.forEach(data ->
                wantedDataList.add(
                        switch (type) {
                            case "formatCode" -> data.formatCode;
                            case "extension" -> data.extension;
                            case "type" -> data.type;
                            case "resolution" -> data.resolution;
                            case "size" -> data.size;
                            default -> "";
                        }
                )
        );

        return wantedDataList;
    }

    public static void update() throws IOException {
       executeCommand(Mode.Update);
    }

    public static void loadVideo(String url) throws IOException {
        boolean videoExist = loadVideoInfo(url);
        if (videoExist) {
            loadVideoThumbnail(url);
        }
    }

    // TODO: Thread olmalı, yoksa program kitleniyor
    private static boolean loadVideoInfo(String url) throws IOException {
        String[] commands = {
                Mode.FormatList,
                Utility.safeUrl(url)
        };
        ArrayList<String> outString = executeCommand(commands);

        if (outString.isEmpty()) {
            return false;
        }

        parseOutput(outString);
        return true;
    }

    private static ArrayList<String> executeCommand(String... commands) throws IOException {
        return Utility.executeCommand(Utility.createCommand(YOUTUBE_DL_PATH, commands));
    }

    private static void loadVideoThumbnail(String url) throws IOException {
        String[] commands = {
                Mode.Quite,
                Mode.Output,
                "thumbnail.jpg",
                Mode.Thumbnail,
                Mode.SkipDownload,
                Utility.safeUrl(url)
        };
        executeCommand(commands);

        thumbnail = Utility.getImageFromFile("thumbnail.jpg");

        Utility.deleteFile("thumbnail.jpg");
    }

    // https://www.youtube.com/watch?v=5ANH_jvyOmA
    private static String[] splitOutLine(String outLine) {
        String[] split1 = outLine.split("\\s{2,}");
        String[] split2 = split1[split1.length - 1].split(",");
        ArrayList<String> splits =
                new ArrayList<>(Arrays.asList(split1).subList(0, split1.length - 1));
        Collections.addAll(splits, split2);
        return splits.toArray(new String[0]);
    }

    private static String parseResType(String[] split) {
        String type;
        if (split[2].contains("audio")) {
            type = "audio";
        } else if (split[split.length - 2].contains("video")) {
            type = "video";
        } else {
            type = "full";
        }

        return type;
    }

    private static void parseOutput(ArrayList<String> lines) {
        // Ses ve video seçmezse en uygun kanalı (son input olur, formatsız indirilir) indir.
        // 2 audio değilse 2 (res) ile 3 (144p vs.) 'ü al
        // Son parçayı `,` ile ayır. En sondaki MiB bir öncekinde "video only" yok ise seslidir
        // 0 Format, 1 Ext, 2 Audio değilse res,
        // Audio hariç:  3 pixel,
        // son: MiB son-1: video only değilse tam

        lines.forEach(line -> {
            if (line.contains("MiB")) {
                String[] split = splitOutLine(line);
                String formatCode = split[0];
                String extension = split[1];
                String type = parseResType(split);
                String resolution = split[3].split(" ")[0];
                String size = split[split.length - 1];

                dataList.add(new YData(
                        formatCode,
                        extension,
                        type,
                        resolution,
                        size
                ));
            }
        });

    }

    public static Image getThumbnail() {
        return thumbnail;
    }

    public static String getUrl() {
        return url;
    }

    private static class YData {
        final String formatCode;
        final String extension;
        final String type;
        final String resolution;
        final String size;

        YData(String formatCode, String extension, String type, String resolution, String size) {
            this.formatCode = formatCode;
            this.extension = extension;
            this.type = type;
            this.resolution = resolution;
            this.size = size;
        }
    }
}
