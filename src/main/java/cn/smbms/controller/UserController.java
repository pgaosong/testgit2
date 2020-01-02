package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private User user;
    @Resource
    private RoleService roleService;


    //1.用户发出请求，进入登陆页面
    @RequestMapping(path={"/login","/login.html","/login.jsp"})
    public String login(){
        return "login";
    }

    //2.用户填写登录信息，进行登陆处理
    @RequestMapping(value = "/doLogin",method = RequestMethod.POST)
    public String doLogin(Model model, HttpServletRequest request, String userCode, String userPassword) {
        System.out.println("login ============ ");
        //调用service方法，进行用户匹配
        User user = userService.login(userCode, userPassword);
        if (null != user) {//登录成功
            //放入session
            request.getSession().setAttribute(Constants.USER_SESSION, user);
            //页面跳转（frame.jsp）
            //response.sendRedirect("jsp/frame.jsp");
            return "redirect:/frame";
        } else {
            //request.setAttribute("error", "用户名或密码不正确");
            request.setAttribute("error", "用户名或密码不正确");
            //页面跳转（login.jsp）带出提示信息--转发
            //request.getRequestDispatcher("login.jsp").forward(request, response);
            return "login";
        }
    }

    //3.用户登陆成功，跳转到首页
    @RequestMapping("/frame")
    public String main(HttpSession session){
        if(session.getAttribute(Constants.USER_SESSION)==null){
            return "login";
        }
        return "frame";
    }

    //用户注销
    @RequestMapping("/loginOut")
    public String loginOut(HttpSession session){
        //清除session
        session.removeAttribute(Constants.USER_SESSION);
        return "login";
    }

    //用户列表
    @RequestMapping("/userList")
    public String userList(HttpServletRequest request,String queryname,String queryUserRole,String pageIndex){
        //查询用户列表
        int queryUserRoleNew = 0;
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        System.out.println("queryUserName servlet--------"+queryname);
        System.out.println("queryUserRole servlet--------"+queryUserRoleNew);
        System.out.println("query pageIndex--------- > " + pageIndex);
        if(queryname == null){
            queryname = "";
        }
        if(queryUserRole != null && !queryUserRole.equals("")){
            queryUserRoleNew = Integer.parseInt(queryUserRole);
        }

        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                //response.sendRedirect("error.jsp");
                return "error";
            }
        }
        //总数量（表）
        int totalCount	= userService.getUserCount(queryname,queryUserRoleNew);
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        userList = userService.getUserList(queryname,queryUserRoleNew,currentPageNo, pageSize);
        request.setAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        request.setAttribute("roleList", roleList);
        request.setAttribute("queryUserName", queryname);
        request.setAttribute("queryUserRole", queryUserRoleNew);
        request.setAttribute("totalPageCount", totalPageCount);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPageNo", currentPageNo);
        //request.getRequestDispatcher("userlist.jsp").forward(request, response);
        return "userlist";
    }

    //根据用户角色查询用户信息
    @RequestMapping("/view")
    public String getUserById(HttpServletRequest request,String id){
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            User user = userService.getUserById(id);
            request.setAttribute("user", user);
            return "userview";
        }
        return "userlist";
    }

    //跳转用户修改页面
    @RequestMapping("/modify")
    public String modifyUser(HttpServletRequest request ,String id){
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            User user = userService.getUserById(id);
            request.setAttribute("user", user);
            return "usermodify";
        }
        return "userlist";
    }

    //保存用户修改信息
    @RequestMapping(value = "/modifyexe",method = RequestMethod.POST)
    public String modify(HttpServletRequest request,User user){
        user.setModifyDate(new Date());
        user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        if(userService.modify(user)){
            return "redirect:/userList";
        }
        return "usermodify";
    }


    //跳转添加页
    //Spring表单标签
    @RequestMapping("userAdd")
    public String addUser(@ModelAttribute("user")User user){
        return "useradd";
    }

    //判断用户是否存在
    @ResponseBody
    @RequestMapping(value = "ucexist",produces = "application/json;charset=UTF-8")
    public String userCodeExist(String userCode){
        //判断用户账号是否可用
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            //userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        }else{
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        return JSONArray.toJSONString(resultMap);
    }

    //添加用户
    //Spring表单标签
    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public String add(HttpServletRequest request, User user){
        System.out.println("add()================");
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        if(userService.add(user)){
            //response.sendRedirect(request.getContextPath()+"/jsp/user.do?method=query");
            return "redirect:/userList";
        }
        return "useradd";
    }


    //添加用户
    //文件上传需在RequestMappeing中指定@RequestParam(value="idPicPath",required=false)MultipartFile attach
    /*@RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public String add(HttpServletRequest request,User user, @RequestParam(value="newIdPicPath",required=false)MultipartFile attach){
        System.out.println("add()================");
        //声明上传文件路径变量
        String idPicPath = null;
        //判断文件是否为空
        if(!attach.isEmpty()) {
            //指定上传路径
            String path = "D:\\360";
            //获取原文件名
            String originalFileName = attach.getOriginalFilename();
            //获取原文件名后缀
            String suffix = FilenameUtils.getExtension(originalFileName);
            //设置上传文件最大容量
            int fileSize = 500000;
            //判断上传文件容量是否大于设置容量大小
            if (attach.getSize() > fileSize) {
                request.setAttribute("uploadFileError", "上传文件大小不得超过500kb");
                return "useradd";
            } else if (suffix.equalsIgnoreCase("jpg")
                    || suffix.equalsIgnoreCase("png")
                    || suffix.equalsIgnoreCase("jpeg")
                    || suffix.equalsIgnoreCase("pneg")){
                //得到上传文件的拼接的新文件名
                String fileName=System.currentTimeMillis()+new Random().nextInt(1000000)+"Personal."+suffix;
                //创建新文件
                File targeFile = new File(path,fileName);
                //判断新文件是否存在，如果不存在-创建并保存，如果存在-上传失败
                if(!targeFile.exists()) {
                    //创建文件
                    targeFile.mkdirs();
                }
                //保存文件
                try {
                    attach.transferTo(targeFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("uploadFileError","文件上传失败");
                    return "useradd";
                }
                idPicPath = path+File.separator+fileName;
            }else{
                request.setAttribute("uploadFileError","上传图片格式不正确");
                return "useradd";
            }
        }
        user.setCreationDate(new Date());
        user.setIdPicPath(idPicPath);
        user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        if(userService.add(user)){
            //response.sendRedirect(request.getContextPath()+"/jsp/user.do?method=query");
            return "redirect:/userList";
        }
        return "useradd";
    }*/




    //删除用户
    @ResponseBody
    @RequestMapping(value="/delUser",produces = "application/json;charset=UTF-8")
    public String delUser(String uid){
        Integer delId = 0;
        try{
            delId = Integer.parseInt(uid);
        }catch (Exception e) {
            // TODO: handle exception
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }
        //把resultMap转换成json对象输出
        return JSONArray.toJSONString(resultMap);
    }


    //获取用户角色列表
    @ResponseBody
    @RequestMapping(value="/getroleList",produces = "application/json;charset=UTF-8")
    public String getRoleList(){
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        return JSONArray.toJSONString(roleList);
    }

    //跳转修改密码页
    @RequestMapping("/modifypwd")
    public String pwdmodify(){
        return "pwdmodify";
    }


    //查询密码是否正确
    @ResponseBody
    @RequestMapping(value = "/oldPwdQuery",produces = "application/json;charset=UTF-8")
    public String getPwdByUserId(HttpServletRequest request,String oldpassword){
        Object o = request.getSession().getAttribute(Constants.USER_SESSION);
        Map<String, String> resultMap = new HashMap<String, String>();
        if(null == o ){//session过期
            resultMap.put("result", "sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){//旧密码输入为空
            resultMap.put("result", "error");
        }else{
            String sessionPwd = ((User)o).getUserPassword();
            if(oldpassword.equals(sessionPwd)){
                resultMap.put("result", "true");
            }else{//旧密码输入不正确
                resultMap.put("result", "false");
            }
        }
        //把resultMap转换成json对象输出
        return JSONArray.toJSONString(resultMap);
    }

    //修改密码
    @RequestMapping(value = "/updatePwd",method = RequestMethod.POST)
    public String updatePwd(HttpServletRequest request,String newpassword){
        Object o = request.getSession().getAttribute(Constants.USER_SESSION);
        boolean flag = false;
        if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
            flag = userService.updatePwd(((User)o).getId(),newpassword);
            if(flag){
                request.setAttribute(Constants.SYS_MESSAGE, "修改密码成功,请退出并使用新密码重新登录！");
                request.getSession().removeAttribute(Constants.USER_SESSION);//session注销
            }else{
                request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
            }
        }else{
            request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
        }
        //request.getRequestDispatcher("pwdmodify.jsp").forward(request, response);
        return "pwdmodify";
    }





    /**
     * 局部异常处理--只能处理当前控制器内的异常，不能处理其他控制器内发生的异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    public String handlerException(Exception e,HttpServletRequest request){
        request.setAttribute("e",e);
        return "error";
    }

    @RequestMapping("exception")
    public String testException(Integer id){
        if(id==1){
            System.out.println("user:空指针异常");
            throw new NullPointerException("空指针异常");
        }else if(id==2){
            System.out.println("user:算数异常");
            throw new ArithmeticException("算数异常");
        }
        return null;
    }

}
