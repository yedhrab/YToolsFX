package services;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public abstract class YoutubeDownloader {

    private static final String YOUTUBE_DL_PATH = "/tools/youtube-dl.exe";

    private static final ArrayList<YData> availableDataList = new ArrayList<>();
    private static final ArrayList<YData> dataList = new ArrayList<>();

    private static Image thumbnail = null;
    private static String url = null;

    private static boolean downloadAudio = true;
    private static boolean downloadVideo = true;
    private static double qualityRatio = 1;

    private static String selectedFormat = null;
    private static int selectedAvailableIndex = -1;

    /**
     * Saklanan verileri temizleme
     * TODO: Her yeni veri eklendiğinde buraya da eklenmeli
     */
    public static void flushData() {
        availableDataList.clear();
        dataList.clear();

        thumbnail = null;
        url = null;

        downloadAudio = true;
        downloadVideo = true;
        qualityRatio = 1;

        selectedFormat = null;
        selectedAvailableIndex = -1;
    }

    public static void setController(CheckBox audio, CheckBox video, Slider quality, Label fileSize) {
        onCheckBoxChanged("audip", audio.isSelected(), quality, fileSize);

        audio.selectedProperty().addListener(
                (observableValue, oldValue, newValue) -> onCheckBoxChanged("audio", newValue, quality, fileSize)
        );

        video.selectedProperty().addListener(
                (observableValue, oldValue, newValue) -> onCheckBoxChanged("video", newValue, quality, fileSize)
        );
        quality.valueProperty().addListener((observableValueQ, oldValue, newValue) -> {
            qualityRatio = newValue.doubleValue() / quality.getMax();
            selectFormat();
            fileSize.setText(getFileSize());
        });
    }

    private static void onCheckBoxChanged(String type, boolean value, Slider quality, Label fileSize) {
        if (type.equals("audio")) {
            downloadAudio = value;
        } else if (type.equals("video")) {
            downloadVideo = value;
        }

        updateAvailableList();
        setUpSlider(quality);
        selectFormat();
        fileSize.setText(getFileSize());
    }

    private static void setUpSlider(Slider quality) {
        if (availableDataList.size() <= 1) {
            quality.setDisable(true);
        } else {
            quality.setDisable(false);
            quality.setMax(availableDataList.size());
            quality.setValue(availableDataList.size() * qualityRatio);
        }
    }

    private static void updateAvailableList() {
        String type = determineDataType();

        availableDataList.clear();
        dataList.forEach(data -> {
            if (data.type.equals(type)) availableDataList.add(data);
        });
    }

    private static void selectFormat() {
        if (availableDataList.isEmpty()) {
            selectedFormat = null;
            selectedAvailableIndex = -1;
        } else {
            int index = (int) ((availableDataList.size() - 1) * qualityRatio);
            selectedFormat = availableDataList.get(index).formatCode;
            selectedAvailableIndex = index;
        }
    }

    private static void selectFormat(double qualityRatio) {
        if (availableDataList.isEmpty()) {
            selectedFormat = null;
            selectedAvailableIndex = -1;
        } else {
            int index = (int) ((availableDataList.size() - 1) * qualityRatio);
            selectedFormat = availableDataList.get(index).formatCode;
            selectedAvailableIndex = index;
        }
    }

    public static String getFileSize(boolean downloadAudio, boolean downloadVideo, double qualityRatio) {
        YoutubeDownloader.downloadAudio = downloadAudio;
        YoutubeDownloader.downloadVideo = downloadVideo;

        updateAvailableList();
        selectFormat(qualityRatio);
        return getFileSize();
    }

    private static String getFileSize() {
        if (selectedAvailableIndex == -1) return "~ MB";
        return availableDataList.get(selectedAvailableIndex).size;
    }

    private static String determineDataType() {
        String type;
        if (downloadAudio) {
            if (downloadVideo) {
                type = "full";
            } else {
                type = "audio";
            }
        } else {
            if (downloadVideo) {
                type = "video";
            } else {
                type = null;
            }
        }

        return type;
    }

    public static void download(ProgressIndicator progressIndicator, Label installationSize) throws IOException {
        progressIndicator.setVisible(true);
        // TODO: pipeline gerekli
        // TODO: iptal edilebilir olmalı
        ArrayList<String> output = executeCommand(
                Mode.Format,
                selectedFormat,
                Mode.Output,
                "temp." + availableDataList.get(selectedAvailableIndex).extension,
                Utility.safeUrl(url)
        );

        DownloadData data = parseDownloadOutput(output);

        Platform.runLater(() -> {
            progressIndicator.setProgress(data.ratio);
            installationSize.setText(String.valueOf(data.size));
            // TODO: yükleme hızı
        });


//        executeCommand(
//                Mode.Format,
//                selectedFormat,
//                Mode.Quite,
//                Utility.safeUrl(url)
//        );
    }

    private static DownloadData parseDownloadOutput(ArrayList<String> downloadOutput) {
        String[] downloadInfo = downloadOutput.get(downloadOutput.size() - 1).split(" ");

        double ratio = 0, size = 0, speed = 0;
        for (String info : downloadInfo) {
            String value;
            if (info.contains("%")) {
                value = info.replace("%", "");
                ratio = Double.parseDouble(value);
            } else if (info.contains("MiB")) {
                if (info.contains("/s")) {
                    value = info.replace("Mib/s", "");
                    speed = Double.parseDouble(value);
                } else {
                    value = info.replace("MiB", "");
                    size = Double.parseDouble(value);
                }
            }
        }

        return new DownloadData(ratio, size, speed);
    }

    private static class DownloadData {
        final double ratio;
        final double size;
        final double speed;

        DownloadData(double ratio, double size, double speed) {
            this.ratio = ratio;
            this.size = size;
            this.speed = speed;
        }
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
            YoutubeDownloader.url = url;
        }
    }

    // https://www.youtube.com/watch?v=5ANH_jvyOmA
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

    private static final class Mode {
        private static final String FormatList = "-F";
        private static final String Format = "-f";
        private static final String Quite = "-q";
        private static final String Output = "-o";
        private static final String Thumbnail = "--write-thumbnail";
        private static final String SkipDownload = "--skip-download";
        private static final String Update = "-U";
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
