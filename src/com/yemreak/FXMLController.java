package com.yemreak;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;


public class FXMLController {

    static boolean isMouseDragging = false;

    private Image oldImage;

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
    void loadVideoFromClipboard() throws IOException, UnsupportedFlavorException {
        String url = Utility.getClipboard();
        YoutubeDownloader.loadVideoInfo(url);
        String filename = YoutubeDownloader.downloadVideoThumbnail(url);
        ivYoutubeVideoPreview.setLayoutX(94);
        ivYoutubeVideoPreview.setLayoutY(128);
        oldImage = ivYoutubeVideoPreview.getImage();
        ivYoutubeVideoPreview.setImage(Utility.getImageFromFile(filename));
        Utility.deleteFile(filename);
        // TODO: burada kalındı
    }

    @FXML
    void cleanLoadedVideo() {

        YoutubeDownloader.flushData();
        if (oldImage != null) ivYoutubeVideoPreview.setImage(oldImage);
    }

    @FXML
    void onQuickIconClicked() throws IOException, UnsupportedFlavorException {
        String link = Utility.getClipboard();
        link = Utility.makeLinkDirect(link);
        Utility.putClipboard(link);
    }


    @FXML
    void createDirectLink() {
        System.out.println("createDirectLink");
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
