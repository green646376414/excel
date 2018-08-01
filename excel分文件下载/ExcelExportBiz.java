package dianchou.service.about.biz;

import dianchou.common.page.PageBean;
import dianchou.common.page.PageParam;
import dianchou.facade.about.entity.ExcelExport;
import dianchou.service.about.dao.ExcelExportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Service("excelExportBiz")
public class ExcelExportBiz {
    @Autowired
    private ExcelExportDao excelExportDao;

    public List<LinkedHashMap<String, Object>> exportExcel(String sql) {
        return excelExportDao.exportExcel(sql);
    }
    public void create(ExcelExport excelExport) {
        excelExportDao.insert(excelExport);
    }

    
    public void delete(long id) {
        excelExportDao.deleteById(id);
    }


    public void update(ExcelExport excelExport) {
        excelExportDao.update(excelExport);
    }


    public ExcelExport getById(long id) {
        return (ExcelExport)excelExportDao.getById(id);
    }


    public PageBean listPage(PageParam pageParam, Map<String, Object> paramMap) {
        return excelExportDao.listPage(pageParam,paramMap);
    }
}
