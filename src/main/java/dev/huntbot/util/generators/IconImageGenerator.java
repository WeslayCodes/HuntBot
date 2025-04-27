package dev.huntbot.util.generators;

import dev.huntbot.util.graphics.GraphicsUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class IconImageGenerator extends ImageGenerator {
    private final InputStream backgroundImageStream;
    private final String logoImagePath;

    public IconImageGenerator(InputStream backgroundImageStream, String logoImagePath) {
        this.backgroundImageStream = backgroundImageStream;
        this.logoImagePath = logoImagePath;
    }

    @Override
    public IconImageGenerator generate() throws IOException, URISyntaxException {
        this.generatedImage = ImageIO.read(this.backgroundImageStream);
        Graphics2D g2d = this.generatedImage.createGraphics();

        int[] pos = {this.generatedImage.getWidth() / 2, this.generatedImage.getHeight() / 2};
        Image logoImage = GraphicsUtil.getImage(this.logoImagePath);

        pos[0] = pos[0] - logoImage.getWidth(null) / 2;
        pos[1] = pos[1] - logoImage.getHeight(null) / 2;

        GraphicsUtil.drawImage(g2d, this.logoImagePath, pos);

        return this;
    }
}