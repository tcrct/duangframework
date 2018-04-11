package com.duangframework.ext.report;

import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Mapping;
import com.duangframework.core.common.Const;
import com.duangframework.mvc.core.BaseController;

/**
 * @author Created by laotang
 * @date createed in 2018/2/6.
 */
@Controller
@Mapping(value = "/{flag}"+ Const.REPORT_MAPPING_KEY, desc = "框架信息报告")
public class ReportController extends BaseController {

    @Import
    private ReportService reportService;

    /**
     * 返回所有原始action记录
     */
//    public void actions() {
//        try {
//            returnSuccessJson(reportService.actions());
//        } catch (Exception e) {
//            returnFailJson(e);
//        }
//    }

    /**
     * 返回树型action记录， 以controller mapping value为key,
     */
//    public void treeActions() {
//        try {
//            returnSuccessJson(reportService.treeActions());
//        } catch (Exception e) {
//            returnFailJson(e);
//        }
//    }

    /**
     * 返回系统信息
     */
    public void info() {
        try {
            returnSuccessJson(reportService.info());
        } catch (Exception e) {
            returnFailJson(e);
        }
    }
}
