package com.mojian.utils;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.Arrays;

/**
 * Markdown 转 HTML 工具类
 * 使用 flexmark-java 库进行转换
 */
public class MarkdownUtils {

    /**
     * Markdown 解析器（单例）
     */
    private static final Parser PARSER;
    
    /**
     * HTML 渲染器（单例）
     */
    private static final HtmlRenderer RENDERER;

    static {
        // 配置 Markdown 解析选项
        MutableDataSet options = new MutableDataSet();
        
        // 启用扩展：表格、删除线等
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create()
        ));
        
        // 配置 HTML 渲染选项
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        
        PARSER = Parser.builder(options).build();
        RENDERER = HtmlRenderer.builder(options).build();
    }

    /**
     * 将 Markdown 文本转换为 HTML
     *
     * @param markdown Markdown 格式的文本
     * @return HTML 格式的文本
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        
        Node document = PARSER.parse(markdown);
        return RENDERER.render(document);
    }

    /**
     * 将 Markdown 文本转换为 HTML（带安全清理）
     * 移除潜在的 XSS 攻击代码
     *
     * @param markdown Markdown 格式的文本
     * @return 清理后的 HTML 格式文本
     */
    public static String toSafeHtml(String markdown) {
        String html = toHtml(markdown);
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        // 基本的安全清理
        return html
                .replaceAll("<script[^>]*>.*?</script>", "")  // 移除 script 标签
                .replaceAll("javascript:", "")                  // 移除 javascript: 协议
                .replaceAll("on\\w+\\s*=", "");                 // 移除事件处理器
    }
}
