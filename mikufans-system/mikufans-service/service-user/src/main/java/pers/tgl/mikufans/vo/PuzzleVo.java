package pers.tgl.mikufans.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PuzzleVo implements Serializable {
   //拼块数据
   private String puzzle;
   //整个拼图
   private String image;
   //Y偏移值
   private float offset;
   //拼图id
   private String puzzleId;
   //到期时间
   private long expireAt;
}