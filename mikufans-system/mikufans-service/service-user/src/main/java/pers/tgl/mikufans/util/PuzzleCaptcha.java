package pers.tgl.mikufans.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 拼图验证码
 * @author TGL
 */
public class PuzzleCaptcha {
    private static final Random random = new Random();

    // 图片的base64前缀
    private static final String jpegheader = "data:image/jpeg;base64,";
    private static final String pngheader = "data:image/png;base64,";

    // 原图
    private File rawImage;
    // 拼图
    private File puzzle;
    // 过滤色
    private String filterColor = "6a6a6a";

    public Result handle() throws IOException {
        // 获取拼图的颜色位置(忽略透明色)
        List<Point> puzzleRGB = new ArrayList<>();
        BufferedImage puzzleImage = ImageIO.read(puzzle);
        int puzzleWidth = puzzleImage.getWidth();
        int puzzleHeight = puzzleImage.getHeight();
        for (int x = 0; x < puzzleWidth; x++) {
            for (int y = 0; y < puzzleHeight; y++) {
                int rgb = puzzleImage.getRGB(x, y);
                if (rgb != 0) {
                    puzzleRGB.add(new Point(x, y));
                }
            }
        }
        // 原图
        BufferedImage bufferedImage = ImageIO.read(rawImage);
        // 滑块图
        BufferedImage sliderImage = new BufferedImage(puzzleWidth, puzzleHeight, puzzleImage.getType());
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        // x2防止出现在边界（最顶部或最底部）
        int targetX = (int) ((width - puzzleWidth * 2) * random.nextDouble()) + puzzleWidth;
        int targetY = (int) ((height - puzzleHeight * 2) * random.nextDouble()) + puzzleHeight;
        for (Point point : puzzleRGB) {
            // 混淆位置
            int x = point.x + targetX;
            int y = point.y + targetY;
            sliderImage.setRGB(point.x, point.y, bufferedImage.getRGB(x, y));
            bufferedImage.setRGB(x, y, Integer.parseInt(filterColor, 16));
        }
        Result result = new Result();
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", os);
        result.image = jpegheader + Base64.encodeBase64String(os.toByteArray());
        os.reset();
        ImageIO.write(sliderImage, "png", os);
        result.puzzle = pngheader + Base64.encodeBase64String(os.toByteArray());
        os.close();
        result.value = targetX / (float) width;
        result.offsetY = targetY / (float) height;
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    // 验证码生成结果 base64
    public static final class Result {
        // 经过裁剪的原图
        public String image;
        // 裁剪出来的部分
        public String puzzle;
        // 正确的x偏移量(百分比0-1)
        public Float value;
        // y偏移量(百分比0-1)
        public Float offsetY;
    }

    public static final class Builder {
        private final PuzzleCaptcha puzzleCaptcha;
        public Builder() {
            puzzleCaptcha = new PuzzleCaptcha();
        }

        public Builder setFilterColor(String filterColor) {
            puzzleCaptcha.filterColor = filterColor;
            return this;
        }

        public Builder setRawImageUrl(String rawImageUrl) {
            puzzleCaptcha.rawImage = new File(rawImageUrl);
            return this;
        }
        public Builder setRawImageFile(File rawImageFile) {
            puzzleCaptcha.rawImage = rawImageFile;
            return this;
        }

        public Builder setPuzzleUrl(String puzzleUrl) {
            puzzleCaptcha.puzzle = new File(puzzleUrl);
            return this;
        }

        public Builder setPuzzleFile(File puzzleFile) {
            puzzleCaptcha.puzzle = puzzleFile;
            return this;
        }

        public Result build() throws IOException {
            return puzzleCaptcha.handle();
        }
    }
}