package pers.tgl.mikufans.anno;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PermType {
    SELECT("查询", "select"),
    CREATE("创建", "create"),
    UPDATE("更新", "update"),
    DELETE("删除", "delete");

    private final String label;
    private final String value;
}