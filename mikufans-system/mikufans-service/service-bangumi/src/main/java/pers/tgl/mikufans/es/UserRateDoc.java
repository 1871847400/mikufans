package pers.tgl.mikufans.es;

import com.github.yulichang.toolkit.Constant;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.user.UserRate;
import pers.tgl.mikufans.domain.video.Video;

@ElasticAutoSync(
        tableClass = UserDynamic.class,
        name = "rate_dynamic",
        joins = {
            @ElasticAutoSync.Join(thisField = UserDynamic.Fields.targetId, joinTable = UserRate.class, method = Constant.INNER_JOIN),
            @ElasticAutoSync.Join(thisField = UserRate.Fields.targetId, joinTable = Video.class, joinField = Video.Fields.bangumiId, method = Constant.INNER_JOIN),
        }
)
public class UserRateDoc extends UserDynamicDoc {
    public UserRateDoc() {}

    public UserRateDoc(UserDynamic userDynamic, UserRate userRate, Video video) {
        super(userDynamic, video.getTitle(), userRate.getContent());
    }
}