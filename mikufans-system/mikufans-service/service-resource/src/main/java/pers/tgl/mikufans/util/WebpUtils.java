package pers.tgl.mikufans.util;

import com.luciad.imageio.webp.WebPWriteParam;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class WebpUtils {
    public static void compress(String input, String output) throws IOException {
        compress(new File(input), new File(output));
    }

    public static void compress(File input, File output) throws IOException {
        compress(Files.newInputStream(input.toPath()), output);
    }

    public static void compress(InputStream input, File output) throws IOException {
        BufferedImage image = ImageIO.read(input);
        ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
        WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        // 设置有损压缩
        writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
        //设置 80% 的质量. 设置范围 0-1
        writeParam.setCompressionQuality(0.8f);
//        设置无损,会比压缩之前大几倍
//        writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);
        writer.setOutput(new FileImageOutputStream(output));
        writer.write(null, new IIOImage(image, null, null), writeParam);
    }
}