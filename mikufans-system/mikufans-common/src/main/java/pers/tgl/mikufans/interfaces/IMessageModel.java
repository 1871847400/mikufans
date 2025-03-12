package pers.tgl.mikufans.interfaces;

public interface IMessageModel {
    /**
     * 在消息列表中怎么展示
     */
    default String getMessage() {
        return null;
    }
    /**
     * 在消息列表中的访问链接
     */
    default String getUri() {
        return null;
    }
}