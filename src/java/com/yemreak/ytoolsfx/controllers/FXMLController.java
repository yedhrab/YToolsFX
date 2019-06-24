package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import services.Cache;
import services.Utility;
import services.YoutubeDownloader;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URISyntaxException;


public class FXMLController {

    public static boolean isMouseDragging = false;

    @FXML
    private ImageView iv_drive, iv_info, iv_youtube, ivYoutubeVideoPreview;

    @FXML
    private AnchorPane drivePane, infoPane, youtubePane;

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
    void loadVideoFromClipboard() {
        Image thumbnail = ivYoutubeVideoPreview.getImage();
        Cache.create("thumbnail", thumbnail);

        ivYoutubeVideoPreview.setImage(Utility.createImage("/gifs/loading_spinner.gif"));

        try {
            String url = Utility.getClipboard();
            // YoutubeDownloader.loadVideo(url); // TODO: Bundan dolayı yükleniyor gifi ekrana gelmiyor

            if (YoutubeDownloader.getThumbnail() != null) {
                thumbnail = YoutubeDownloader.getThumbnail();
            }
        } catch (IOException | UnsupportedFlavorException e) {
            e.printStackTrace();
        }

        ivYoutubeVideoPreview.setImage(thumbnail);
        // TODO: burada kalındı
    }

    @FXML
    void onVideoThumbnailClicked() throws IOException, URISyntaxException {
        Utility.openInDefaultBrowser(YoutubeDownloader.getUrl());
    }

    @FXML
    void clearLoadedVideo() {

        YoutubeDownloader.flushData();
        Image thumbnailCache = (Image) Cache.get("thumbnail", null);
        if (thumbnailCache != null) ivYoutubeVideoPreview.setImage(thumbnailCache);
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
