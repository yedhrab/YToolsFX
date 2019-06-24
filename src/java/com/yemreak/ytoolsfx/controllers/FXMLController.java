package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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

    private boolean videoLoading = false;
    private boolean controlExist = false;

    @FXML
    private ImageView iv_drive, iv_info, iv_youtube, ivYoutubeVideoPreview, ivDownloadVideo;

    @FXML
    private Label labelFileSize;

    @FXML
    private CheckBox cbAudio, cbVideo;

    @FXML
    private Slider sliderQuality;

    @FXML
    private AnchorPane drivePane, infoPane, youtubePane, youtubeVideoSettingsPane;

    @FXML
    private TextField txt_gDriveLink, txt_directLink;

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
        YoutubeDownloader.downloadVideo();
        // TODO: new Thread()
    }

    @FXML
    void loadVideoFromClipboard() {
        if (videoLoading) return;

        videoLoading = true;
        ivYoutubeVideoPreview.setImage(Utility.createImage("/gifs/loading_spinner.gif"));

        new Thread(() -> {
            try {
                String url = Utility.getClipboard();
                YoutubeDownloader.loadVideo(url);

                Image thumbnail;
                if (YoutubeDownloader.getThumbnail() != null) {
                    thumbnail = YoutubeDownloader.getThumbnail();
                    youtubeVideoSettingsPane.setDisable(false);
                    if (!controlExist) {
                        YoutubeDownloader.setController(cbAudio, cbVideo, sliderQuality, labelFileSize);
                        controlExist = true;
                    }
                    Platform.runLater(() -> labelFileSize.setText(YoutubeDownloader.getFileSize(
                            cbAudio.isSelected(), cbVideo.isSelected(),
                            sliderQuality.getValue() / sliderQuality.getMax()
                    )));
                } else {
                    thumbnail = Utility.createImage("/images/video_not_found.png");
                    youtubeVideoSettingsPane.setDisable(true);
                }

                ivYoutubeVideoPreview.setImage(thumbnail);

            } catch (IOException | UnsupportedFlavorException e) {
                e.printStackTrace();
            }

            videoLoading = false;
        }).start();
    }

    @FXML
    void onVideoThumbnailClicked() throws IOException, URISyntaxException {
        System.out.println(YoutubeDownloader.getUrl());
        Utility.openInDefaultBrowser(YoutubeDownloader.getUrl());
    }

    @FXML
    void clearLoadedVideo() {
        YoutubeDownloader.flushData();
        ivYoutubeVideoPreview.setImage(Utility.createImage("/images/video_thumbnail.png"));
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

}
