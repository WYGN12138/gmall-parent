package com.atguigu.gmall.model.vo.user;

import lombok.Data;
import sun.util.resources.cldr.es.CalendarData_es_PY;

@Data
public class LoginSuccessVo {
    private String token; //用户的令牌
    private String nickName; //用户

}
