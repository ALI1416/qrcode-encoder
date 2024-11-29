package cn.z.qrcode.encoder;

/**
 * <h1>通用Galois Fields域多项式(通用伽罗华域多项式)</h1>
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
public class GenericGFPoly {

    /**
     * 多项式系数([0]常数项、[1]一次方的系数、[2]二次方的系数...)
     */
    public final int[] Coefficients;
    /**
     * 多项式次数
     */
    public final int Degree;
    /**
     * 多项式是否为0(常数项为0)
     */
    public final boolean IsZero;

    /**
     * 构造多项式
     *
     * @param coefficients 多项式常数
     */
    public GenericGFPoly(int[] coefficients) {
        int coefficientsLength = coefficients.length;
        // 常数项为0
        if (coefficients[0] == 0) {
            // 查找第一个非0的下标
            int firstNonZero = 1;
            while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0) {
                firstNonZero++;
            }
            // 全为0
            if (firstNonZero == coefficientsLength) {
                // 该多项式为0
                Coefficients = new int[]{0};
            } else {
                // 去除前面的0
                Coefficients = new int[coefficientsLength - firstNonZero];
                System.arraycopy(coefficients, firstNonZero, Coefficients, 0, Coefficients.length);
            }
        } else {
            Coefficients = coefficients;
        }
        Degree = Coefficients.length - 1;
        IsZero = Coefficients[0] == 0;
    }

    /**
     * 获取多项式中`次数`的系数
     *
     * @param degree 次数
     * @return 系数
     */
    public int GetCoefficient(int degree) {
        return Coefficients[Coefficients.length - 1 - degree];
    }

    /**
     * 加法
     *
     * @param other 被加数
     * @return 结果
     */
    public GenericGFPoly Addition(GenericGFPoly other) {
        if (IsZero) {
            return other;
        }
        if (other.IsZero) {
            return this;
        }
        int[] smallerCoefficients = Coefficients;
        int[] largerCoefficients = other.Coefficients;
        if (smallerCoefficients.length > largerCoefficients.length) {
            int[] temp = largerCoefficients;
            largerCoefficients = smallerCoefficients;
            smallerCoefficients = temp;
        }
        int[] sumDiff = new int[largerCoefficients.length];
        int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
        System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);
        for (int i = lengthDiff; i < largerCoefficients.length; i++) {
            sumDiff[i] = GenericGF.Addition(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
        }
        return new GenericGFPoly(sumDiff);
    }

    /**
     * 乘法
     *
     * @param other 被乘数
     * @return 结果
     */
    public GenericGFPoly Multiply(GenericGFPoly other) {
        if (IsZero || other.IsZero) {
            return ReedSolomon.Zero;
        }
        int[] aCoefficients = Coefficients;
        int[] bCoefficients = other.Coefficients;
        int aLength = aCoefficients.length;
        int bLength = bCoefficients.length;
        int[] product = new int[aLength + bLength - 1];
        for (int i = 0; i < aLength; i++) {
            int aCoefficient = aCoefficients[i];
            for (int j = 0; j < bLength; j++) {
                product[i + j] = GenericGF.Addition(product[i + j], GenericGF.Multiply(aCoefficient, bCoefficients[j]));
            }
        }
        return new GenericGFPoly(product);
    }

    /**
     * 单项式乘法
     *
     * @param degree      次数
     * @param coefficient 系数
     * @return 结果
     */
    public GenericGFPoly MultiplyByMonomial(int degree, int coefficient) {
        if (coefficient == 0) {
            return ReedSolomon.Zero;
        }
        int size = Coefficients.length;
        int[] product = new int[size + degree];
        for (int i = 0; i < size; i++) {
            product[i] = GenericGF.Multiply(Coefficients[i], coefficient);
        }
        return new GenericGFPoly(product);
    }

    /**
     * 除法的余数
     *
     * @param other 被除数
     * @return 余数
     */
    public GenericGFPoly RemainderOfDivide(GenericGFPoly other) {
        GenericGFPoly remainder = this;
        int denominatorLeadingTerm = other.GetCoefficient(other.Degree);
        int inverseDenominatorLeadingTerm = GenericGF.Inverse(denominatorLeadingTerm);
        while (remainder.Degree >= other.Degree && !remainder.IsZero) {
            int degreeDifference = remainder.Degree - other.Degree;
            int scale = GenericGF.Multiply(remainder.GetCoefficient(remainder.Degree), inverseDenominatorLeadingTerm);
            GenericGFPoly term = other.MultiplyByMonomial(degreeDifference, scale);
            remainder = remainder.Addition(term);
        }
        return remainder;
    }

}
