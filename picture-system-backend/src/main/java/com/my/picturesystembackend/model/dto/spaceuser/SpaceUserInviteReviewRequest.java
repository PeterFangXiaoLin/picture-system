package com.my.picturesystembackend.model.dto.spaceuser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 处理空间成员邀请请求体。
 */
@Data
@ApiModel(value = "SpaceUserInviteReviewRequest", description = "处理空间成员邀请请求体")
public class SpaceUserInviteReviewRequest implements Serializable {

    /**
     * 空间成员记录 id
     */
    @ApiModelProperty(value = "空间成员记录 id", required = true)
    private Long id;

    /**
     * 邀请确认状态：1-接受；2-拒绝
     */
    @ApiModelProperty(value = "邀请确认状态：1-接受；2-拒绝", required = true)
    private Integer inviteStatus;

    private static final long serialVersionUID = 1L;
}
