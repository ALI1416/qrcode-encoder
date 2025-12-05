# QR Code Encoder For Java 二维码生成器Java版

[![License](https://img.shields.io/github/license/ALI1416/qrcode-encoder?label=License)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Java Support](https://img.shields.io/badge/Java-8+-green)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/maven-central/v/cn.404z/qrcode-encoder?label=Maven%20Central)](https://mvnrepository.com/artifact/cn.404z/qrcode-encoder)
[![Tag](https://img.shields.io/github/v/tag/ALI1416/qrcode-encoder?label=Tag)](https://github.com/ALI1416/qrcode-encoder/tags)
[![Repo Size](https://img.shields.io/github/repo-size/ALI1416/qrcode-encoder?label=Repo%20Size&color=success)](https://github.com/ALI1416/qrcode-encoder/archive/refs/heads/master.zip)

[![Java CI](https://github.com/ALI1416/qrcode-encoder/actions/workflows/ci.yml/badge.svg)](https://github.com/ALI1416/qrcode-encoder/actions/workflows/ci.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_qrcode-encoder&metric=coverage)
![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_qrcode-encoder&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_qrcode-encoder&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_qrcode-encoder&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_qrcode-encoder)

## 简介

本项目迁移自[ALI1416/QRCodeEncoder.Net](https://github.com/ALI1416/QRCodeEncoder.Net)，只编写了生成器部分，并对处理逻辑进行了大量优化，构建后`qrcode-encoder.jar`文件仅`20kb`

注意：本项目不提供二维码绘制方法，如需绘制请看`使用示例`

### 其他语言项目

- `.Net` : [ALI1416/QRCodeEncoder.Net](https://github.com/ALI1416/QRCodeEncoder.Net)
- `JavaScript` : [ALI1416/qrcode-encoder-js](https://github.com/ALI1416/qrcode-encoder-js)

## 依赖导入

```xml
<dependency>
    <groupId>cn.404z</groupId>
    <artifactId>qrcode-encoder</artifactId>
    <version>1.4.0</version>
</dependency>
```

## 方法和参数

### 二维码 QRCode

| 参数名        | 中文名   | 类型    | 默认值     |
| ------------- | -------- | ------- | ---------- |
| content       | 内容     | String  | (无)       |
| level         | 纠错等级 | Integer | 0          |
| mode          | 编码模式 | Integer | (自动探测) |
| versionNumber | 版本号   | Integer | (最小版本) |

### 版本 Version

| 参数名        | 中文名     | 类型    | 默认值     |
| ------------- | ---------- | ------- | ---------- |
| length        | 内容字节数 | int     | (无)       |
| level         | 纠错等级   | int     | (无)       |
| mode          | 编码模式   | int     | (无)       |
| versionNumber | 版本号     | Integer | (最小版本) |

### 掩模模板 MaskPattern

| 参数名  | 中文名   | 类型      |
| ------- | -------- | --------- |
| data    | 数据     | boolean[] |
| version | 版本     | Version   |
| level   | 纠错等级 | int       |

### 纠错等级 level

| 值  | 等级 | 纠错率 |
| --- | ---- | ------ |
| 0   | L    | 7%     |
| 1   | M    | 15%    |
| 2   | Q    | 25%    |
| 3   | H    | 30%    |

### 编码模式 mode

| 值  | 模式             | 备注                                     |
| --- | ---------------- | ---------------------------------------- |
| 0   | NUMERIC          | 数字0-9                                  |
| 1   | ALPHANUMERIC     | 数字0-9、大写字母A-Z、符号(空格)$%*+-./: |
| 2   | BYTE(ISO-8859-1) | 兼容ASCII                                |
| 3   | BYTE(UTF-8)      |                                          |

### 版本号 versionNumber

取值范围：`[1,40]`

## 使用示例

QRCodeTest.java

```java
String content = "1234😀";
int level = 0;
int mode = 3;
int versionNumber = 1;
String path = "E:/1.png";
QRCode qrCode = new QRCode(content, level, mode, versionNumber);
BufferedImage image = ImageUtils.qrMatrix2Image(qrCode.Matrix, 10);
ImageUtils.saveImage(image, path);
```

ImageUtils.java

```java
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

public static void saveImage(BufferedImage image, String path) throws IOException {
    ImageIO.write(image, "png", new File(path));
}
```

更多请见[测试](./src/test)

## 参考

- [ALI1416/QRCodeEncoder.Net](https://github.com/ALI1416/QRCodeEncoder.Net)

## 更新日志

[点击查看](./CHANGELOG.md)

## 关于

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://www.404z.cn/images/about.dark.svg">
  <img alt="About" src="https://www.404z.cn/images/about.light.svg">
</picture>
