package cn.z.qrcode.encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <h1>二维码工具</h1>
 *
 * <p>
 * createDate 2023/05/29 11:11:11
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class ImageUtils {

    /**
     * 二维码boolean[][]转BufferedImage
     *
     * @param bytes     boolean[][](false白 true黑)
     * @param pixelSize 像素尺寸
     * @return BufferedImage
     */
    public static BufferedImage qrMatrix2Image(boolean[][] bytes, int pixelSize) {
        int length = bytes.length;
        int size = (length + 2) * pixelSize;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLACK);
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                if (bytes[x][y]) {
                    graphics.fillRect((x + 1) * pixelSize, (y + 1) * pixelSize, pixelSize, pixelSize);
                }
            }
        }
        graphics.dispose();
        return image;
    }

    /**
     * 保存为PNG图片
     *
     * @param image BufferedImage
     * @param path  路径
     */
    public static void saveImage(BufferedImage image, String path) throws IOException {
        ImageIO.write(image, "png", new File(path));
    }

}
