 /**
     * 
     * @Title: impExcel 
     * @Description: 批量导入客户信息
     * @param @param request
     * @param @param response
     * @param @return
     * @return String
     * @throws
     */
    @RequestMapping("impExcel")
    //@ResponseBody
    public String impExcel(MultipartHttpServletRequest request,HttpServletResponse response,HttpSession session){
    	//读取excel
    			try {
    				MultipartFile file = request.getFile("excelFile");
    				 String name=file.getOriginalFilename();
    				 
    				 
    				 String path = ResourceUtils.getURL("classpath:").getPath();
    				 File newFile = new File(path,name);
    				 if(newFile.exists()){
    					 Boolean result=newFile.delete();
    					 System.out.println(result);
    				 }
    			      file.transferTo(newFile);
    			      
    			      
    			      String filePath=path+name;
    			    //判断是否为excel类型文件
    			        if(!filePath.endsWith(".xls")&&!filePath.endsWith(".xlsx")){
    			            System.out.println("文件不是excel类型");
    			        }
    			        
    			        FileInputStream fis =null;
    			        Workbook wookbook = null;
    			        try{
    			        	 //获取一个绝对地址的流
    			            fis = new FileInputStream(filePath);
    			            if(filePath.endsWith(".xls")) {
    			            	//2003版本的excel，用.xls结尾
    			                wookbook = new HSSFWorkbook(fis);//得到工作簿
    			            }else {
    			            	//2007版本的excel，用.xlsx结尾
    			                wookbook = new XSSFWorkbook(fis);//得到工作簿
    			            }
    			             
    			        } catch (Exception ex){
    			        	ex.printStackTrace();
    			        }
    			        
    			        //得到一个工作表
    			        Sheet sheet = wookbook.getSheetAt(0);
    			        
    			        //获得表头
    			        Row rowHead = sheet.getRow(0);
    			    
    			        //获得数据的总行数
    			        int totalRowNum = sheet.getLastRowNum();
    			        //要获得属性
    			       // String name = "";
    			        int latitude = 0;
    			        
    			       //获得所有数据
    			        for(int i = 1 ; i <= totalRowNum ; i++)
    			        {
    			            //获得第i行对象
    			            Row row = sheet.getRow(i);
    			           //高等数学A----96.0----6.0----必修----86.0----57.0----66.0----2014-2015-6
    			            System.out.println(row.getCell(0)+"----"+row.getCell(1)+"----"+row.getCell(2)+"----"+row.getCell(3)+"----"
    	    			            +row.getCell(4)+"----"+row.getCell(5)+"----"+row.getCell(6)+"----"+row.getCell(7));
    			            //获得获得第i行第0列的 String类型对象
    			            //Cell cell = row.getCell((short)0);
    			            Score score=new Score();
    			             
    						score.setCname(row.getCell(0).toString());
    						score.setTimes(convert(row.getCell(1)));
    						score.setCredit(convert(row.getCell(2)));
    						score.setSort(row.getCell(3).toString());
    						score.setPscore(convert(row.getCell(4)));
    						score.setQimo(convert(row.getCell(5)));
    						score.setZping(convert(row.getCell(6)));
    						//score.setXuehao(row.getCell(7).toString());
    						score.setXueqi(row.getCell(7).toString());		
    						scoreService.add(score);
    			            
    			            
    			        }
    			} catch (Exception e) {
    				e.printStackTrace();	
    			}

    			//session.setAttribute("ses",1);
		return "redirect:/score/fen?ses=1";
    }
    
    
 public static int convert(Object o) {
	 if(o!=null&&StringUtils.isNotBlank(o.toString())) { 
		return (int)Double.parseDouble(o.toString());
	 }else {
		 return 0;
	 }
 }