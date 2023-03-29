package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 文章编辑器模式
 * @author zqw
 * @date 2022/4/12
 */
@AllArgsConstructor
public enum EditorModelType {
    /**
     * 富文本编辑器模式
     */
    RICH_TEXT(0, "富文本编辑器"),
    /**
     * MarkDown模式
     */
    MARKDOWN_TEXT(1, "MarkDown");
    public final Integer type;
    public final String value;
}
