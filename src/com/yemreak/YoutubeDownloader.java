package com.yemreak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class YoutubeDownloader {
    private static final ArrayList<YData> dataList = new ArrayList<>();

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

    static ArrayList<String> getDatas(String type) {
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

    static void loadVideoInfo(String url) throws IOException {
        ArrayList<String> outString = Utility.executeCommand("youtube-dl -F " + url);
        parseOutput(outString);
    }

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

    private static void parseOutput(ArrayList<String>  lines) {
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
}
