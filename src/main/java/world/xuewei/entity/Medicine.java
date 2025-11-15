package world.xuewei.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 药物实体
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("medicine")
public class Medicine implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 药物名字
     */
    private String medicineName;

    /**
     * 药物描述
     */
    @TableField(exist = false)
    private String medicineInfo;

    /**
     * 药物价格
     */
    private Double medicinePrice;

    /**
     * 药物图片
     */
    private String imgPath;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}