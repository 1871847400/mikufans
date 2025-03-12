package pers.tgl.mikufans.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

@Data
public class SearchResultCombine {
    private IPage<VideoVo> video;
    private IPage<VideoVo> anime;
    private IPage<VideoVo> movie;
    private IPage<UserVo> user;
}