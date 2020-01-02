package cn.smbms.controller;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.tools.Constants;
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
public class ProviderController {

    @Resource
    private Provider provider;
    @Resource
    private ProviderService providerService;

    //查看供应商列表
    @RequestMapping("/providerList")
    public String query(HttpServletRequest request,String queryProName, String queryProCode){
        if(StringUtils.isNullOrEmpty(queryProName)){
            queryProName = "";
        }
        if(StringUtils.isNullOrEmpty(queryProCode)){
            queryProCode = "";
        }
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList(queryProName,queryProCode);
        request.setAttribute("providerList", providerList);
        request.setAttribute("queryProName", queryProName);
        request.setAttribute("queryProCode", queryProCode);
        //request.getRequestDispatcher("providerlist.jsp").forward(request, response);
        return "providerlist";
    }

    //查看供应商信息
    @RequestMapping("/viewp")
    public String getProviderById(HttpServletRequest request,String id) {
        if(!StringUtils.isNullOrEmpty(id)){
            provider = providerService.getProviderById(id);
            request.setAttribute("provider", provider);
            //request.getRequestDispatcher(url).forward(request, response);
            return "providerview";
        }
        return "providerlist";
    }

    //跳转修改页
    @RequestMapping("/modifyProvider")
    public String modifyProvider(HttpServletRequest request,String id){
        if(!StringUtils.isNullOrEmpty(id)){
            provider = providerService.getProviderById(id);
            request.setAttribute("provider", provider);
            //request.getRequestDispatcher(url).forward(request, response);
            return "providermodify";
        }
        return "providerlist";
    }

    //修改供应商信息
    @RequestMapping(value = "/modifySave",method = RequestMethod.POST)
    public String modify(HttpServletRequest request,Provider provider){
        provider.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());
        if(providerService.modify(provider)){
            //response.sendRedirect(request.getContextPath()+"/jsp/provider.do?method=query");
            System.out.println("修改成功");
            return "redirect:/providerList";
        }
        System.out.println("修改失败");
        return "providermodify";
    }

    //跳转添加页
    @RequestMapping("/providerAdd")
    public String providerAdd(){
        return "provideradd";
    }

    //添加供应商
    @RequestMapping("/addProvider")
    public String add(Provider provider){
        provider.setCreationDate(new Date());
        boolean flag = false;
        flag = providerService.add(provider);
        if(flag){
            //response.sendRedirect(request.getContextPath()+"/jsp/provider.do?method=query");
            return "redirect:providerList";
        }
        return "provideradd";
    }

    //删除供应商
    @ResponseBody
    @RequestMapping(value = "/delprovider",produces = "application/json;charset=UTF-8")
    public String delProvider(String id){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(id)){
            int flag = providerService.deleteProviderById(id);
            if(flag == 0){//删除成功
                resultMap.put("delResult", "true");
            }else if(flag == -1){//删除失败
                resultMap.put("delResult", "false");
            }else if(flag > 0){//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        //把resultMap转换成json对象输出
        return JSONArray.toJSONString(resultMap);
    }

}
