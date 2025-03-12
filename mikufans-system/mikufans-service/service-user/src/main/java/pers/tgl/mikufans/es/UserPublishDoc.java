package pers.tgl.mikufans.es;

import com.github.yulichang.toolkit.Constant;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.user.UserPublish;

@ElasticAutoSync(
        tableClass = UserDynamic.class,
        name = "publish_dynamic",
        joins = @ElasticAutoSync.Join(thisField = UserDynamic.Fields.targetId, joinTable = UserPublish.class, method = Constant.INNER_JOIN)
)
public class UserPublishDoc extends UserDynamicDoc {
    public UserPublishDoc() {}

    public UserPublishDoc(UserDynamic userDynamic, UserPublish userPublish) {
        super(userDynamic, userPublish.getTitle(), userPublish.getContent());
    }
}