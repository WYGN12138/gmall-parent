package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author mi
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2022-09-08 18:54:09
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 用户登录
     * @param info
     * @return
     */
    @Override
    public LoginSuccessVo login(UserInfo info) {
        LoginSuccessVo vo= new LoginSuccessVo();
        //1.查询数据库是否有这个人
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getLoginName, info.getLoginName()).eq(UserInfo::getPasswd, MD5.encrypt(info.getPasswd())));
        //2.判断
        if (userInfo!=null){//登录成功
            //生成令牌
            String token = UUID.randomUUID().toString().replace("-", "");

            //添加到redis key为user:login+token value：登录的信息 redis绑定信息
            redisTemplate.opsForValue()
                    .set(SysRedisConst.LOGIN_USER+token,
                    Jsons.toStr(userInfo),
                    7, TimeUnit.DAYS);
            //设置token,生成令牌
            vo.setToken(token);
            vo.setNickName(userInfo.getNickName());
            return vo;
        }
        //不成功
        return null;



    }

    /**
     * 退出登录
     * @param token
     */
    @Override
    public void logout(String token) {
        redisTemplate.delete(token);

    }
}




