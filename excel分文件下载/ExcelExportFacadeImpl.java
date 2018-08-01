package dianchou.facade.about.service.impl;

import dianchou.common.page.PageBean;
import dianchou.common.page.PageParam;
import dianchou.facade.about.entity.ExcelExport;
import dianchou.facade.about.service.ExcelExportFacade;
import dianchou.service.about.biz.ExcelExportBiz;
import dianchou.service.about.dao.ExcelExportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service("excelExportFacade")
public class ExcelExportFacadeImpl implements ExcelExportFacade {
    @Autowired
    private ExcelExportBiz excelExportBiz;


    @Override
    public List<LinkedHashMap<String, Object>> exportExcel(String sql) {
        return excelExportBiz.exportExcel(sql);
    }

    @Override
    public void create(ExcelExport excelExport) {
        excelExportBiz.create(excelExport);
    }

    @Override
    public void delete(long id) {
        excelExportBiz.delete(id);
    }

    @Override
    public void update(ExcelExport excelExport) {
        excelExportBiz.update(excelExport);
    }

    @Override
    public ExcelExport getById(long id) {
        return excelExportBiz.getById(id);
    }

    @Override
    public PageBean listPage(PageParam pageParam, Map<String, Object> paramMap) {
        return excelExportBiz.listPage(pageParam,paramMap);
    }


}
