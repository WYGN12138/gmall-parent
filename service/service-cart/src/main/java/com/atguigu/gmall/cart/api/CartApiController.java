package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inner/rpc/cart")
public class CartApiController {
    @Autowired
    CartService cartService;
    /**
     * 把商品添加到购物车
     * @param skuId
     * @param num
     * @return  把那个商品添加到了购物车
     * ,
     * @RequestHeader(value=SysRedisConst.USERID_HEADER,required = false)String userId,
     * @RequestHeader(value = SysRedisConst.USERTEMPID_HEADER,required = false) String userTempId
     */
    @GetMapping("/addToCart")
    public Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                                     @RequestParam("num") Integer num){

        //老请求 RequestContextHolder 绑定在当前线程
        //1、清除dns缓存 ipconfig /flushdns
        //2、验证ping gmall.com。 确保hosts文件是修改成功
        //3、vmnet8网卡不要配置错


        SkuInfo skuInfo = cartService.addToCart(skuId,num);

        return Result.ok(skuInfo);
    }


    /**
     * 删除购物车中选中的商品
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked(){
        String cartKey = cartService.determinCartKey();
        cartService.deleteChecked(cartKey);
        return Result.ok();
    }


    @GetMapping("/checked/list")
    public Result<List<CartInfo>> getChecked(){
        String cartKey = cartService.determinCartKey();//决定用哪个键
        List<CartInfo> checkedItems = cartService.getCheckedItems(cartKey);//获取勾选的项目
        return Result.ok(checkedItems);
    }

}
