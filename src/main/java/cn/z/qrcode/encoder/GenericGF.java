package cn.z.qrcode.encoder;

/**
 * <h1>通用Galois Fields域(通用伽罗华域)</h1>
 *
 * <p>仅适用于QrCode</p>
 *
 * <p>
 * createDate 2023/05/29 11:11:11
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class GenericGF {

    /**
     * 维度
     * <p>256</p>
     */
    private static final int DIMENSION = 256;
    /**
     * 多项式
     * <p>0x011D -> 0000 0001 0001 1101 -> x^8 + x^4 + x^3 + x^2 + 1</p>
     */
    private static final int POLY = 0x011D;

    /**
     * 指数表
     */
    private static final int[] ExpTable;
    /**
     * 对数表
     */
    private static final int[] LogTable;

    static {
        // 初始化指数表和对数表
        ExpTable = new int[DIMENSION];
        LogTable = new int[DIMENSION];
        int x = 1;
        for (int i = 0; i < DIMENSION; i++) {
            ExpTable[i] = x;
            x <<= 1;
            if (x >= DIMENSION) {
                x ^= POLY;
                x &= DIMENSION - 1;
            }
        }
        for (int i = 0; i < DIMENSION - 1; i++) {
            LogTable[ExpTable[i]] = i;
        }
    }

    private GenericGF() {
    }

    /**
     * 加法
     *
     * @param a 加数
     * @param b 被加数
     * @return 结果
     */
    public static int Addition(int a, int b) {
        return a ^ b;
    }

    /**
     * 2的次方
     *
     * @param a 幂
     * @return 结果
     */
    public static int Exp(int a) {
        return ExpTable[a];
    }

    /**
     * 逆运算
     *
     * @param a 被操作数
     * @return 结果
     */
    public static int Inverse(int a) {
        return ExpTable[DIMENSION - LogTable[a] - 1];
    }

    /**
     * 乘法
     *
     * @param a 乘数
     * @param b 被乘数
     * @return 结果
     */
    public static int Multiply(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return ExpTable[(LogTable[a] + LogTable[b]) % (DIMENSION - 1)];
    }

}
