package pers.tgl.mikufans.event;

public enum EventAction {
    /**
     * 插入数据
     */
    INSERT,
    /**
     * 更新数据 entity内不为null的字段
     * @deprecated 更新可能过于频繁,不能保证每次都会发布事件
     */
    @Deprecated
    UPDATE,
    /**
     * 删除数据(包括逻辑删除)
     */
    DELETE;
}