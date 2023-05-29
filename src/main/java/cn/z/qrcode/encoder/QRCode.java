package cn.z.qrcode.encoder;

import java.nio.charset.StandardCharsets;

/**
 * <h1>二维码</h1>
 *
 * <p>
 * createDate 2023/05/29 11:11:11
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class QRCode {

    /**
     * 纠错等级
     */
    public final int Level;
    /**
     * 编码模式
     */
    public final int Mode;
    /**
     * 版本
     */
    public final Version Version;
    /**
     * 版本号
     */
    public final int VersionNumber;
    /**
     * 掩模模板
     */
    public final MaskPattern MaskPattern;
    /**
     * 掩模模板号
     */
    public final int MaskPatternNumber;
    /**
     * 矩阵
     * <p>false白 true黑</p>
     */
    public final boolean[][] Matrix;


    /**
     * 构造二维码
     * <p>纠错等级 0 L 7%</p>
     * <p>编码模式 自动探测</p>
     * <p>版本号 最小版本</p>
     *
     * @param content 内容
     */
    public QRCode(String content) {
        this(content, null, null, null);
    }

    /**
     * 构造二维码
     * <p>编码模式 自动探测</p>
     * <p>版本号 最小版本</p>
     *
     * @param content 内容
     * @param level   纠错等级<br>
     *                0 L 7%<br>
     *                1 M 15%<br>
     *                2 Q 25%<br>
     *                3 H 30%
     */
    public QRCode(String content, Integer level) {
        this(content, level, null, null);
    }

    /**
     * 构造二维码
     * <p>版本号 最小版本</p>
     *
     * @param content 内容
     * @param level   纠错等级<br>
     *                0 L 7%<br>
     *                1 M 15%<br>
     *                2 Q 25%<br>
     *                3 H 30%
     * @param mode    编码模式<br>
     *                0 NUMERIC 数字0-9<br>
     *                1 ALPHANUMERIC 数字0-9、大写字母A-Z、符号(空格)$%*+-./:<br>
     *                2 BYTE(ISO-8859-1)<br>
     *                3 BYTE(UTF-8)
     */
    public QRCode(String content, Integer level, Integer mode) {
        this(content, level, mode, null);
    }

    /**
     * 构造二维码
     *
     * @param content       内容
     * @param level         纠错等级<br>
     *                      0 L 7%<br>
     *                      1 M 15%<br>
     *                      2 Q 25%<br>
     *                      3 H 30%
     * @param mode          编码模式<br>
     *                      0 NUMERIC 数字0-9<br>
     *                      1 ALPHANUMERIC 数字0-9、大写字母A-Z、符号(空格)$%*+-./:<br>
     *                      2 BYTE(ISO-8859-1)<br>
     *                      3 BYTE(UTF-8)
     * @param versionNumber 版本号(默认最小版本)<br>
     *                      [1,40]
     */
    public QRCode(String content, Integer level, Integer mode, Integer versionNumber) {
        /* 数据 */
        if (content == null) {
            content = "";
        }
        // 纠错等级
        if (level == null) {
            level = 0;
        } else if (level < 0 || level > 3) {
            throw new RuntimeException("纠错等级 " + level + " 不合法！应为 [0,3]");
        }
        Level = level;
        // 编码模式
        if (mode == null) {
            mode = DetectionMode(content);
        } else if (mode < 0 || mode > 3) {
            throw new RuntimeException("编码模式 " + mode + " 不合法！应为 [0,3]");
        } else {
            int detectionMode = DetectionMode(content);
            if (mode < detectionMode) {
                throw new RuntimeException("编码模式 " + mode + " 太小！最小为 " + detectionMode);
            }
        }
        Mode = mode;
        // 内容bytes
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        // 版本
        Version = new Version(contentBytes.length, level, mode, versionNumber);
        VersionNumber = Version.VersionNumber;
        // 数据bits
        boolean[] dataBits = new boolean[Version.DataBits];
        // 填充数据
        switch (mode) {
            // 填充编码模式为NUMERIC的数据
            case 0: {
                ModeNumbers(dataBits, contentBytes, Version);
                break;
            }
            // 填充编码模式为ALPHANUMERIC的数据
            case 1: {
                ModeAlphaNumeric(dataBits, contentBytes, Version);
                break;
            }
            // 填充编码模式为BYTE编码格式为ISO-8859-1的数据
            case 2: {
                ModeByteIso88591(dataBits, contentBytes, Version);
                break;
            }
            // 填充编码模式为BYTE编码格式为UTF-8的数据
            default: {
                ModeByteUtf8(dataBits, contentBytes, Version);
                break;
            }
        }

        /* 纠错 */
        int[][] ec = Version.Ec;
        // 数据块数 或 纠错块数
        int blocks = 0;
        for (int i = 0; i < Version.Ec.length; i++) {
            blocks += Version.Ec[i][0];
        }
        // 纠错块字节数
        int ecBlockBytes = (Version.DataAndEcBits - Version.DataBits) / 8 / blocks;
        int[][] dataBlocks = new int[blocks][];
        int[][] ecBlocks = new int[blocks][];
        int blockNum = 0;
        int dataByteNum = 0;
        for (int[] e : ec) {
            int count = e[0];
            int dataBytes = e[1];
            for (int j = 0; j < count; j++) {
                // 数据块
                int[] dataBlock = QRCodeUtils.GetBytes(dataBits, dataByteNum * 8, dataBytes);
                dataBlocks[blockNum] = dataBlock;
                // 纠错块
                ecBlocks[blockNum] = ReedSolomon.Encoder(dataBlock, ecBlockBytes);
                blockNum++;
                dataByteNum += dataBytes;
            }
        }

        /* 交叉数据和纠错 */
        boolean[] dataAndEcBits = new boolean[Version.DataAndEcBits];
        int dataBlockMaxBytes = dataBlocks[blocks - 1].length;
        int dataAndEcBitPtr = 0;
        for (int i = 0; i < dataBlockMaxBytes; i++) {
            for (int j = 0; j < blocks; j++) {
                if (dataBlocks[j].length > i) {
                    QRCodeUtils.AddBits(dataAndEcBits, dataAndEcBitPtr, dataBlocks[j][i], 8);
                    dataAndEcBitPtr += 8;
                }
            }
        }
        for (int i = 0; i < ecBlockBytes; i++) {
            for (int j = 0; j < blocks; j++) {
                QRCodeUtils.AddBits(dataAndEcBits, dataAndEcBitPtr, ecBlocks[j][i], 8);
                dataAndEcBitPtr += 8;
            }
        }

        /* 构造掩模模板 */
        MaskPattern = new MaskPattern(dataAndEcBits, Version, level);
        MaskPatternNumber = MaskPattern.Best;
        Matrix = QRCodeUtils.Convert(MaskPattern.Patterns[MaskPatternNumber], Version.Dimension);
    }

    /**
     * 填充编码模式为NUMERIC的数据
     *
     * @param dataBits     数据bits
     * @param contentBytes 内容bytes
     * @param version      版本
     */
    private static void ModeNumbers(boolean[] dataBits, byte[] contentBytes, Version version) {
        // 数据指针
        int ptr = 0;
        // 模式指示符(4bit) NUMERIC 0b0001=1
        // 数据来源 ISO/IEC 18004-2015 -> 7.4.1 -> Table 2 -> QR Code symbols列Numbers行
        QRCodeUtils.AddBits(dataBits, ptr, 1, 4);
        ptr += 4;
        // 内容字节数
        int contentLength = contentBytes.length;
        // `内容字节数`bit数(10/12/14bit)
        int contentBytesBits = version.ContentBytesBits;
        QRCodeUtils.AddBits(dataBits, ptr, contentLength, contentBytesBits);
        ptr += contentBytesBits;
        // 内容 3个字符10bit 2个字符7bit 1个字符4bit
        for (int i = 0; i < contentLength - 2; i += 3) {
            QRCodeUtils.AddBits(dataBits, ptr,
                    (contentBytes[i] - 48) * 100 + (contentBytes[i + 1] - 48) * 10 + contentBytes[i + 2] - 48, 10);
            ptr += 10;
        }
        switch (contentLength % 3) {
            case 2: {
                QRCodeUtils.AddBits(dataBits, ptr,
                        (contentBytes[contentLength - 2] - 48) * 10 + contentBytes[contentLength - 1] - 48, 7);
                ptr += 7;
                break;
            }
            case 1: {
                QRCodeUtils.AddBits(dataBits, ptr, contentBytes[contentLength - 1] - 48, 4);
                ptr += 4;
                break;
            }
            default:
        }
        // 结束符和补齐符
        TerminatorAndPadding(dataBits, version.DataBits, ptr);
    }

    /**
     * 填充编码模式为ALPHANUMERIC的数据
     *
     * @param dataBits     数据bits
     * @param contentBytes 内容bytes
     * @param version      版本
     */
    private static void ModeAlphaNumeric(boolean[] dataBits, byte[] contentBytes, Version version) {
        // 数据指针
        int ptr = 0;
        // 模式指示符(4bit) ALPHANUMERIC 0b0010=2
        // 数据来源 ISO/IEC 18004-2015 -> 7.4.1 -> Table 2 -> QR Code symbols列Alphanumeric行
        QRCodeUtils.AddBits(dataBits, ptr, 2, 4);
        ptr += 4;
        // 内容字节数
        int contentLength = contentBytes.length;
        // `内容字节数`bit数(9/11/13bit)
        int contentBytesBits = version.ContentBytesBits;
        QRCodeUtils.AddBits(dataBits, ptr, contentLength, contentBytesBits);
        ptr += contentBytesBits;
        // 内容 2个字符11bit 1个字符6bit
        for (int i = 0; i < contentLength - 1; i += 2) {
            QRCodeUtils.AddBits(dataBits, ptr,
                    ALPHA_NUMERIC_TABLE[contentBytes[i]] * 45 + ALPHA_NUMERIC_TABLE[contentBytes[i + 1]], 11);
            ptr += 11;
        }
        if (contentLength % 2 == 1) {
            QRCodeUtils.AddBits(dataBits, ptr, ALPHA_NUMERIC_TABLE[contentBytes[contentLength - 1]], 6);
            ptr += 6;
        }
        // 结束符和补齐符
        TerminatorAndPadding(dataBits, version.DataBits, ptr);
    }

    /**
     * 填充编码模式为BYTE编码格式为ISO-8859-1的数据
     *
     * @param dataBits     数据bits
     * @param contentBytes 内容bytes
     * @param version      版本
     */
    private static void ModeByteIso88591(boolean[] dataBits, byte[] contentBytes, Version version) {
        // 数据指针
        int ptr = 0;
        // 模式指示符(4bit) BYTE 0b0100=4
        // 数据来源 ISO/IEC 18004-2015 -> 7.4.1 -> Table 2 -> QR Code symbols列Byte行
        QRCodeUtils.AddBits(dataBits, ptr, 4, 4);
        ptr += 4;
        // 内容字节数
        int contentLength = contentBytes.length;
        // `内容字节数`bit数(8/16bit)
        int contentBytesBits = version.ContentBytesBits;
        QRCodeUtils.AddBits(dataBits, ptr, contentLength, contentBytesBits);
        ptr += contentBytesBits;
        // 内容
        for (byte contentByte : contentBytes) {
            QRCodeUtils.AddBits(dataBits, ptr, contentByte, 8);
            ptr += 8;
        }
        // 结束符和补齐符
        TerminatorAndPadding(dataBits, version.DataBits, ptr);
    }

    /**
     * 填充编码模式为BYTE编码格式为UTF-8的数据
     *
     * @param dataBits     数据bits
     * @param contentBytes 内容bytes
     * @param version      版本
     */
    private static void ModeByteUtf8(boolean[] dataBits, byte[] contentBytes, Version version) {
        // 数据指针
        int ptr = 0;
        // ECI模式指示符(4bit) 0b0111=7
        // 数据来源 ISO/IEC 18004-2015 -> 7.4.1 -> Table 2 -> QR Code symbols列ECI行
        QRCodeUtils.AddBits(dataBits, ptr, 7, 4);
        ptr += 4;
        // ECI指定符 UTF-8(8bit) 0b00011010=26
        // 数据来源 ?
        QRCodeUtils.AddBits(dataBits, ptr, 26, 8);
        ptr += 8;
        // 模式指示符(4bit) BYTE 0b0100=4
        // 数据来源 ISO/IEC 18004-2015 -> 7.4.1 -> Table 2 -> QR Code symbols列Byte行
        QRCodeUtils.AddBits(dataBits, ptr, 4, 4);
        ptr += 4;
        // 内容字节数
        int contentLength = contentBytes.length;
        // `内容字节数`bit数(8/16bit)
        int contentBytesBits = version.ContentBytesBits;
        QRCodeUtils.AddBits(dataBits, ptr, contentLength, contentBytesBits);
        ptr += contentBytesBits;
        // 内容
        for (byte contentByte : contentBytes) {
            QRCodeUtils.AddBits(dataBits, ptr, contentByte, 8);
            ptr += 8;
        }
        // 结束符和补齐符
        TerminatorAndPadding(dataBits, version.DataBits, ptr);
    }

    /**
     * 结束符和补齐符
     *
     * @param data     数据bits
     * @param dataBits 数据bits数
     * @param ptr      数据指针
     */
    private static void TerminatorAndPadding(boolean[] data, int dataBits, int ptr) {
        // 如果有刚好填满，则不需要结束符和补齐符
        // 如果还剩1-8bit，需要1-8bit结束符，不用补齐符
        // 如果还剩8+bit，先填充4bit结束符，再填充结束符使8bit对齐，再交替补齐符至填满
        if (dataBits - ptr > 7) {
            // 结束符(4bit)
            // 数据来源 ISO/IEC 18004-2015 -> 7.4.9
            ptr += 4;
            // 结束符(8bit对齐)
            ptr = (((ptr - 1) / 8) + 1) * 8;
            // 补齐符 交替0b11101100=0xEC和0b00010001=0x11至填满
            // 数据来源 ISO/IEC 18004-2015 -> 7.4.10
            int count = (dataBits - ptr) / 8;
            for (int i = 0; i < count; i++) {
                if (i % 2 == 0) {
                    System.arraycopy(NUMBER_0xEC_8BITS, 0, data, ptr, 8);
                } else {
                    System.arraycopy(NUMBER_0x11_8BITS, 0, data, ptr, 8);
                }
                ptr += 8;
            }
        }
    }

    /**
     * 探测编码模式
     *
     * @param content 内容
     * @return 编码模式<br>
     * 0 NUMERIC 数字0-9<br>
     * 1 ALPHANUMERIC 数字0-9、大写字母A-Z、符号(空格)$%*+-./:<br>
     * 2 BYTE(ISO-8859-1)<br>
     * 3 BYTE(UTF-8)
     */
    private static int DetectionMode(String content) {
        int length = content.length();
        // 为了与ZXing结果保持一致，长度为0时使用BYTE(ISO-8859-1)编码
        if (length == 0) {
            return 2;
        }
        // BYTE(UTF-8)
        for (int i = 0; i < length; i++) {
            if (content.charAt(i) > 255) {
                return 3;
            }
        }
        // BYTE(ISO-8859-1)
        for (int i = 0; i < length; i++) {
            if (content.charAt(i) > 127 || ALPHA_NUMERIC_TABLE[content.charAt(i)] > 44) {
                return 2;
            }
        }
        // ALPHANUMERIC 数字0-9、大写字母A-Z、符号(空格)$%*+-./:
        for (int i = 0; i < length; i++) {
            if (ALPHA_NUMERIC_TABLE[content.charAt(i)] > 9) {
                return 1;
            }
        }
        // NUMERIC 数字0-9
        return 0;
    }

    /**
     * 数字0xEC
     * <p>数据来源 ISO/IEC 18004-2015 -> 7.4.10 -> 11101100</p>
     */
    private static final boolean[] NUMBER_0xEC_8BITS = QRCodeUtils.GetBits(0xEC, 8);
    /**
     * 数字0x11
     * <p>数据来源 ISO/IEC 18004-2015 -> 7.4.10 -> 00010001</p>
     */
    private static final boolean[] NUMBER_0x11_8BITS = QRCodeUtils.GetBits(0x11, 8);

    /**
     * ALPHANUMERIC模式映射表
     * <p>数字0-9 [0x30,0x39] [0,9]</p>
     * <p>大写字母A-Z [0x41,0x5A] [10,35]</p>
     * <p>(空格) [0x20] [36]</p>
     * <p>$ [0x24] [37]</p>
     * <p>% [0x25] [38]</p>
     * <p>* [0x2A] [39]</p>
     * <p>+ [0x2B] [40]</p>
     * <p>- [0x2D] [41]</p>
     * <p>. [0x2E] [42]</p>
     * <p>/ [0x2F] [43]</p>
     * <p>: [0x3A] [44]</p>
     * <p>数据来源 ISO/IEC 18004-2015 -> 7.4.5 -> Table 6</p>
     */
    private static final byte[] ALPHA_NUMERIC_TABLE = new byte[]{ //
            127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, // 0x00-0x0F
            127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, // 0x10-0x1F
            36, 127, 127, 127, 37, 38, 127, 127, 127, 127, 39, 40, 127, 41, 42, 43, // 0x20-0x2F
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, 127, 127, 127, 127, 127, // 0x30-0x3F
            127, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, // 0x40-0x4F
            25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 127, 127, 127, 127, 127, // 0x50-0x5F
            127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, // 0x60-0x6F
            127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, // 0x70-0x7F
    };

}
