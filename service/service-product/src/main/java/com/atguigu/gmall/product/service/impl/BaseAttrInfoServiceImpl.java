package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author mi
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-08-23 20:20:32
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Resource
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    BaseAttrValueMapper baseAttrValueMapper;


    /**
     * 通过id查询指定分类下的所有属性名和值
     * @param c1Id
     * @param c2Id
     * @param c3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long c1Id, Long c2Id, Long c3Id) {

        List<BaseAttrInfo> infos = baseAttrInfoMapper.getAttrInfoAndValueByCategoryId(c1Id, c2Id, c3Id);
        return infos;
    }

    /**
     * 新增修改二合一
     * @param info
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo info) {

        if(info.getId()==null){//新增
            addAttrInfoAndValue(info);
        }else {//修改
             updateAttrInfoAndValue(info);
        }
    }

    private void updateAttrInfoAndValue(BaseAttrInfo info) {
        //该属性名
        baseAttrInfoMapper.updateById(info);
        //该属性值
        //之前有现在不要的删除
        //之前有现在有的修改
        //之前没有现在有的新增
        List<BaseAttrValue> valueList = info.getAttrValueList();
        //1.先删除
        //前端提交来的集合
        List<Long> vids = new ArrayList<>();
        for (BaseAttrValue attrValuevalue : valueList) {//收集前端传来的id集合
            Long id = attrValuevalue.getId();
            if (id!=null){
                vids.add(id);
            }
        }
        //找到id对应的属性值删除不在前端集合中的属性值
        if (vids.size()>0){
            //部分删除
        baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                .eq(BaseAttrValue::getAttrId, info.getId())
                .notIn(BaseAttrValue::getId,vids));
        }else {
            baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId,info.getId()));
        }
        for (BaseAttrValue value : valueList) {
            if (value.getId()==null){//之前没有的新增
                value.setAttrId(info.getId());//回填id
                baseAttrValueMapper.insert(value);
            } else {//修改
                baseAttrValueMapper.updateById(value);
            }
        }
    }

    private void addAttrInfoAndValue(BaseAttrInfo info) {
        //保存attrInfo属性名
        baseAttrInfoMapper.insert(info);
        Long id = info.getId();

        //保存value属性值
        List<BaseAttrValue> valueList = info.getAttrValueList();//获得属性值的集合
        for (BaseAttrValue value : valueList) {
            //回填自增id维护外键
           value.setAttrId(id);
            baseAttrValueMapper.insert(value);
        }
    }
}




