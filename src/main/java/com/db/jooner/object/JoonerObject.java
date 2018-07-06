package com.db.jooner.object;

import java.util.Date;

/**
 * 〈数据库对象的基类〉<br>
 *
 * @author 未绪
 * @time 2018/2/12 14:56
 */
public class JoonerObject<T> {

    //当前对象的类型
    private short type;
    //当前对象的编码
    private short encoding;
    //当前对象
    private T obj;
    //当前对象的存活到的时间
    private long ttl;

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getEncoding() {
        return encoding;
    }

    public void setEncoding(short encoding) {
        this.encoding = encoding;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
