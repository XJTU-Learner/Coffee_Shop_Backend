package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 广告轮播图表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_advertisement")
public class Advertisement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 轮播图名称
     */
    @TableField("image_name")
    private String imageName;

    /**
     * 轮播图路径
     */
    @TableField("image_path")
    private String imagePath;

    /**
     * 说明
     */
    @TableField("description")
    private String description;

    /**
     * 轮播图类型 1=首页轮播图 2=菜单页轮播图 3=积分商城页面轮播图 4=分享页面生成美图
     */
    @TableField("type")
    private Integer type;

    /**
     * 排序号
     */
    @TableField("sort_number")
    private Integer sortNumber;

    /**
     * 点击轮播图跳转的链接
     */
    @TableField("image_link_url")
    private String imageLinkUrl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
