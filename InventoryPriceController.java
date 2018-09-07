package com.walmart.controller.mongodb;

import com.baomidou.mybatisplus.plugins.Page;
import com.walmart.common.bean.Rest;
import com.walmart.controller.FileUploadController;
import com.walmart.utils.ExportUtil;
import com.walmart.utils.GetSingleExcelUtil;
import com.walmart.utils.ReflectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.walmart.common.controller.SuperController;
import com.walmart.entity.mongodb.InventoryPrice;
import com.walmart.service.mongodb.InventoryPriceService;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户控制器
 * @author Gaojun.Zhou
 * @date 2016年12月13日 上午10:22:41
 */
@Controller
@RequestMapping("/mongo/inventoryPrice")
public class InventoryPriceController extends SuperController{
	public static final Logger log = LoggerFactory.getLogger(InventoryPriceController.class);
	@Autowired private InventoryPriceService inventoryPriceService;

	
	/**
	 * 分页查询用户
	 */
	@RequiresPermissions("inventoryPriceList")
    @RequestMapping("/list/{pageNumber}")  
    public  String list(@PathVariable Integer pageNumber,@RequestParam(defaultValue="15") Integer pageSize,String storeNbr,String upcNbr,String itemNbr,String search,Model model){
		if(StringUtils.isNotBlank(search)){
			model.addAttribute("search", search);
		}
		InventoryPrice inventoryPrice=new InventoryPrice();

		if(StringUtils.isNotBlank(storeNbr)){
			model.addAttribute("storeNbr", storeNbr);
			inventoryPrice.setStoreNbr(Integer.parseInt(storeNbr));
		}
		if(StringUtils.isNotBlank(upcNbr)){
			model.addAttribute("upcNbr", upcNbr);
			inventoryPrice.setUpcNbr(Long.parseLong(upcNbr));
		}
		if(StringUtils.isNotBlank(itemNbr)){
			model.addAttribute("itemNbr", itemNbr);
			inventoryPrice.setItemNbr(Integer.parseInt(itemNbr));
		}
    	Page<InventoryPrice> page = getPage(pageNumber,pageSize);
    	model.addAttribute("pageSize", pageSize);

		Page<InventoryPrice> pageData = inventoryPriceService.getPage(page,inventoryPrice,search);
    	model.addAttribute("pageData", pageData);
    	return "mongo/inventoryPrice/list";
    }


	@RequestMapping("/export")
	public  void export(String storeNbr,String upcNbr,String itemNbr){
		InventoryPrice inventoryPrice=new InventoryPrice();
		if(StringUtils.isNotBlank(storeNbr)){
			inventoryPrice.setStoreNbr(Integer.parseInt(storeNbr));
		}
		if(StringUtils.isNotBlank(upcNbr)){
			inventoryPrice.setUpcNbr(Long.parseLong(upcNbr));
		}
		if(StringUtils.isNotBlank(itemNbr)){
			inventoryPrice.setItemNbr(Integer.parseInt(itemNbr));
		}
		List<InventoryPrice> list = inventoryPriceService.findListByPara(inventoryPrice);
		SimpleDateFormat sf =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		GetSingleExcelUtil.getSingleExcel(InventoryPrice.class, "inventory_price"+sf.format(new Date()), list,response);
	}

}
