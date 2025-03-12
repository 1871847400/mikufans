package pers.tgl.mikufans.anno;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PermGroup {
    SYSTEM("系统管理", "system"),
    BUSINESS("业务管理", "business"),
    LOG("日志管理", "log"),
    MONITOR("系统监控", "monitor");

    private final String permName;
    private final String permPrefix;
}