package pers.tgl.mikufans.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.domain.enums.SubtitleType;
import pers.tgl.mikufans.domain.system.SysRegion;
import pers.tgl.mikufans.domain.video.VideoSubtitle;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.VideoSubtitleMapper;
import pers.tgl.mikufans.dto.SubtitleFile;
import pers.tgl.mikufans.service.VideoResourceService;
import pers.tgl.mikufans.service.VideoSubtitleService;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoSubtitleServiceImpl extends BaseServiceImpl<VideoSubtitle, VideoSubtitleMapper> implements VideoSubtitleService {
    @Resource
    @Lazy
    private VideoResourceService videoResourceService;

    public File getSubtitleFile(Long resId, @Nullable String child) {
        File storage = videoResourceService.getStorage(resId, "subtitles");
        return StrUtil.isBlank(child) ? storage : new File(storage, child);
    }

    public VideoSubtitle getLanguage(Long resId, Long regionId) {
        return lambdaQuery()
                .eq(VideoSubtitle::getRid, resId)
                .eq(VideoSubtitle::getRegionId, regionId)
                .one();
    }

    @Override
    public List<VideoSubtitle> listByResId(Long resId) {
        return wrapper()
                .selectAll(VideoSubtitle.class)
                .selectAssociation(SysRegion.class, VideoSubtitle::getRegion)
                .leftJoin(SysRegion.class, SysRegion::getId, VideoSubtitle::getRegionId)
                .eq(VideoSubtitle::getRid, resId)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSubtitle(Long vid, Long rid, SubtitleFile file) {
        if (file.getType() == null || file.getType() == SubtitleType.UNKNOWN) {
            throw new CustomException("不支持的字幕类型");
        }
        SysRegion region = Db.getById(file.getRegionId(), SysRegion.class);
        VideoSubtitle oldSubtitle = getLanguage(rid, region.getId());
        if (oldSubtitle != null) {
            //如果已经存在同一地区的语言文件则先删除旧的再保存新的
            removeById(oldSubtitle);
        }
        String saveName = region.getLangEnName() + "." + file.getType().getSuffix();
        VideoSubtitle videoSubtitle = new VideoSubtitle();
        videoSubtitle.setFilename(file.getFilename());
        videoSubtitle.setRegionId(file.getRegionId());
        videoSubtitle.setType(file.getType());
        videoSubtitle.setVid(vid);
        videoSubtitle.setRid(rid);
        videoSubtitle.setSaveName(saveName);
        save(videoSubtitle);
        File saveFile = getSubtitleFile(rid, saveName);
        FileUtil.mkdir(saveFile.getParentFile());
        FileUtil.writeUtf8String(file.getData(), saveFile);
    }

    @Override
    public String getSubtitleText(Long id) {
        VideoSubtitle subtitle = getById(id);
        if (subtitle == null) {
            return "";
        }
        File file = videoResourceService.getStorage(subtitle.getRid(), "subtitles" + File.separator + subtitle.getSaveName());
        if (FileUtil.exist(file)) {
            return FileUtil.readUtf8String(file);
        }
        return "";
    }

    @Override
    public boolean removeById(VideoSubtitle entity) {
        if (super.removeById(entity)) {
            File subtitleFile = getSubtitleFile(entity.getRid(), entity.getSaveName());
            return FileUtil.del(subtitleFile);
        }
        return false;
    }
}