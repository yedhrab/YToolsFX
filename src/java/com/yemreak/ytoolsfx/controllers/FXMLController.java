package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import services.Utility;
import services.YoutubeDownloader;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URISyntaxException;


public class FXMLController {

    public static boolean isMouseDragging = false;

    private static Thread thread = null;

    private boolean videoLoading = false;
    private boolean controlExist = false;

    @FXML
    private ImageView iv_drive, iv_info, iv_youtube, ivYoutubeVideoPreview, ivDownloadVideo;

    @FXML
    private Label labelFileSize, labelInstallationSize;

    @FXML
    private CheckBox cbAudio, cbVideo;

    @FXML
    private Button buttonDownload;

    @FXML
    private Slider sliderQuality;

    @FXML
    private AnchorPane drivePane, infoPane, youtubePane, youtubeVideoSettingsPane;

    @FXML
    private AnchorPane paneProgress;

    @FXML
    private ProgressIndicator downloadProgress;

    @FXML
    private TextField txt_gDriveLink, txt_directLink;


    /**
     * Tek bir panel gösterme yapısını sağlar
     * TODO: Her panel, eklendiğinde buraya da eklenmeli
     *
     * @param pane Gösterilecek panel
     */
    private void hideAllPaneExceptOne(AnchorPane pane) {
        boolean isVisible = pane == null || pane.isVisible();

        drivePane.setVisible(false);
        infoPane.setVisible(false);
        youtubePane.setVisible(false);

        if (!isVisible) pane.setVisible(true);
    }

    /**
     * Panellerin üstündeki ikonlara basıldığında yapılacak eylemeler
     * TODO: Her ikon, eklendiğinde buraya da eklenmeli
     *
     * @param event
     */
    @FXML
    void handleIconClick(MouseEvent event) {
        if (isMouseDragging) return;

        ImageView iv = (ImageView) event.getTarget();
        if (iv_drive.equals(iv)) {
            hideAllPaneExceptOne(drivePane);
        } else if (iv_info.equals(iv)) {
            hideAllPaneExceptOne(infoPane);
        } else if (iv_youtube.equals(iv)) {
            hideAllPaneExceptOne(youtubePane);
            // TODO: Bunun yerine kapatma butonuna koy, basılı tutulursa program kapatılsın
        } else {
            hideAllPaneExceptOne(null);
        }
    }

    @FXML
    void clearGDirectLink() {
        txt_gDriveLink.setText("");
    }

    @FXML
    void closeApplication() {
        System.exit(0);
    }

    @FXML
    void onClipboardIconClicked() {
        Utility.putClipboard(txt_directLink.getText());
    }

    @FXML
    void downloadVideo() {
        thread = new Thread(() -> {
            try {
                if (YoutubeDownloader.getUrl() == null) return;

                buttonDownload.setVisible(false);
                paneProgress.setVisible(true);
                YoutubeDownloader.download(downloadProgress, labelInstallationSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @FXML
    void loadVideoFromClipboard() {
        if (videoLoading) return;

        videoLoading = true;
        ivYoutubeVideoPreview.setImage(Utility.createImage("/gifs/loading_spinner.gif"));

        thread = new Thread(() -> {
            try {
                String url = "https://www.youtube.com/watch?v=5ANH_jvyOmA"; // Utility.getClipboard();
                YoutubeDownloader.loadVideo(url);

                Image thumbnail;
                if (YoutubeDownloader.getThumbnail() != null) {
                    thumbnail = YoutubeDownloader.getThumbnail();
                    youtubeVideoSettingsPane.setDisable(false);
                    if (!controlExist) {
                        Platform.runLater(() -> {
                            YoutubeDownloader.setController(cbAudio, cbVideo, sliderQuality, labelFileSize);
                            controlExist = true;
                        });
                    }
                } else {
                    thumbnail = Utility.createImage("/images/video_not_found.png");
                    youtubeVideoSettingsPane.setDisable(true);
                }

                ivYoutubeVideoPreview.setImage(thumbnail);

            } catch (IOException e) {
                e.printStackTrace();
            }

            videoLoading = false;
        });
        thread.start();
    }

    @FXML
    void onVideoThumbnailClicked() throws IOException, URISyntaxException {
        System.out.println(YoutubeDownloader.getUrl());
        Utility.openInDefaultBrowser(YoutubeDownloader.getUrl());
    }

    @FXML
    void clearLoadedVideo() {
        if (thread != null) thread.interrupt();

        YoutubeDownloader.flushData();
        ivYoutubeVideoPreview.setImage(Utility.createImage("/images/video_thumbnail.png"));
        labelFileSize.setText("~ MB");
        youtubeVideoSettingsPane.setDisable(true);
    }

    @FXML
    void onQuickIconClicked() throws IOException, UnsupportedFlavorException {
        String link = Utility.getClipboard();
        link = Utility.makeLinkDirect(link);
        Utility.putClipboard(link);
    }


    @FXML
    void createDirectLink() {
        txt_directLink.setText(Utility.makeLinkDirect(txt_gDriveLink.getText()));
    }


}
