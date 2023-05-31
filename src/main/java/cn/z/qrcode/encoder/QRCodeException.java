package cn.z.qrcode.encoder;

/**
 * <h1>QRCode异常类</h1>
 *
 * <p>
 * createDate 2023/05/29 11:11:11
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class QRCodeException extends RuntimeException {

    /**
     * QRCode异常
     */
    public QRCodeException() {
        super();
    }

    /**
     * QRCode异常
     *
     * @param message 信息
     */
    public QRCodeException(String message) {
        super(message);
    }

}
