package cn.z.qrcode.encoder;

/**
 * <h1>Reed-Solomon(里德-所罗门码)</h1>
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
public class ReedSolomon {

    /**
     * 多项式0
     */
    public static final GenericGFPoly Zero = new GenericGFPoly(new int[]{0});
    /**
     * GenericGFPoly数组
     */
    private static final GenericGFPoly[] GenericGFPolyArray = new GenericGFPoly[69];

    static {
        // 初始化GenericGFPoly数组
        GenericGFPolyArray[0] = new GenericGFPoly(new int[]{1});
        // 最大值68
        // 数据来源 ISO/IEC 18004-2015 -> Annex A -> Table A.1 -> Number of error correction codewords列最大值
        for (int i = 1; i < 69; i++) {
            GenericGFPolyArray[i] = GenericGFPolyArray[i - 1].Multiply(new GenericGFPoly(new int[]{1,
                    GenericGF.Exp(i - 1)}));
        }
    }

    /**
     * 编码
     *
     * @param coefficients 系数
     * @param degree       次数
     * @return 结果
     */
    public static int[] Encoder(int[] coefficients, int degree) {
        GenericGFPoly info = new GenericGFPoly(coefficients);
        info = info.MultiplyByMonomial(degree, 1);
        GenericGFPoly remainder = info.RemainderOfDivide(GenericGFPolyArray[degree]);
        // 纠错码
        int[] result = remainder.Coefficients;
        int length = result.length;
        // 长度不够前面补0
        int padding = degree - length;
        if (padding == 0) {
            return result;
        } else {
            int[] resultPadding = new int[degree];
            System.arraycopy(result, 0, resultPadding, padding, length);
            return resultPadding;
        }
    }

}
