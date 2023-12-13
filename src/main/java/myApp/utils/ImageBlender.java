package myApp.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageBlender {
    public static Image blendColor(final Image sourceImage, final Color blendColor) {
        if (sourceImage == null) {
            throw new IllegalArgumentException("Source image cannot be null.");
        }

        final double r = blendColor.getRed();
        final double g = blendColor.getGreen();
        final double b = blendColor.getBlue();

        final int w = (int) sourceImage.getWidth();
        final int h = (int) sourceImage.getHeight();

        final WritableImage outputImage = new WritableImage(w, h);
        final PixelWriter writer = outputImage.getPixelWriter();
        final PixelReader reader = sourceImage.getPixelReader();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // Keeping the opacity of every pixel as it is.
                writer.setColor(x, y, new Color(r, g, b, reader.getColor(x, y).getOpacity()));
            }
        }

        return outputImage;
    }
}
