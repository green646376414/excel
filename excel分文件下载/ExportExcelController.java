package dianchou.web.boss.controller.excelport;

import dianchou.common.dictionary.entity.KnBcDictionary;
import dianchou.common.dictionary.service.KnBcDictionaryFacade;
import dianchou.common.page.PageBean;
import dianchou.common.utils.StringUtils;
import dianchou.facade.about.entity.ExcelExport;
import dianchou.facade.about.service.ExcelExportFacade;
import dianchou.web.boss.common.controller.WebBossBaseController;
import dianchou.web.boss.utils.DeleteDirectory;
import dianchou.web.boss.utils.ExportUtil;
import dianchou.web.boss.utils.ZipUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/pms/excelport")
public class ExportExcelController extends WebBossBaseController
{

    private static final Log log = LogFactory.getLog(ExportExcelController.class);
    @Autowired
    private ExcelExportFacade excelExportFacade;

    @Autowired
    private KnBcDictionaryFacade knBcDictionaryFacade;



    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list() {
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            String status = getString("status");
            String name = getString("name");

            paramMap.put("status", status);
            paramMap.put("name", name);
            PageBean pageBean = excelExportFacade.listPage(getPageParam(), paramMap);
            this.pushData(SEARCH, paramMap);
            this.pushData(PAGE, pageBean);
            return "/admin/excelport/excelExportList";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("== createUI exception ", e);
            return operateError("服务异常:" + e.getClass().getSimpleName());
        }
    }

    @RequestMapping(value = "/add/ui")
    public String addUI() {
        try {
            return "/admin/excelport/excelExportAdd";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("== createUI exception ", e);
            return operateError("服务异常:" + e.getClass().getSimpleName());
        }

    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String create(ExcelExport excelExport) {
        try {
           /* BASE64Encoder encoder=new BASE64Encoder();
            byte[] textByte = excelExport.getSql_().getBytes("UTF-8");
            String sql=encoder.encode(textByte);
            excelExport.setSql_(sql);*/
            excelExportFacade.create(excelExport);
            return operateSuccess("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("== create exception ", e);
            return operateError("保存失败");
        }
    }

    @RequestMapping(value = "/edit/ui")
    public String editUI(Long id) {
        try {
            ExcelExport excelExport = excelExportFacade.getById(id);
            this.pushData("excelExport", excelExport);
            return "/admin/excelport/excelExportEdit";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("== editUI exception ", e);
            return operateError("服务异常:" + e.getClass().getSimpleName());
        }

    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String edit(ExcelExport excelExport) {
        try {
            excelExportFacade.update(excelExport);
            return operateSuccess("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("== create exception ", e);
            return operateError("保存失败");
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(long id) {
        try {
            excelExportFacade.delete(id);
            return operateSuccess("操作成功");
        } catch (Exception e) {
            log.error("==" + this.getClass().getName(), e);
            return this.operateError("后台异常");
        }
    }

    @RequestMapping(value = "/dataexport", method = RequestMethod.GET)
    public String dataexport(){
        Map map=new HashMap();
        map.put("dicKey_export_excel","export_excel_");
        //List<KnBcDictionary> list=knBcDictionaryFacade.listBy(map);
        //this.pushData("list",list);
        return "admin/excelport/excelport";
    }


    @RequestMapping(value = "/port", method = RequestMethod.GET)
    public String port(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            String id = request.getParameter("id");
            ExcelExport excelExport=excelExportFacade.getById(Long.parseLong(id));

            //String sql = request.getParameter("sql");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            //String key = request.getParameter("key");
            //String limit = request.getParameter("limit");
            if(org.apache.commons.lang3.StringUtils.isNotBlank(excelExport.getSql_())){
                if(excelExport.getSql_().contains("@startDate")){
                    if(StringUtils.isNotBlank(startDate)){
                        log.error("================================sql包含了，开始时间但是没填开始时间");
                    }
                }
                if(excelExport.getSql_().contains("@endDate")){
                    if(StringUtils.isNotBlank(startDate)){
                        log.error("================================sql包含了，结束时间但是没填结束时间");
                    }
                }
            }
            if(StringUtils.isNotBlank(excelExport.getTitle())){
                if(excelExport.getTitle().contains("，")){
                    excelExport.getTitle().replace("，",",");
                }
                String[] titles = excelExport.getTitle().split(",");
               /* BASE64Decoder decoder = new BASE64Decoder();
                String sql=new String(decoder.decodeBuffer(excelExport.getSql_()), "UTF-8");*/
                getDate(excelExport.getName_().trim(),excelExport.getSql_(), titles,startDate,endDate,excelExport.getPage());
            }else{
                log.error("========标题栏不能为空");
            }
        }catch (Exception e)
        {
           // log.error("*********导出报错*******"+e);
            e.printStackTrace();
        }

        return null;
    }

    private void getDate(String name,String sql,String[] titles ,String startDate ,String endDate,Integer limitNum) throws Exception{

            if(org.apache.commons.lang.StringUtils.isNotBlank(sql)){
                if(sql.contains("@startDate")){
                    String realSql="";
                    StringBuilder sb=new StringBuilder();
                    sb.append("'");
                    sb.append(startDate);
                    sb.append(" 00:00:00'");
                    realSql=sql.replace("@startDate",sb.toString());
                    sql=realSql;
                }
                if(sql.contains("@endDate")){
                    String realSql="";
                    StringBuilder sb=new StringBuilder();
                    sb.append("'");
                    sb.append(endDate);
                    sb.append(" 23:59:59'");
                    realSql=sql.replace("@endDate",sb.toString());
                    sql=realSql;
                }
            }
            log.info("=======================sql========================================================================="+sql);
            List<LinkedHashMap<String, Object>> list = excelExportFacade.exportExcel(sql);


            limitNum=limitNum!=null?limitNum:0;
            int listSize=list!=null&&list.size()>0?list.size():0;
            //如果分页参数不为0
            //吧excel分割成多个excel，然后在打包下载
            if(limitNum>0){
                if(listSize>limitNum){
                    List<LinkedHashMap<String, Object>> tempList=new ArrayList<LinkedHashMap<String, Object>>();
                    //int times=listSize/limitNum;//循环添加的次数
                    int ii=1;
                    for (int j = 0; j < list.size(); j++) {
                        tempList.add(list.get(j));
                        if(j==(limitNum*ii)){
                            newExcel(titles,tempList,name+ii+".xls");
                            tempList=new ArrayList<LinkedHashMap<String, Object>>();
                            ii++;
                        }
                    }
                    //最后一次整除之后,剩下的数据
                    newExcel(titles,tempList,name+ii+".xls");

                    //压缩文件
                    String pathStr = request.getServletContext().getRealPath("");
                    File file = new File(pathStr + File.separator + "tempZip");
                    if (!file.exists() && !file.isDirectory()) {
                        //当文件夹不存在或者不是文件夹时创建文件夹
                        file.mkdir();
                    }
                    File f=new File(file+ File.separator +name+getFileNameNew()+".zip");
                    FileOutputStream fos1 = new FileOutputStream(f);
                    ZipUtils.toZip(pathStr + File.separator + "tempExcel", fos1,true);

                    //downloadFile(f,response,false);
                    downloadFile(f,response,true);
                    DeleteDirectory.deleteDir(new File(pathStr + File.separator + "tempExcel"));
                }else{//直接单个excel下载
                    getDateSigle(name,titles,list);
                }
            }else{//直接单个excel下载
                getDateSigle(name,titles,list);
            }

           log.info("*****************生成文件成功*****************");

    }

    //new  一个excel文件
    private void newExcel(String[] titles ,List list,String fileName) throws Exception{
        // 创建一个workbook 对应一个excel应用文件
        XSSFWorkbook workBook = new XSSFWorkbook();
        //FileOutputStream outputStream=new FileOutputStream(fileName);
        // 在workbook中添加一个sheet,对应Excel文件中的sheet
        XSSFSheet sheet = workBook.createSheet("sheet");
        ExportUtil exportUtil = new ExportUtil(workBook, sheet);
        XSSFCellStyle headStyle = exportUtil.getHeadStyle();
        XSSFCellStyle bodyStyle = exportUtil.getBodyStyle();
        // 构建表头
        XSSFRow headRow = sheet.createRow(0);
        XSSFCell cell = null;
        for (int i = 0; i < titles.length; i++) {
            cell = headRow.createCell(i);
            cell.setCellStyle(headStyle);
            cell.setCellValue(titles[i]);
        }
        // 构建表体数据
        if (list != null && list.size() > 0) {
            for (int j = 0; j < list.size(); j++) {
                XSSFRow bodyRow = sheet.createRow(j + 1);
                LinkedHashMap linkedHashMap = (LinkedHashMap) list.get(j);

                Iterator<Map.Entry> iterator = linkedHashMap.entrySet().iterator();
                //while(iterator.hasNext())
                for (int i = 0; i < titles.length; i++) {
                    Map.Entry entry = iterator.next();
                    //System.out.println(entry.getKey()+":"+entry.getValue());
                    cell = bodyRow.createCell(i);
                    cell.setCellStyle(bodyStyle);
                    if (entry.getValue() != null) {
                        cell.setCellValue(entry.getValue().toString());
                    }
                }
            }
        }
        //获取项目的webroot路径
        String pathStr = request.getServletContext().getRealPath("");

        File file = new File(pathStr + File.separator + "tempExcel");
        if (!file.exists() && !file.isDirectory()) {//当文件夹不存在或者不是文件夹时创建文件夹
            file.mkdir();
        }
        FileOutputStream fos = new FileOutputStream(file + File.separator + fileName);
        workBook.write(fos);
        fos.flush();
        fos.close();

    }

    private void getDateSigle(String name,String[] titles ,List list) throws Exception{
        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet("sheet");
        ExportUtil exportUtil = new ExportUtil(workBook, sheet);
        XSSFCellStyle headStyle = exportUtil.getHeadStyle();
        XSSFCellStyle bodyStyle = exportUtil.getBodyStyle();
        // 构建表头
        XSSFRow headRow = sheet.createRow(0);
        XSSFCell cell = null;
        for (int i = 0; i < titles.length; i++) {
            cell = headRow.createCell(i);
            cell.setCellStyle(headStyle);
            cell.setCellValue(titles[i]);
        }
        // 构建表体数据
        if (list != null && list.size() > 0) {
            for (int j = 0; j < list.size(); j++) {
                XSSFRow bodyRow = sheet.createRow(j + 1);
                LinkedHashMap linkedHashMap = (LinkedHashMap) list.get(j);

                Iterator<Map.Entry> iterator = linkedHashMap.entrySet().iterator();
                //while(iterator.hasNext())
                for (int i = 0; i < titles.length; i++) {
                    Map.Entry entry = iterator.next();
                    //System.out.println(entry.getKey()+":"+entry.getValue());
                    cell = bodyRow.createCell(i);
                    cell.setCellStyle(bodyStyle);
                    if (entry.getValue() != null) {
                        cell.setCellValue(entry.getValue().toString());
                    }
                }
            }
        }
            //response.setContentType("multipart/form-data");
            //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
            String file=name+".xls";
            response.setHeader("Content-Disposition", "attachment;fileName="+new String(file.getBytes("UTF-8"), "ISO-8859-1"));
            ServletOutputStream out = response.getOutputStream();
            workBook.write(out);
            out.close();
            out.flush();
    }

    /**
     * 下载文件
     *
     * @param file
     * @param response
     */
    private void downloadFile(File file, HttpServletResponse response, boolean isDelete) throws Exception{

// 以流的形式下载文件。
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
// 清空response
            response.reset();
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition","attachment;filename=" + new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"));

            toClient.write(buffer);
            if (isDelete) {
                file.delete(); // 是否将生成的服务器端文件删除
            }
    }


    private String getFileNameNew() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return fmt.format(new Date());
    }
}
