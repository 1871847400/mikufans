#插入默认最高管理员账号admin,密码123456
#默认使用原文存储,建议尽快登录后台修改密码
insert into sys_user(id, username, password)
values (1, 'admin', '{noop}123456');

#插入默认视频分区
insert into video_channel(id, pid, channel_name, channel_desc, icon_name, url)
values (10, 0, '动画', '', 'channel-icon-anime', '/channel/anime'),
       (11, 10, '官方资讯', '以动画/轻小说/漫画/杂志为主的资讯内容，PV/CM/特报/冒头/映像/预告', '',
        '/channel/anime/info'),
       (12, 10, '个人二创', '使用已存在的动画作品进行二次加工创作', '', '/channel/anime/sc'),
       (13, 10, '自制动画', '由个人或小团队独立制作的动画作品', '', '/channel/anime/art'),
       (14, 10, '动画杂谈', '对动画作品的剧情,背景,文化等进行讨论和分析', '', '/channel/anime/talk'),
       (20, 0, '影视', '', 'channel-icon-teleplay', '/channel/teleplay'),
       (21, 20, '影视解说', '对电影/电视剧内容进行讲解、分析和阐述', '', '/channel/teleplay/talk'),
       (22, 20, '影视资讯', '电影/电视剧的预告,情报等', '', '/channel/teleplay/info'),
       (30, 0, '生活', '', 'channel-icon-life', '/channel/life'),
       (31, 30, '日常', '一般日常向的生活类视频', '', '/channel/life/daily'),
       (32, 30, '出行', '旅行、户外、探店、VLOG相关的视频', '', '/channel/life/trip'),
       (33, 30, '手工', '与手工艺、DIY、发明创造相关的视频', '', '/channel/life/art'),
       (40, 0, '科技', '', 'channel-icon-tech', '/channel/tech'),
       (41, 40, '数码', '手机/电脑/相机/影音智能设备等', '', '/channel/tech/num'),
       (42, 40, 'AI人工智能', '与最新的AI技术相关的视频', '', '/channel/tech/ai'),
       (50, 0, '游戏', '', 'channel-icon-game', '/channel/game'),
       (51, 50, '单机游戏', '以单机或其他联机模式为主要内容', '', '/channel/game/single'),
       (52, 50, '网络游戏', '多人在线游戏为主要内容', '', '/channel/game/network'),
       (53, 50, '手机游戏', '手机及平板设备平台上的游戏相关视频', '', '/channel/game/mobile'),
       (60, 0, '鬼畜', '', 'channel-icon-kichiku', '/channel/kichiku'),
       (61, 60, '鬼畜调教', '使用素材在音频、画面上做一定处理，达到与BGM具有一定同步感的视频', '',
        '/channel/kichiku/funny'),
       (62, 60, '教程演示', '鬼畜相关的科普和教程演示', '', '/channel/kichiku/teach'),
       (70, 0, '音乐', '', 'channel-icon-music', '/channel/music'),
       (71, 70, '原创音乐', '以任何题材创作的、以音乐主体为主要原创考量标准的原创歌曲及纯音乐', '',
        '/channel/music/original'),
       (72, 70, '音乐现场', '录户外或专业演出场所公开进行音乐表演的实况视频', '', '/channel/music/live'),
       (73, 70, '翻唱', '对曲目的人声再演绎视频', '', '/channel/music/cover'),
       (80, 0, '舞蹈', '', 'channel-icon-dance', '/channel/dance'),
       (81, 80, '宅舞', '与ACG相关的翻跳、原创舞蹈', '', '/channel/dance/otaku'),
       (82, 80, '街舞', '收录街舞相关内容，包括赛事现场、舞室作品、个人翻跳、FREESTYLE等', '', '/channel/dance/street'),
       (90, 0, '知识', '', 'channel-icon-knowledge', '/channel/knowledge'),
       (91, 90, '科学科普', '以自然科学或基于自然科学思维展开的知识视频', '', '/channel/knowledge/science'),
       (92, 90, '人文历史', '人物/文学/历史/文化/奇闻/艺术等', '', '/channel/knowledge/culture'),
       (100, 0, '纪录片', '', 'channel-icon-documentary', '/channel/documentary'),
       (101, 100, '自然', '除演讲、网课、教程外的，科学探索自然纪录剧集或电影、预告、花絮、二创', '',
        '/channel/documentary/nature'),
       (102, 100, '军事', '除时政军事新闻外的，军事纪录剧集或电影、预告、花絮、二创', '', '/channel/documentary/military');

#可选弹幕颜色
insert into video_danmu_color(color_name, color_code)
values ('红色', '#FE0302'),
       ('橙色', '#FF7204'),
       ('橙色2', '#FFAA02'),
       ('橙色3', '#FFD302'),
       ('黄色', '#FFFF00'),
       ('浅绿色', '#A0EE00'),
       ('绿色', '#00CD00'),
       ('青色', '#019899'),
       ('深蓝色', '#4266BE'),
       ('浅蓝色', '#89D5FF'),
       ('粉色', '#CC0273'),
       ('黑色', '#222222'),
       ('灰色', '#9B9B9B'),
       ('白色', '#FFFFFF');

insert into sys_param(param_key, param_value, remark)
values ('user_level_1', 200, '用户1级所需经验值'),
       ('user_level_2', 1000, '用户2级所需经验值'),
       ('user_level_3', 3000, '用户3级所需经验值'),
       ('user_level_4', 8000, '用户4级所需经验值'),
       ('user_level_5', 20000, '用户5级所需经验值'),
       ('user_level_6', 50000, '用户6级所需经验值'),
       ('star_max_video', 200, '单个收藏夹最多收藏多少个视频'),
       ('puzzle_captcha_enable', 'true', '是否开启拼图验证码');

delete from sys_notice;
insert into sys_notice(notice_type, title, template)
values (101, '视频举报结果反馈', '您举报的视频<em>{0}</em>已被要求下架整改。'),
       (102, '视频举报结果反馈', '您举报的视频<em>{0}</em>暂未发现违规。'),
       (103, '视频审核结果通知', '您投稿的<a href="#uri" target="_blank"><em>{0}</em></a>审核通过。'),
       (104, '视频审核结果通知', '您投稿的<em>{0}</em>审核未通过，原因：{1}'),
       (105, '视频违规提醒', '您的视频<em>{0}</em>涉嫌违规,原因：{1},已被暂时下架，请修改后申请重审。'),
       (106, '视频重审通知', '您的视频<em>{0}</em>已申请重新审核,请耐心等待。'),
       (201, '弹幕举报结果反馈', '您在<a href="#uri" target="_blank"><em>{0}</em></a>举报的弹幕『{1}』已被移除。'),
       (202, '弹幕举报结果反馈', '您在<a href="#uri" target="_blank"><em>{0}</em></a>举报的弹幕『{1}』暂未发现违规。'),
       (203, '弹幕违规提醒', '您在<a href="#uri" target="_blank"><em>{0}</em></a>发送的弹幕『{1}』已违规，原因: {2}。'),
       (301, '评论举报结果反馈', '您在<a href="#uri" target="_blank"><em>{0}</em></a>举报的评论『{1}』已被移除。'),
       (302, '评论举报结果反馈', '您在<a href="#uri" target="_blank"><em>{0}</em></a>举报的评论『{1}』暂未发现违规。'),
       (303, '评论违规提醒', '您在<a href="#uri" target="_blank"><em>{0}</em></a>发布的评论『{1}』已违规，原因：{2}。');

insert into bangumi_style(video_type, style_name)
values (2, '原创'),
       (2, '漫画改'),
       (2, '小说改'),
       (2, '游戏改'),
       (2, '热血'),
       (2, '穿越'),
       (2, '奇幻'),
       (2, '战斗'),
       (2, '搞笑'),
       (2, '日常'),
       (2, '科幻'),
       (2, '治愈'),
       (2, '泡面'),
       (2, '恋爱'),
       (2, '推理'),
       (2, '美食'),
       (2, '偶像'),
       (2, '催泪'),
       (3, '剧情'),
       (3, '喜剧'),
       (3, '爱情'),
       (3, '动作'),
       (3, '恐怖'),
       (3, '科幻'),
       (3, '犯罪'),
       (3, '惊悚'),
       (3, '动画'),
       (3, '历史'),
       (3, '战争');

insert into sys_region(region_name, region_code, lang_name, lang_code, lang_en_name)
values ('中国', 'CN', '中文', 'zh_CN', 'chinese'),
       ('日本', 'JP', '日语', 'ja_JP', 'japanese'),
       ('韩国', 'KR', '韩语', 'ko_KR', 'korea'),
       ('美国', 'US', '英语', 'en_US', 'english');

insert into report_behavior(category, behavior)
values ('违反法律法规', '违法违禁'),
       ('违反法律法规', '赌博诈骗'),
       ('违反法律法规', '色情低俗'),
       ('谣言及不实信息', '涉政谣言'),
       ('谣言及不实信息', '虚假不实信息'),
       ('侵犯个人权益', '人身攻击'),
       ('侵犯个人权益', '侵犯隐私'),
       ('有害社区环境', '垃圾广告'),
       ('有害社区环境', '引战'),
       ('有害社区环境', '剧透'),
       ('有害社区环境', '刷屏'),
       ('有害社区环境', '视频不相关'),
       ('有害社区环境', '违规抽奖');

insert into sys_ext_dict(`term`, `illegal`)
values ('恶魔之魂', 0),
       ('黑暗之魂', 0),
       ('只狼', 0),
       ('艾尔登法环', 0),
       ('黑神话', 0),
       ('黑神话悟空', 0),
       ('任天堂', 0),
       ('塞尔达传说', 0),
       ('旷野之息', 0),
       ('王国之泪', 0),
       ('米哈游', 0),
       ('崩坏学园', 0),
       ('原神', 0),
       ('星穹铁道', 0),
       ('绝区零', 0),
       ('傻逼', 1),
       ('弱智', 1),
       ('精神病', 1),
       ('奥里给', 0);

delete from sys_dict_type;
insert into sys_dict_type(dict_name, dict_type, mutable)
values ('禁用状态', 'disable_status', 0),
       ('是否状态', 'is_status', 0),
       ('标签类型', 'tag_type', 1),
       ('用户性别', 'user_gender', 1),
       ('轮播图位置', 'carousel_position', 0),
       ('弹幕类型', 'danmu_type', 0);

delete from sys_dict_data;
insert into sys_dict_data(dict_type, dict_label, dict_value, tag_type, icon_name, icon_color)
values ('disable_status', '正常', '0', 'success', '', ''),
       ('disable_status', '停用', '1', 'danger', '', ''),
       ('is_status', '是', '1', 'success', '', ''),
       ('is_status', '否', '0', 'danger', '', ''),
       ('tag_type', '默认', 'default', 'default', '', ''),
       ('tag_type', '主要', 'primary', 'primary', '', ''),
       ('tag_type', '成功', 'success', 'success', '', ''),
       ('tag_type', '信息', 'info', 'info', '', ''),
       ('tag_type', '警告', 'warning', 'warning', '', ''),
       ('tag_type', '危险', 'danger', 'danger', '', ''),
       ('user_gender', '保密', '0', 'warning', 'icon-xingbie', '#ccc'),
       ('user_gender', '男', '1', 'primary', 'icon-xingbie-nan', '#5fd4f2'),
       ('user_gender', '女', '2', 'success', 'icon-xingbie-nv', '#ff758f'),
       ('carousel_position', '首页', 'HOME', 'default', '', ''),
       ('carousel_position', '番剧', 'ANIME', 'default', '', ''),
       ('carousel_position', '电影', 'MOVIE', 'default', '', ''),
       ('carousel_position', '频道', 'CHANNEL', 'default', '', ''),
       ('danmu_type', '滚动弹幕', '1', 'primary', '', ''),
       ('danmu_type', '顶部弹幕', '2', 'success', '', ''),
       ('danmu_type', '底部弹幕', '3', 'warning', '', '');