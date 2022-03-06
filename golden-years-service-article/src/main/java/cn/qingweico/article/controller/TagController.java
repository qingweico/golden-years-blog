package cn.qingweico.article.controller;

import cn.qingweico.api.controller.article.TagControllerApi;
import cn.qingweico.article.service.TagService;
import cn.qingweico.result.GraceJsonResult;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2022/3/5
 */
public class TagController implements TagControllerApi {

    @Resource
    private TagService tagService;

    @Override
    public GraceJsonResult query() {
        return GraceJsonResult.ok(tagService.getTagList());
    }
    // TODO


    @Override
    public GraceJsonResult addPersonalTag() {
        return null;
    }


    @Override
    public GraceJsonResult deletePersonalTag() {
        return null;
    }
}
