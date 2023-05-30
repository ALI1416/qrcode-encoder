package cn.z.qrcode.encoder;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * <h1>二维码测试</h1>
 *
 * <p>
 * createDate 2023/05/29 11:11:11
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
class QRCodeTest {

    /**
     * 测试
     */
    @Test
    void test() throws IOException {
        String content = "123Ab";
        int level = 1;
        int mode = 3;
        int versionNumber = 2;
        String path = "E:/1.png";
        QRCode qrCode = new QRCode(content, level, mode, versionNumber);
        BufferedImage image = ImageUtils.qrMatrix2Image(qrCode.Matrix, 10);
        ImageUtils.saveImage(image, path);
    }

}
