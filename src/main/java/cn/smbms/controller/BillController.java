package cn.smbms.controller;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.provider.ProviderService;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class BillController {

    @Resource
    private Bill bill;
    @Resource
    private BillService billService;
    @Resource
    private ProviderService providerService;

    //查询订单列表
    @RequestMapping("/billList")
    public String query(HttpServletRequest request,String queryProductName,String queryProviderId,String queryIsPayment){
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("","");
        request.setAttribute("providerList", providerList);

        if(StringUtils.isNullOrEmpty(queryProductName)){
            queryProductName = "";
        }

        List<Bill> billList = new ArrayList<Bill>();
        if(StringUtils.isNullOrEmpty(queryIsPayment)){
            bill.setIsPayment(0);
        }else{
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if(StringUtils.isNullOrEmpty(queryProviderId)){
            bill.setProviderId(0);
        }else{
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        request.setAttribute("billList", billList);
        request.setAttribute("queryProductName", queryProductName);
        request.setAttribute("queryProviderId", queryProviderId);
        request.setAttribute("queryIsPayment", queryIsPayment);
        //request.getRequestDispatcher("billlist.jsp").forward(request, response);
        return "billlist";
    }

    //查询订单信息-根据订单id
    @RequestMapping("/views")
    public String getBillById(HttpServletRequest request,String id){
        if(!StringUtils.isNullOrEmpty(id)){
            bill = billService.getBillById(id);
            request.setAttribute("bill", bill);
            //request.getRequestDispatcher(url).forward(request, response);
            return "billview";
        }
        return "billlist";
    }


    //跳转修改订单页
    @RequestMapping("/modifyBill")
    public String modifyBill(HttpServletRequest request,String id){
        if(!StringUtils.isNullOrEmpty(id)){
            bill = billService.getBillById(id);
            request.setAttribute("bill", bill);
            //request.getRequestDispatcher(url).forward(request, response);
            return "billmodify";
        }
        return "billlist";
    }

    //查看供应商列表
    @ResponseBody
    @RequestMapping(value = "/getproviderlist",produces = "application/json;charset=UTF-8")
    public String getProviderlist(){
        System.out.println("getproviderlist ========================= ");
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("","");
        //把providerList转换成json对象输出
        return JSONArray.toJSONString(providerList);
    }

    //修改订单
    @RequestMapping(value = "/modifysave",method = RequestMethod.POST)
    public String modify(Bill bill){
        System.out.println("modify===============");
        bill.setModifyDate(new Date());
        boolean flag = false;
        flag = billService.modify(bill);
        if(flag){
            //response.sendRedirect(request.getContextPath()+"/jsp/bill.do?method=query");
            return "redirect:/billList";
        }
        return "billmodify";
    }

    //跳转添加页
    @RequestMapping("billAdd")
    public String addBill(){
        return "billadd";
    }


    //添加订单
    @RequestMapping(value = "/addBill",method = RequestMethod.POST)
    public String add(Bill bill){
        bill.setCreationDate(new Date());
        boolean flag = false;
        flag = billService.add(bill);
        System.out.println("add flag -- > " + flag);
        if(flag){
            //response.sendRedirect(request.getContextPath()+"/jsp/bill.do?method=query");
            return "redirect:/billList";
        }
        return "billadd";
    }

    //删除订单
    @ResponseBody
    @RequestMapping(value = "/delbill",produces = "application/json;charset=UTF-8")
    public String delBill(String id){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(id)){
            boolean flag = billService.deleteBillById(id);
            if(flag){//删除成功
                resultMap.put("delResult", "true");
            }else{//删除失败
                resultMap.put("delResult", "false");
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        //把resultMap转换成json对象输出
        return JSONArray.toJSONString(resultMap);
    }
}
