package com.hzh.pinda.log.event;


import com.hzh.pinda.log.entity.OptLogDTO;

import org.springframework.context.ApplicationEvent;

/**
 * 系统日志事件
 *
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(OptLogDTO source) {
        super(source);
    }
}
