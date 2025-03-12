package pers.tgl.mikufans.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 根据文件名前缀中的数字进行排序 (升序)
 * 要求文件名前缀必须是数字  下划线将当作小数点处理
 */
public class FileComparator<T> implements Comparator<T> {
    private final Function<T, String> getFileName;

    public FileComparator() {
        this.getFileName = Object::toString;
    }

    public FileComparator(Function<T, String> getFileName) {
        this.getFileName = getFileName;
    }

    private double parseValue(T f) {
        String filename = getFileName.apply(f).trim();
        try {
            return NumberUtil.parseDouble(FileUtil.getPrefix(filename).replace('_', '.'));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int compare(T f1, T f2) {
        return NumberUtil.compare(parseValue(f1), parseValue(f2));
    }
}