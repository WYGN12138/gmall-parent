package com.atguigu.gmall.user.api;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.atguigu.gmall.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inner/rpc/user")
public class UserApiController {

    @Autowired
    UserAddressService userAddressService;

    /**
     * 获取用户所有得收货地址列表
     * @return
     */
    @GetMapping("/addtress/list")
    public Result<List<UserAddress>> getUserAddressList(){
        //1.获取当前登录 的用户
        UserAuthInfo info = AuthUtils.getCurrentAuthInfo();
        //获取用户id
        Long userId = info.getUserId();
        //根据用户Id获取收货地址
        List<UserAddress> list = userAddressService.list(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
        return Result.ok(list);

    }
}
