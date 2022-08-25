package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.FileUploadService;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 品牌列表
 */
@RestController
@RequestMapping("/admin/product")
public class BaseTrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;
    @Autowired
    FileUploadService fileUploadService;

    /**
     * 分页查询品牌列表
     * @param pn
     * @param ps
     * @return
     */
    //baseTrademark/1/10
    @GetMapping("/baseTrademark/{pn}/{ps}")
    public Result getBaseTrademark(@PathVariable("pn")Long pn,
                                   @PathVariable("ps")Long ps){
        Page<BaseTrademark> pageResult =  baseTrademarkService.page(new Page<>(pn,ps));
        return Result.ok(pageResult);
    }
    ///baseTrademark/save  添加品牌

    /**
     * 添加品牌
     * @param baseTrademark
     * @return
     */
    @PostMapping("baseTrademark/save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }
    //baseTrademark/get/13

    /**
     * 通过id查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getById(@PathVariable("id")Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    //baseTrademark/update

    /**
     * 修改品牌信息
     * @param baseTrademark
     * @return
     */
    @PutMapping("/baseTrademark/update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }
    //baseTrademark/remove/12
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result delete(@PathVariable("id")Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    //文件上传
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestPart("file")MultipartFile file) throws Exception {
        //收到前端的文件
        //上传minio
        String url =  fileUploadService.fileUpload(file);
        //返回储存地址
        return Result.ok(url);
    }

    /**
     * 获得所有品牌列表
     * @return
     */
    ///admin/product/baseTrademark/getTrademarkList
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> list = baseTrademarkService.list();
        return Result.ok(list);
    }



}
