package services;

import interfaces.ProcessEvent;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public abstract class YoutubeDownloader {

    private static final String YOUTUBE_DL_PATH = "/tools/youtube-dl.exe";
    private static final String OUT_PATH = System.getProperty("user.home") + "\\Downloads\\";

    private static final ArrayList<YData> availableDataList = new ArrayList<>();
    private static final ArrayList<YData> dataList = new ArrayList<>();

    private static String videoName = null;
    private static Image thumbnail = null;
    private static String url = null;

    private static boolean videoLoad = false;

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

        videoName = null;
        thumbnail = null;
        url = null;

        videoLoad = false;

        downloadAudio = true;
        downloadVideo = true;
        qualityRatio = 1;

        selectedFormat = null;
        selectedAvailableIndex = -1;
    }

    public static void setController(CheckBox audio, CheckBox video, Slider quality, Label fileSize) {
        onCheckBoxChanged("audio", audio.isSelected(), quality, fileSize);

        audio.selectedProperty().addListener(
                (observableValue, oldValue, newValue) -> onCheckBoxChanged("audio", newValue, quality, fileSize)
        );

        video.selectedProperty().addListener(
                (observableValue, oldValue, newValue) -> onCheckBoxChanged("video", newValue, quality, fileSize)
        );
        quality.valueProperty().addListener((observableValueQ, oldValue, newValue) -> {
            qualityRatio = newValue.doubleValue() / quality.getMax();
            selectFormat();
            Platform.runLater(() -> fileSize.setText(getFileSize()));
        });
    }

    public static void prepareToDownload(CheckBox audio, CheckBox video, Slider quality, Label fileSize) {
        downloadAudio = audio.isSelected();
        downloadVideo = video.isSelected();

        updateAvailableList();
        setUpSlider(quality);
        selectFormat();
        Platform.runLater(() -> fileSize.setText(getFileSize()));
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
        Platform.runLater(() -> fileSize.setText(getFileSize()));
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

    public static boolean isDownloadable() {
        return url != null && selectedAvailableIndex >= 0;
    }

    public static void download(DownloadEvent de, ProgressIndicator progressIndicator, Label installationSize) throws IOException {
        // TODO: iptal edilebilir olmalı

        String[] commands = {
                Mode.Format,
                selectedFormat,
                Mode.Output,
                Utility.createFilePath(OUT_PATH, videoName, availableDataList.get(selectedAvailableIndex).extension),
                Utility.safeUrl(url)
        };

        executeCommand((output -> {
            if (isDownloadOutput(output)) {
                DownloadData data = parseDownloadOutput(output);
                Platform.runLater(() -> {
                    progressIndicator.setProgress(data.ratio);
                    installationSize.setText(Utility.formatDecimal(data.size, 4));
                    // TODO: yükleme hızı
                });
            }
        }), commands);

        de.onDownloadFinished();
    }

    private static boolean isDownloadOutput(String downloadOutput) {
        return downloadOutput.contains("[download]");
    }

    private static DownloadData parseDownloadOutput(String downloadOutput) {
        String[] downloadInfo = downloadOutput.split(" ");

        double ratio = 0, size = 0, speed = 0;
        for (String info : downloadInfo) {
            String value;
            if (info.contains("%")) {
                value = info.replace("%", "");
                ratio = Double.parseDouble(value) / 100;
            } else if (info.contains("MiB")) {
                if (info.contains("/s")) {
                    value = info.replace("MiB/s", "");
                    speed = Double.parseDouble(value);
                } else {
                    value = info.replace("MiB", "");
                    size = Double.parseDouble(value);
                }
            }
        }
        size = ratio * size;

        return new DownloadData(ratio, size, speed);
    }

    public static ArrayList<String> getDatas(String type) {
        final ArrayList<String> wantedDataList = new ArrayList<>();
        dataList.forEach(data -> wantedDataList.add(
                type.equals("formatCode") ? data.formatCode :
                        type.equals("extension") ? data.extension :
                                type.equals("type") ? data.type :
                                        type.equals("resolution") ? data.resolution :
                                                type.equals("size") ? data.size : ""
        ));

        return wantedDataList;
    }

    public static void update(ProcessEvent pe) throws IOException {
        executeCommand(pe, Mode.Update);
    }

    public static void loadVideo(String url) throws IOException {
        loadVideoInfo(url);
        if (videoLoad) {
            loadVideoThumbnail(url);
            YoutubeDownloader.url = url;
        }
    }

    private static void loadVideoInfo(String url) throws IOException {
        String[] commands = {
                Mode.FormatList,
                Utility.safeUrl(url)
        };

        executeCommand(output -> {
            videoLoad = true;
            parseVideoFormat(output);
        }, commands);
    }

    private static void executeCommand(ProcessEvent event, String... commands) throws IOException {
        Utility.executeCommand(event, Utility.createCommand(YOUTUBE_DL_PATH, commands));
    }

    private static void loadVideoThumbnail(String url) throws IOException {
        String[] commands = {
                Mode.Thumbnail,
                Mode.SkipDownload,
                Utility.safeUrl(url)
        };

        executeCommand((output) -> {
            if (output.contains("Writing thumbnail to:")) {
                videoName = "";
                String[] split = output.split("Writing thumbnail to: ")[1].split("\\.");
                for (int i = 0; i < split.length - 1; i++) {
                    videoName = videoName.concat(split[i]);
                }
            }
        }, commands);

        thumbnail = Utility.getImageFromFile(videoName + ".jpg");
        Utility.deleteFile(videoName + ".jpg");
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

    private static void parseVideoFormat(String output) {
        if (output.contains("MiB")) {
            String[] split = splitOutLine(output);
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
    }

    public static Image getThumbnail() {
        return thumbnail;
    }

    public static String getUrl() {
        return url;
    }

    public static String getVideoName() {
        return videoName;
    }

    @FunctionalInterface
    public interface DownloadEvent {
        void onDownloadFinished();
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

        @Override
        public String toString() {
            return String.format("Ratio: %f, Size: %f, Speed: %f", ratio, size, speed);
        }
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
