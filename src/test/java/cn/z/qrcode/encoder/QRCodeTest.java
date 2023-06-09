package cn.z.qrcode.encoder;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.awt.image.BufferedImage;

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
    void test() throws Exception {
        String content = "1234😀";
        int level = 0;
        int mode = 3;
        int versionNumber = 1;
        String path = "./target/1.png";
        QRCode qr = new QRCode(content, level, mode, versionNumber);
        log.info("Mode {}", qr.Mode);
        log.info("VersionNumber {}", qr.VersionNumber);
        log.info("MaskPatternNumber {}", qr.MaskPatternNumber);
        BufferedImage image = ImageUtils.qrMatrix2Image(qr.Matrix, 10);
        ImageUtils.saveImage(image, path);
        assert qr.Mode == 3;
        assert qr.VersionNumber == 1;
        assert qr.MaskPatternNumber == 3;
    }

}
