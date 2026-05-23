package com.mojian.controller.article;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.annotation.AccessLimit;
import com.mojian.entity.SysCategory;
import com.mojian.service.ArticleService;
import com.mojian.vo.article.ArchiveListVo;
import com.mojian.vo.article.ArticleDetailVo;
import com.mojian.vo.article.ArticleListVo;
import com.mojian.vo.article.CategoryListVo;
import com.mojian.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "门户-文章管理")
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/list")
    @Operation(summary = "获取文章列表")
    public Result<IPage<ArticleListVo>> getArticleList(Integer tagId, Integer categoryId,String keyword) {
        return Result.success(articleService.getArticleList(tagId,categoryId,keyword));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "获取文章详情")
    public Result<ArticleDetailVo> getArticleDetail(@PathVariable Long id) {
        return Result.success(articleService.getArticleDetail(id));
    }

    @GetMapping("/archive")
    @Operation(summary = "获取归档")
    public Result<List<ArchiveListVo>> getArticleArchive() {
        return Result.success(articleService.getArticleArchive());
    }

    @GetMapping("/categories")
    @Operation(summary = "获取分类")
    public Result<List<CategoryListVo>> getArticleCategories() {
        return Result.success(articleService.getArticleCategories());
    }

    @GetMapping("/categorie-all")
    @Operation(summary = "获取所有分类")
    public Result<List<SysCategory>> getCategoryAll() {
        return Result.success(articleService.getCategoryAll());
    }


    @GetMapping("/getCarousels")
    @Operation(summary = "获取轮播文章")
    public Result<List<ArticleListVo>> getCarouselArticle() {
        return Result.success(articleService.getCarouselArticle());
    }

    @GetMapping("/getRecommends")
    @Operation(summary = "获取推荐文章")
    public Result<List<ArticleListVo>> getRecommendArticle() {
        return Result.success(articleService.getRecommendArticle());
    }

    @SaCheckLogin
    @GetMapping("/like/{id}")
    @AccessLimit(time = 5, count = 1)
    @Operation(summary = "点赞文章")
    public Result<Boolean> like(@PathVariable Long id) {
        return Result.success(articleService.like(id));
    }
}
