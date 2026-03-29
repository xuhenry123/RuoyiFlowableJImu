package org.dromara.report.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.report.domain.vo.ReportVo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    @GetMapping("/list")
    public R<List<ReportVo>> list() {
        List<ReportVo> list = new ArrayList<>();
        ReportVo vo = new ReportVo();
        vo.setId("1");
        vo.setCode("demo-report");
        vo.setName("示例报表");
        vo.setType("chart");
        vo.setStatus("1");
        list.add(vo);
        return R.ok(list);
    }

    @GetMapping("/{id}")
    public R<ReportVo> getInfo(@PathVariable String id) {
        ReportVo vo = new ReportVo();
        vo.setId(id);
        vo.setCode("demo-report");
        vo.setName("示例报表");
        vo.setType("chart");
        vo.setStatus("1");
        return R.ok(vo);
    }

    @GetMapping("/preview/{id}")
    public void preview(@PathVariable String id, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("<html><body><h1>报表预览</h1><p>报表ID: " + id + "</p></body></html>");
    }

    @GetMapping("/design")
    public R<String> getDesignUrl() {
        return R.ok("http://jimureport.baike888.com/index.html");
    }
}
