package cn.z.qrcode.encoder;

/**
 * <h1>QRCode异常</h1>
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
     * @param message 详细信息
     */
    public QRCodeException(String message) {
        super(message);
    }

    /**
     * QRCode异常
     *
     * @param message 详细信息
     * @param cause   原因
     */
    public QRCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * QRCode异常
     *
     * @param cause 原因
     */
    public QRCodeException(Throwable cause) {
        super(cause);
    }

    /**
     * QRCode异常
     *
     * @param message            详细信息
     * @param cause              原因
     * @param enableSuppression  是否启用抑制
     * @param writableStackTrace 堆栈跟踪是否为可写的
     */
    protected QRCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
