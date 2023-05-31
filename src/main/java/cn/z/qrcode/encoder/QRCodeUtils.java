package cn.z.qrcode.encoder;

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
public class QRCodeUtils {

    private QRCodeUtils() {
    }

    /**
     * byte[][]转为boolean[][]
     * <p>0 -> false</p>
     * <p>1 -> true</p>
     *
     * @param bytes     byte[][]
     * @param dimension 尺寸
     * @return boolean[][]
     */
    public static boolean[][] Convert(byte[][] bytes, int dimension) {
        boolean[][] data = new boolean[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (bytes[i][j] == 1) {
                    data[i][j] = true;
                }
            }
        }
        return data;
    }

    /**
     * 添加bit
     *
     * @param bits       目的数据
     * @param pos        位置
     * @param value      值
     * @param numberBits 添加bit个数
     */
    public static void AddBits(boolean[] bits, int pos, int value, int numberBits) {
        for (int i = 0; i < numberBits; i++) {
            bits[pos + i] = (value & (1 << (numberBits - i - 1))) != 0;
        }
    }

    /**
     * 获取bit数组
     *
     * @param value      值
     * @param numberBits 添加bit个数
     * @return bit数组
     */
    public static boolean[] GetBits(int value, int numberBits) {
        boolean[] bits = new boolean[numberBits];
        for (int i = 0; i < numberBits; i++) {
            bits[i] = (value & (1 << (numberBits - i - 1))) != 0;
        }
        return bits;
    }

    /**
     * 获取字节数组
     *
     * @param data   数据
     * @param offset 起始位置
     * @param bytes  字节长度
     * @return 字节数组
     */
    public static int[] GetBytes(boolean[] data, int offset, int bytes) {
        int[] result = new int[bytes];
        for (int i = 0; i < bytes; i++) {
            int ptr = offset + i * 8;
            result[i] = ((data[ptr] ? 0x80 : 0) //
                    | (data[ptr + 1] ? 0x40 : 0) //
                    | (data[ptr + 2] ? 0x20 : 0) //
                    | (data[ptr + 3] ? 0x10 : 0) //
                    | (data[ptr + 4] ? 0x08 : 0) //
                    | (data[ptr + 5] ? 0x04 : 0) //
                    | (data[ptr + 6] ? 0x02 : 0) //
                    | (data[ptr + 7] ? 0x01 : 0) //
            );
        }
        return result;
    }

}
