package pers.tgl.mikufans.consts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import pers.tgl.mikufans.es.UserDoc;

@RequiredArgsConstructor
@Getter
public enum UserSearchSort {
    DEFAULT("默认排序", "", Sort.Direction.DESC),
    FANS_MORE("粉丝数由高到低", UserDoc.Fields.fans, Sort.Direction.DESC),
    FANS_LESS("粉丝数由低到高", UserDoc.Fields.fans, Sort.Direction.ASC),
    LEVEL_HIGH("等级由高到低", UserDoc.Fields.exp, Sort.Direction.DESC),
    LEVEL_LOW("等级由低到高", UserDoc.Fields.exp,  Sort.Direction.ASC);

    private final String label;
    private final String property;
    private final Sort.Direction direction;
}