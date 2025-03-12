package pers.tgl.mikufans.es;

import com.github.yulichang.toolkit.Constant;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.video.Video;

@ElasticAutoSync(
        tableClass = UserDynamic.class,
        name = "video_dynamic",
        joins = @ElasticAutoSync.Join(thisField = UserDynamic.Fields.targetId, joinTable = Video.class, method = Constant.INNER_JOIN)
)
public class VideoDynamicDoc extends UserDynamicDoc {
    public VideoDynamicDoc() {}

    public VideoDynamicDoc(UserDynamic userDynamic, Video video) {
        super(userDynamic, video.getTitle());
        if (video.getDisabled() == 1) {
            super.setDisabled(1);
        }
    }
}