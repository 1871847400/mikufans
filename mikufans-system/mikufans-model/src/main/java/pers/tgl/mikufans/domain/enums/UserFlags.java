package pers.tgl.mikufans.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum UserFlags {
    PUBLIC_UPLOAD("公开投稿", true),
    PUBLIC_STAR("公开收藏", true),
    PUBLIC_FOLLOW("公开关注", true),
    PUBLIC_FANS("公开粉丝", true),
    PUBLIC_SUBSCRIBE("公开订阅", true);

    private final String display;
    private final Object defValue;

    public Map<String, Object> getMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name());
        map.put("display", display);
        map.put("defValue", defValue);
        return map;
    }
}