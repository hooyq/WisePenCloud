package com.oriole.wisepen.document.api.domain.dto.res;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文档上传初始化响应
 */
@Data
public class DocumentUploadInitResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 文档唯一 ID */
    private String documentId;

    /** OSS 预签名直传 PUT URL（flashUploaded=true 时为 null） */
    private String putUrl;

    /** 直传时需附加在 PUT 请求 Header 中的 x-oss-callback 字符串 */
    private String callbackHeader;

    /** 文件在 OSS 中的 ObjectKey */
    private String objectKey;

    /** 是否触发秒传（true 表示文件已存在，无需上传） */
    private Boolean flashUploaded;
}
